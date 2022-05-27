package com.github.blarc.gitlab.template.lint.plugin.gitlab

import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettingsState
import com.intellij.openapi.project.Project

class GitLabFactory() {

    fun getGitLab(url: String): Gitlab {
        return Gitlab(url, AppSettingsState.instance.gitlabToken!!)
    }

    companion object {
        fun getInstance(project: Project?): GitLabFactory? {
            return project!!.getService(GitLabFactory::class.java)
        }
    }
}
