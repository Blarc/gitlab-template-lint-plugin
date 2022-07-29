package com.github.blarc.gitlab.template.lint.plugin.gitlab

import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.intellij.openapi.project.Project

class GitlabFactory() {

    fun getGitLab(url: String): Gitlab {
        return Gitlab(url, AppSettings.instance?.gitlabToken!!)
    }

    companion object {
        fun getInstance(project: Project?): GitlabFactory? {
            return project!!.getService(GitlabFactory::class.java)
        }
    }
}
