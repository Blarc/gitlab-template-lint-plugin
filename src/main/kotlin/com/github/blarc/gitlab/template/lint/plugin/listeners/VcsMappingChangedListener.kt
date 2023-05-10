package com.github.blarc.gitlab.template.lint.plugin.listeners

import com.github.blarc.gitlab.template.lint.plugin.git.gitlabUrl
import com.github.blarc.gitlab.template.lint.plugin.git.locateRemote
import com.github.blarc.gitlab.template.lint.plugin.gitlab.http.toHttps
import com.github.blarc.gitlab.template.lint.plugin.notifications.Notification
import com.github.blarc.gitlab.template.lint.plugin.notifications.sendNotification
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.intellij.dvcs.repo.VcsRepositoryMappingListener
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import git4idea.repo.GitRepositoryManager

class VcsMappingChangedListener(val project: Project) : VcsRepositoryMappingListener {

    override fun mappingChanged() {
        val settings = project.service<ProjectSettings>()
        val projectSettings = project.service<ProjectSettings>()

        val currentGitlabUrls = GitRepositoryManager.getInstance(project)
            .repositories
            .asSequence()
            .mapNotNull { it.locateRemote(settings.remote) }
            .mapNotNull { it.gitlabUrl }
            .map { if (project.service<ProjectSettings>().forceHttps) it.toHttps() else it }
            .map { it.toString() }
            .toSet()

        projectSettings.gitlabUrls = currentGitlabUrls
        AppSettings.instance.remotes
            .mapNotNull { it.value.gitlabUrl }
            .forEach { projectSettings.gitlabUrls += it }

        if (projectSettings.newProject) {
            if (projectSettings.gitlabUrls.isEmpty()) {
                sendNotification(Notification.couldNotDetectGitlabUrl(project), project)
            } else {
                sendNotification(Notification.gitlabUrlsAutoDetected(currentGitlabUrls, project), project)
            }
        }

        projectSettings.newProject = false
    }
}
