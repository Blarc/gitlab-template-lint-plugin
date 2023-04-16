package com.github.blarc.gitlab.template.lint.plugin.gitlab

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.gitlab.http.HttpClientFactory
import com.github.blarc.gitlab.template.lint.plugin.notifications.Notification
import com.github.blarc.gitlab.template.lint.plugin.notifications.sendNotification
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
open class Gitlab(val project: Project) {
    var gitlabUrl: String = "https://gitlab.com"
    var gitlabToken: String = "invalid"
    private val allowSelfSignedTls: Boolean = false
    private val httpClient: OkHttpClient
    private val json: Json

    init {
        val httpClientFactory: HttpClientFactory = HttpClientFactory.instance
        httpClient = if (allowSelfSignedTls) httpClientFactory.insecureHttpClient else httpClientFactory.httpClient
        json = Json { ignoreUnknownKeys = true }
    }

    private fun prepareRequest(
        gitlabUrl: String = this.gitlabUrl,
        gitlabToken: String = this.gitlabToken,
        urlSuffix: String
    ): Request.Builder {
        return Request.Builder()
            .url("${gitlabUrl}${urlSuffix}")
            .addHeader("Private-Token", gitlabToken)
    }

    fun getVersion(
        gitlabUrl: String = this.gitlabUrl,
        gitlabToken: String = this.gitlabToken,
    ): CompletableFuture<GitlabVersion?> {
        val request: Request = prepareRequest(gitlabUrl, gitlabToken, "/version")
            .get()
            .build()

        return makeRequest(request, false) {
            json.decodeFromString<GitlabVersion>(it)
        }
    }

    fun searchProjectId(
        gitlabUrl: String = this.gitlabUrl,
        gitlabToken: String = this.gitlabToken,
        remoteUrl: String
    ): CompletableFuture<Long?> {
        val projectName = remoteUrl.split("/").last()
        val request: Request = prepareRequest(gitlabUrl, gitlabToken, "/projects?search=$projectName")
            .get()
            .build()

        return makeRequest(request, false) {
            val gitlabProjects = json.decodeFromString<Array<GitlabProject>>(it)
            val gitlabProject = gitlabProjects.find { gitlabProject -> gitlabProject.webUrl.equals(remoteUrl, true) }
            val gitlabProjectId = gitlabProject?.id

            if (gitlabProjectId == null) {
                sendNotification(Notification.remoteIdNotFound(project), project)
            }
            gitlabProjectId
        }
    }

    fun lintTemplate(
        gitlabUrl: String = this.gitlabUrl,
        gitlabToken: String = this.gitlabToken,
        content: String,
        remoteId: Long,
        branch: String,
        showGitlabTokenNotification: Boolean
    ): CompletableFuture<GitlabLint?> {

        val formBody = FormBody.Builder()
            .add("content", content)
            .add("ref", branch)
            .add("dry_run", "true")
            .build()

        val request = prepareRequest(gitlabUrl, gitlabToken, "/projects/${remoteId}/ci/lint")
            .header("Content-Type", "application/json")
            .post(formBody)
            .build()

        return makeRequest(request, showGitlabTokenNotification) {
            json.decodeFromString<GitlabLint>(it)
        }
    }

    fun getFile(
        gitlabUrl: String = this.gitlabUrl,
        gitlabToken: String = this.gitlabToken,
        filePath: String,
        remoteId: Long,
        branch: String,
        showGitlabTokenNotification: Boolean
    ): CompletableFuture<GitlabFile?> {

        val filePathUrlEncoded = URLEncoder.encode(filePath.removePrefix("/"), "UTF-8")
        val request: Request =
            prepareRequest(gitlabUrl, gitlabToken, "/projects/${remoteId}/repository/files/${filePathUrlEncoded}?ref=${branch}")
                .get()
                .build()

        return makeRequest(request, showGitlabTokenNotification) {
            val gitlabFile = json.decodeFromString<GitlabFile>(it)
            gitlabFile.content = String(Base64.getDecoder().decode(gitlabFile.content))
            gitlabFile
        }
    }

    private fun <T> makeRequest(
        request: Request,
        showGitlabTokenNotification: Boolean,
        deserialize: (String) -> T
    ): CompletableFuture<T?> {
        val result = CompletableFuture<T?>()

        httpClient.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (showGitlabTokenNotification) {
                        sendNotification(Notification.unsuccessfulRequest(e.localizedMessage))
                    }
                    result.complete(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (it.isSuccessful) {
                            val responseString = it.body!!.string()
                            result.complete(deserialize(responseString))
                        } else {
                            if (it.code == 401) {
                                if (showGitlabTokenNotification) {
                                    sendNotification(Notification.unauthorizedRequest(project), project)
                                }
                                result.complete(null)
                            } else if (it.code >= 500) {
                                if (showGitlabTokenNotification) {
                                    sendNotification(
                                        Notification.unsuccessfulRequest(
                                            message(
                                                "notifications.internal-server-error",
                                                it.code
                                            )
                                        )
                                    )
                                }
                                result.complete(null)
                            } else {
                                if (showGitlabTokenNotification) {
                                    sendNotification(Notification.unsuccessfulRequest(it.message.ifEmpty {
                                        message(
                                            "notifications.unknown-error",
                                            it.code
                                        )
                                    }))
                                }
                                result.complete(null)
                            }
                        }
                    }
                }
            })
        return result
    }
}
