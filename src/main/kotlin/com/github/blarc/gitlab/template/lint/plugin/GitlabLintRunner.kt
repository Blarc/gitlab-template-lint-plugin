package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitLabFactory
import com.intellij.dvcs.DvcsUtil
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import git4idea.GitUtil
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.yaml.snakeyaml.Yaml


@Service
class GitlabLintRunner(private val project: Project) {

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

        if (repository != null) {
            val url = repository.remotes.first().urls[0].toHttpUrlOrNull()
            val projectName = repository.project.name
            val gitlab = GitLabFactory.getInstance(project)!!.getGitLab("${url?.scheme}://${url?.host}")
            gitlab.searchProject(projectName).thenAccept {
                val projectId = it[0].id
                gitlab.lintContent(content, projectId).thenAccept { gitlabLintResponse ->
                    println(gitlabLintResponse)
                }.get()
            }.get()
        }
        else {
            println("Not a repository!")
        }


        return listOf()
    }


}
