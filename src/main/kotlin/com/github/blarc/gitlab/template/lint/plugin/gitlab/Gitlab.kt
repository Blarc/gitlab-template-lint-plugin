package com.github.blarc.gitlab.template.lint.plugin.gitlab

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.gitlab.http.HttpClientFactory
import com.github.blarc.gitlab.template.lint.plugin.notifications.Notification
import com.github.blarc.gitlab.template.lint.plugin.notifications.sendNotification
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException
import java.util.concurrent.CompletableFuture

@Service
open class Gitlab (project: Project) {
    private val allowSelfSignedTls: Boolean = false
    private val httpClient: OkHttpClient
    private val json: Json
    private val privateToken: String
    private val baseUrl: String

    init {
        val httpClientFactory: HttpClientFactory = HttpClientFactory.instance
        httpClient = if (allowSelfSignedTls) httpClientFactory.insecureHttpClient else httpClientFactory.httpClient
        json = Json { ignoreUnknownKeys = true }

        baseUrl = project.service<ProjectSettings>().gitlabUrl!!
        privateToken = AppSettings.instance?.getGitlabToken(baseUrl)!!
    }

    private fun prepareRequest(urlSuffix: String): Request.Builder {
        return Request.Builder()
            .url("${baseUrl}${urlSuffix}")
            .addHeader("Private-Token", privateToken)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun getVersion(): CompletableFuture<GitlabVersion> {
        val result: CompletableFuture<GitlabVersion> = CompletableFuture<GitlabVersion>()

        val request: Request = prepareRequest("/version")
            .get()
            .build()

        httpClient.newCall(request)
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
                            result.completeExceptionally(RuntimeException(it.body?.string() ?: "Request was unsuccessful!"))
                        }
                    }
                }

            })
        return result
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun searchProjectId(projectUrl: String, project: Project): CompletableFuture<Long?> {

        val projectName = projectUrl.split("/").last()
        val request: Request = prepareRequest("/projects?search=$projectName")
            .get()
            .build()

        val result = CompletableFuture<Long?>()
        httpClient.newCall(request)
            .enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    sendNotification(Notification.remoteIdNotFound(project), project)
                    result.complete(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (it.isSuccessful) {
                            val responseString = it.body!!.string()
                            val gitlabProjects = json.decodeFromString<Array<GitlabProject>>(responseString)
                            val gitlabProject = gitlabProjects.find { gitlabProject -> gitlabProject.webUrl.equals(projectUrl, true) }
                            result.complete(gitlabProject?.id)
                        }
                        else {
                            when(it.code) {
                                401 -> {
                                    sendNotification(Notification.unauthorizedRequest(project), project)
                                    result.complete(null)
                                }
                                else -> {
                                    sendNotification(Notification.remoteIdNotFound(project), project)
                                    result.complete(null)
                                }
                            }
                        }
                    }
                }

            })

        return result
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun lintContent(content: String, projectId: Long, branch: String, project: Project, showGitlabTokenNotification: Boolean): CompletableFuture<GitlabLintResponse?> {

        val formBody = FormBody.Builder()
            .add("content", content)
            .add("ref", branch)
            .add("dry_run", "true")
            .build()

        val request = prepareRequest("/projects/${projectId}/ci/lint")
            .header("Content-Type", "application/json")
            .post(formBody)
            .build()

        val result = CompletableFuture<GitlabLintResponse?>()
        httpClient.newCall(request)
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
                            when(it.code) {
                                401 -> {
                                    if (showGitlabTokenNotification) {
                                        sendNotification(Notification.unauthorizedRequest(project), project)
                                    }
                                    result.complete(null)
                                }
                                else -> {
                                    if (showGitlabTokenNotification) {
                                        sendNotification(Notification.unsuccessfulRequest(it.body?.string() ?: message("notifications.unknown-error")))
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
}
