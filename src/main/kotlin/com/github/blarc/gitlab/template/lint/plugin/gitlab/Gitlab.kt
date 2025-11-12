package com.github.blarc.gitlab.template.lint.plugin.gitlab

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.gitlab.http.HttpClientFactory
import com.github.blarc.gitlab.template.lint.plugin.notifications.Notification
import com.github.blarc.gitlab.template.lint.plugin.notifications.sendNotification
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException
import java.util.concurrent.CompletableFuture

@Service(Service.Level.PROJECT)
class Gitlab(val project: Project) {
    private val json: Json = Json { ignoreUnknownKeys = true }

    private fun createHttpClient(): OkHttpClient {
        val httpClientFactory: HttpClientFactory = HttpClientFactory.instance
        return if (AppSettings.instance.allowSelfSignedCertificate) httpClientFactory.insecureHttpClient else httpClientFactory.httpClient
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
    ): CompletableFuture<GitlabVersion> {
        val result: CompletableFuture<GitlabVersion> = CompletableFuture<GitlabVersion>()

        val request: Request = prepareRequest(baseUrl, gitlabToken, "/version")
            .get()
            .build()

        createHttpClient().newCall(request)
            .enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    result.completeExceptionally(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (it.isSuccessful) {
                            val responseString = it.body!!.string()
                            val decodeFromString = json.decodeFromString<GitlabVersion>(responseString)
                            result.complete(decodeFromString)
                        }
                        else {
                            result.completeExceptionally(Throwable(it.message.ifEmpty { "Request was unsuccessful!" }))
                        }
                    }
                }

            })
        return result
    }

    fun searchProjectId(
        baseUrl: String,
        gitlabToken: String,
        remoteUrl: String,
        showProjectIdNotification: Boolean
    ): CompletableFuture<Long?> {

        val projectName = remoteUrl.split("/").last()
        val request: Request = prepareRequest(baseUrl, gitlabToken,"/projects?search=$projectName")
            .get()
            .build()

        val result = CompletableFuture<Long?>()
        createHttpClient().newCall(request)
            .enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    if (showProjectIdNotification) {
                        sendNotification(Notification.remoteIdNotFound(project), project)
                    }
                    result.complete(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (it.isSuccessful) {
                            val responseString = it.body!!.string()
                            val gitlabProjects = json.decodeFromString<Array<GitlabProject>>(responseString)
                            val gitlabProject = gitlabProjects.find { gitlabProject -> gitlabProject.webUrl.equals(remoteUrl, true) }
                            val gitlabProjectId = gitlabProject?.id

                            if (gitlabProjectId == null && showProjectIdNotification) {
                                sendNotification(Notification.remoteIdNotFound(project), project)
                            }
                            result.complete(gitlabProjectId)
                        }
                        else {
                            when(it.code) {
                                401 -> {
                                    if (showProjectIdNotification) {
                                        sendNotification(Notification.unauthorizedRequest(project), project)
                                    }
                                    result.complete(null)
                                }
                                403 -> {
                                    if (showProjectIdNotification) {
                                        sendNotification(Notification.forbiddenRequest(project), project)
                                    }
                                    result.complete(null)
                                }
                                else -> {
                                    if (showProjectIdNotification) {
                                        sendNotification(Notification.remoteIdNotFound(project), project)
                                    }
                                    result.complete(null)
                                }
                            }
                        }
                    }
                }

            })
        return result
    }

    fun lintContent(
        baseUrl: String,
        gitlabToken: String,
        content: String,
        remoteId: Long,
        branch: String,
        showGitlabTokenNotification: Boolean
    ): CompletableFuture<GitlabLintResponse?> {

        val formBody = FormBody.Builder()
            .add("content", content)
            .add("ref", branch)
            .add("dry_run", "true")
            .build()

        val request = prepareRequest(baseUrl, gitlabToken,"/projects/${remoteId}/ci/lint")
            .header("Content-Type", "application/json")
            .post(formBody)
            .build()

        val result = CompletableFuture<GitlabLintResponse?>()
        createHttpClient().newCall(request)
            .enqueue(object: Callback{
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
                            val decodeFromString = json.decodeFromString<GitlabLintResponse>(responseString)
                            result.complete(decodeFromString)
                        }
                        else {
                            if (it.code == 401) {
                                if (showGitlabTokenNotification) {
                                    sendNotification(Notification.unauthorizedRequest(project), project)
                                }
                                result.complete(null)
                            }
                            else if (it.code >= 500) {
                                if (showGitlabTokenNotification) {
                                    sendNotification(Notification.unsuccessfulRequest(message("notifications.internal-server-error", it.code)))
                                }
                                result.complete(null)
                            }
                            else {
                                if (showGitlabTokenNotification) {
                                    sendNotification(Notification.unsuccessfulRequest(it.message.ifEmpty { message("notifications.unknown-error", it.code) }))
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
