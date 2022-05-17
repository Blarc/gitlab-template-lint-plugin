package com.github.blarc.gitlab.template.lint.plugin

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.yaml.snakeyaml.Yaml
import java.time.Duration


@Service
class GitlabLintRunner(project: Project) {
    companion object {
        private val timeout = Duration.ofSeconds(30)
        private val json = Json { ignoreUnknownKeys = true }
    }

    fun run(content: String): List<GitlabLintResponse> {

        try {
            val load = Yaml().load<Map<String, Any>>(content)
        }
        catch (e: Exception) {
            println("Invalid yaml!")
            return listOf()
        }

        val formBody = FormBody.Builder()
            .add("content", content)
            .build()

        val request = Request.Builder()
            .url("")
            .header("PRIVATE-TOKEN", "")
            .header("Content-Type", "application/json")
            .post(formBody)
            .build()

        val client = OkHttpClient.Builder()
            .callTimeout(timeout)
            .build()

        try {
            client.newCall(request).execute().use {
                if (it.isSuccessful) {
                    val responseString = it.body!!.string()
                    val decodeFromString = json.decodeFromString<GitlabLintResponse>(responseString)
                    println(decodeFromString)
                }
                else {
                    println("Unexpected code $it")
                }
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        return listOf()
    }


}
