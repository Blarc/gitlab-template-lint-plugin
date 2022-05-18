package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitLabFactory
import com.intellij.dvcs.DvcsUtil
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import git4idea.GitUtil
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.yaml.snakeyaml.Yaml
import java.nio.charset.StandardCharsets
import java.time.Duration


@Service
class GitlabLintRunner(private val project: Project) {
    companion object {
        private val timeout = Duration.ofSeconds(30)
        private val json = Json { ignoreUnknownKeys = true }
    }

    fun run(content: String, filePath: String): List<GitlabLintResponse> {

        try {
            val load = Yaml().load<Map<String, Any>>(content)
        }
        catch (e: Exception) {
            println("Invalid yaml!")
            return listOf()
        }

        val repositoryManager = GitUtil.getRepositoryManager(project)
        val repository = DvcsUtil.guessCurrentRepositoryQuick(project, repositoryManager, filePath)
        val url = repository!!.remotes.first().urls[0].toHttpUrlOrNull()

        val gitlab = GitLabFactory.getInstance(project)!!.getGitLab("${url?.scheme}://${url?.host}")

        gitlab.getVersion().thenAccept {
            println(it)
        }.get()

        return listOf()
    }

    private fun createCommand(): GeneralCommandLine {
        return GeneralCommandLine()
            .withCharset(StandardCharsets.UTF_8)
    }


}
