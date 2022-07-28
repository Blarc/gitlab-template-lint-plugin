package com.github.blarc.gitlab.template.lint.plugin.gitlab

import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettingsState
import com.intellij.openapi.project.Project

class GitlabFactory() {

    fun getGitLab(url: String): Gitlab {
        return Gitlab(url, AppSettingsState.instance?.gitlabToken!!)
    }

    companion object {
        fun getInstance(project: Project?): GitlabFactory? {
            return project!!.getService(GitlabFactory::class.java)
        }
    }
}
