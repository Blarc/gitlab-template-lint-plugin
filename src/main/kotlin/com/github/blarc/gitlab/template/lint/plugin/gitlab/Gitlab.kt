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
import java.util.concurrent.CompletableFuture

@Service
open class Gitlab(val project: Project) {
    private val allowSelfSignedTls: Boolean = false
    private val httpClient: OkHttpClient
    private val json: Json

    init {
        val httpClientFactory: HttpClientFactory = HttpClientFactory.instance
        httpClient = if (allowSelfSignedTls) httpClientFactory.insecureHttpClient else httpClientFactory.httpClient
        json = Json { ignoreUnknownKeys = true }
    }

    private fun prepareRequest(
        baseUrl: String,
        gitlabToken: String,
        urlSuffix: String
    ): Request.Builder {
        return Request.Builder()
            .url("${baseUrl}${urlSuffix}")
            .addHeader("Private-Token", gitlabToken)
    }

    fun getVersion(
        baseUrl: String,
        gitlabToken: String
    ): CompletableFuture<GitlabVersion?> {
        val request: Request = prepareRequest(baseUrl, gitlabToken, "/version")
            .get()
            .build()

        return makeRequest(request, false) {
            json.decodeFromString<GitlabVersion>(it)
        }
    }

    fun searchProjectId(
        baseUrl: String,
        gitlabToken: String,
        remoteUrl: String
    ): CompletableFuture<Long?> {
        val projectName = remoteUrl.split("/").last()
        val request: Request = prepareRequest(baseUrl, gitlabToken, "/projects?search=$projectName")
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
        baseUrl: String,
        gitlabToken: String,
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

        val request = prepareRequest(baseUrl, gitlabToken, "/projects/${remoteId}/ci/lint")
            .header("Content-Type", "application/json")
            .post(formBody)
            .build()

        return makeRequest(request, showGitlabTokenNotification) {
            json.decodeFromString<GitlabLint>(it)
        }
    }

    fun getFile(
        baseUrl: String,
        gitlabToken: String,
        filePath: String,
        remoteId: Long,
        branch: String,
        showGitlabTokenNotification: Boolean
    ): CompletableFuture<GitlabFile?> {

        val request: Request =
            prepareRequest(baseUrl, gitlabToken, "/projects/${remoteId}/repository/files/${filePath}?ref=${branch}")
                .get()
                .build()

        return makeRequest(request, showGitlabTokenNotification) {
            json.decodeFromString<GitlabFile>(it)
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
