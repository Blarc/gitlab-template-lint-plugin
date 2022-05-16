package com.github.blarc.gitlab.template.lint.plugin

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.yaml.snakeyaml.Yaml
import java.time.Duration

@Service
class GitlabLintRunner(project: Project) {
    companion object {
        private val timeout = Duration.ofSeconds(30)
    }

    fun run(content: String): List<String> {


        try {
            val load = Yaml().load<Map<String, Any>>(content)

            println(content)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        val request = Request.Builder()
            .url("https://gitlab.x.si/api/v4/ci/lint")
            .header("PRIVATE-TOKEN", "insert_token")
            .post("{content: $content}".toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        val client = OkHttpClient()
        try {
            val response = client.newCall(request).execute()
            println(response.body!!.toString())
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        client.newCall(request).execute()



        return listOf()
    }


}
