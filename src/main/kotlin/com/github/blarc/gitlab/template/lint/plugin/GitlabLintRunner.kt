package com.github.blarc.gitlab.template.lint.plugin

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.util.ExecUtil
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.yaml.snakeyaml.Yaml
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.time.Duration


@Service
class GitlabLintRunner(private val project: Project) {
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

        val generalCommandLine = GeneralCommandLine(arrayListOf("git", "status"))
        generalCommandLine.setCharset(Charset.forName("UTF-8"))
        generalCommandLine.setWorkDirectory(project.basePath)

        val execAndGetOutput = ExecUtil.execAndGetOutput(generalCommandLine)
        println(execAndGetOutput)

        "https://gitlab.medius.si/api/v4/projects?search=jakob-test"

        return listOf()
    }

    private fun createCommand(): GeneralCommandLine {
        return GeneralCommandLine()
            .withCharset(StandardCharsets.UTF_8)
    }


}
