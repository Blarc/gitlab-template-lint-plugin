package com.github.blarc.gitlab.template.lint.plugin.listeners

import com.github.blarc.gitlab.template.lint.plugin.git.gitlabUrl
import com.github.blarc.gitlab.template.lint.plugin.git.locateRemote
import com.github.blarc.gitlab.template.lint.plugin.notifications.Notification
import com.github.blarc.gitlab.template.lint.plugin.notifications.sendNotification
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.intellij.dvcs.repo.VcsRepositoryMappingListener
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import git4idea.repo.GitRepositoryManager

class VcsMappingChangedListener(val project: Project) : VcsRepositoryMappingListener {
    override fun mappingChanged() {
        val settings = project.service<ProjectSettings>()
        val projectSettings = project.service<ProjectSettings>()

        val gitlabUrls = GitRepositoryManager.getInstance(project)
            .repositories
            .mapNotNull { it.locateRemote(settings.remote) }
            .mapNotNull { it.gitlabUrl }
            .map { it.toString() }

        if (projectSettings.gitlabUrl != null && gitlabUrls.contains(projectSettings.gitlabUrl)) {
            return
        }

        val gitlabUrl = gitlabUrls.firstOrNull()
        if (gitlabUrl == null) {
            sendNotification(Notification.couldNotDetectGitlabUrl(project), project)
            return
        }

        sendNotification(Notification.gitlabUrlAutoDetected(gitlabUrl, project), project)
        projectSettings.gitlabUrl = gitlabUrl
    }
}
