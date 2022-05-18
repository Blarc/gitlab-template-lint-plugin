package com.github.blarc.gitlab.template.lint.plugin.gitlab

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.gitlab.http.HttpClientFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.apache.http.client.HttpResponseException
import java.io.IOException
import java.util.concurrent.CompletableFuture

open class GitLab @JvmOverloads constructor(
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
            .url(baseUri + urlSuffix)
            .addHeader("Private-Token", privateToken)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun getVersion(): CompletableFuture<GitlabVersionResponse> {
        val url = "$baseUri/version"
        val result: CompletableFuture<GitlabVersionResponse> = CompletableFuture<GitlabVersionResponse>()
        if (url.toHttpUrlOrNull() == null) {
            result.completeExceptionally(HttpResponseException(500, "Incorrect GitLab URL"))
            return result
        }
        if (baseUri.endsWith("/")) {
            result.completeExceptionally(HttpResponseException(500, "Remove last slash from URL"))
            return result
        }
        val request: Request = prepareRequest("/version").build()
        httpClient.newCall(request)
            .enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    result.completeExceptionally(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (it.isSuccessful) {
                            val responseString = it.body!!.string()
                            val decodeFromString = json.decodeFromString<GitlabVersionResponse>(responseString)
                            result.complete(decodeFromString)
                        }
                        else {
                            result.completeExceptionally(RuntimeException("Exception when calling linter!"))
                        }
                    }
                }

            })
        return result
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun searchProject(projectName: String): CompletableFuture<List<GitlabProject>> {

        val request: Request = Request.Builder()
            .url("$baseUri/projects?search=$projectName")
            .addHeader("Private-Token", privateToken)
            .get()
            .build()

        val result: CompletableFuture<List<GitlabProject>> = CompletableFuture<List<GitlabProject>>()

        httpClient.newCall(request)
            .enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    result.completeExceptionally(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (it.isSuccessful) {
                            val responseString = it.body!!.string()
                            val decodeFromString = json.decodeFromString<List<GitlabProject>>(responseString)
                            result.complete(decodeFromString)
                        }
                        else {
                            result.completeExceptionally(RuntimeException("Exception when calling linter!"))
                        }
                    }
                }

            })
        return result
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun lintContent(content: String, projectId: Long): CompletableFuture<GitlabLintResponse> {

        val formBody = FormBody.Builder()
            .add("content", content)
            .build()

        val request = prepareRequest("${projectId}/704/ci/lint\"")
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
                            result.completeExceptionally(RuntimeException("Exception when calling linter!"))
                        }
                    }
                }

            })

        return result
    }
}
