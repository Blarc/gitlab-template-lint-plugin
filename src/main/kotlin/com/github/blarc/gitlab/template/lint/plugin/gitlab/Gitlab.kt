package com.github.blarc.gitlab.template.lint.plugin.gitlab

import com.github.blarc.gitlab.template.lint.plugin.gitlab.http.HttpClientFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.apache.http.client.HttpResponseException
import java.io.IOException
import java.util.concurrent.CompletableFuture

open class Gitlab @JvmOverloads constructor(
    private val baseUri: String,
    private val privateToken: String,
    allowSelfSignedTls: Boolean = false
) {
    private val httpClient: OkHttpClient
    private val json: Json

    init {
        val httpClientFactory: HttpClientFactory = HttpClientFactory.instance
        httpClient = if (allowSelfSignedTls) httpClientFactory.insecureHttpClient else httpClientFactory.httpClient
        json = Json { ignoreUnknownKeys = true }
    }

    private fun prepareRequest(urlSuffix: String): Request.Builder {
        return Request.Builder()
            .url("${baseUri}/api/v4${urlSuffix}")
            .addHeader("Private-Token", privateToken)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun getVersion(): CompletableFuture<GitlabVersion> {
        val url = "$baseUri/version"
        val result: CompletableFuture<GitlabVersion> = CompletableFuture<GitlabVersion>()
        if (url.toHttpUrlOrNull() == null) {
            result.completeExceptionally(HttpResponseException(500, "Incorrect GitLab URL"))
            return result
        }
        if (baseUri.endsWith("/")) {
            result.completeExceptionally(HttpResponseException(500, "Remove last slash from URL"))
            return result
        }
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
    fun searchProjectId(projectUrl: String): CompletableFuture<Long?> {

        val projectName = projectUrl.split("/").last()
        val request: Request = prepareRequest("/projects?search=$projectName")
            .get()
            .build()

        val result = CompletableFuture<Long?>()

        httpClient.newCall(request)
            .enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    result.completeExceptionally(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (it.isSuccessful) {
                            val responseString = it.body!!.string()
                            val gitlabProjects = json.decodeFromString<Array<GitlabProject>>(responseString)
                            val gitlabProject = gitlabProjects.find { gitlabProject -> gitlabProject.webUrl.equals(projectUrl, true) }
                            result.complete(gitlabProject?.id)
                        }
                    }
                }

            })

        return result
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun lintContent(content: String, projectId: Long, branch: String): CompletableFuture<GitlabLintResponse> {

        val formBody = FormBody.Builder()
            .add("content", content)
            .add("ref", branch)
            .add("dry_run", "true")
            .build()

        val request = prepareRequest("/projects/${projectId}/ci/lint")
            .header("Content-Type", "application/json")
            .post(formBody)
            .build()

        val result = CompletableFuture<GitlabLintResponse>()
        httpClient.newCall(request)
            .enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    result.completeExceptionally(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (it.isSuccessful) {
                            val responseString = it.body!!.string()
                            val decodeFromString = json.decodeFromString<GitlabLintResponse>(responseString)
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
}
