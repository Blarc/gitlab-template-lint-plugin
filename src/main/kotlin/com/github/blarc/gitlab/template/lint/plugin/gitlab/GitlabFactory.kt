package com.github.blarc.gitlab.template.lint.plugin.gitlab

import com.intellij.openapi.project.Project
import java.net.URI

class GitlabFactory() {

    fun getGitLab(url: URI, gitlabToken: String): Gitlab {
        return Gitlab("${url.scheme}://${url.host}", gitlabToken)
    }

    companion object {
        fun getInstance(project: Project): GitlabFactory? {
            return project.getService(GitlabFactory::class.java)
        }
    }
}
