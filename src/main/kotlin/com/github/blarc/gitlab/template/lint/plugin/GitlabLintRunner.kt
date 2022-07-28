package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pipeline
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile


fun runLinting(project: Project, file: PsiFile) {
    val pipeline = project.service<Pipeline>()
    pipeline.accept(file)
}


//@Service
//class GitlabLintRunner(private val project: Project) {
//
//    var status: LintStatusEnum = LintStatusEnum.WAITING
//    var gitlabLintResponse: GitlabLintResponse? = null
//
//    fun runLinting(file: PsiFile) {
//        val pipeline = project.service<Pipeline>()
//        pipeline.accept(file)
//    }
//
//    fun run(content: String, filePath: String) {
//
//        updateStatusWidget(LintStatusEnum.RUNNING)
//
//        val repositoryManager = GitUtil.getRepositoryManager(project)
//        val repository = DvcsUtil.guessCurrentRepositoryQuick(project, repositoryManager, filePath)
//
//        if (repository != null) {
//            try {
//
//                val remoteUrl = getRemoteUrl(repository)
//                val remoteUrlString = remoteUrl.toString()
//                val gitlab = GitLabFactory.getInstance(project)!!.getGitLab("${remoteUrl?.scheme}://${remoteUrl?.host}")
//
//                getProjectId(remoteUrlString, gitlab).thenAccept { projectId ->
//
//                    // Cache project id
//                    val remotesMap = AppSettingsState.instance?.remotesMap
//                    if (remotesMap != null && !remotesMap.containsKey(remoteUrlString)) {
//                        remotesMap[remoteUrlString] = projectId
//                    }
//
//                    val branch = repository.currentBranch!!.name
//                    gitlab.lintContent(content, projectId, branch).thenAccept {
//                        gitlabLintResponse = it
//                    }.get()
//
//                }.get()
//            }
//            catch (e: Exception) {
//                GitlabLintUtils.createNotification(project, e.localizedMessage?: "Unknown exception!")
//            }
//
//            if (gitlabLintResponse?.valid == true) updateStatusWidget(LintStatusEnum.VALID) else updateStatusWidget(LintStatusEnum.INVALID)
//        }
//        else {
//            updateStatusWidget(LintStatusEnum.INVALID)
//            gitlabLintResponse = null
//            GitlabLintUtils.createNotification(project, "File is not part of a Gitlab repository.")
//        }
//    }
//
//    private fun updateStatusWidget(lintingStatus: LintStatusEnum) {
//        status = lintingStatus
//        WindowManager.getInstance().getStatusBar(project)?.updateWidget(LintStatusWidget.ID)
//    }
//
//    private fun getProjectId(remoteUrl: String, gitlab: Gitlab) : CompletableFuture<Long> {
//        val remotesMap = AppSettingsState.instance?.remotesMap
//        if (remotesMap != null && remotesMap.containsKey(remoteUrl)) {
//            return CompletableFuture.completedFuture(remotesMap[remoteUrl])
//        }
//        return gitlab.searchProjectId(remoteUrl.removeSuffix(".git"))
//    }
//
//    private fun getRemoteUrl(repository: GitRepository): HttpUrl? {
//        var urlString = repository.remotes.first().firstUrl
//
//        // Example: git@github.com:facebook/react.git
//        if (urlString!!.startsWith("git@")) {
//            urlString = urlString.removePrefix("git@")
//            urlString = urlString.replaceFirst(":", "/")
//            urlString = "http://${urlString}"
//        }
//        // Example: git://github.com/facebook/react.git#gh-pages
//        else if (urlString.startsWith("git:")) {
//            urlString = urlString.removePrefix("git")
//            urlString = "http${urlString}"
//        }
//
//        return urlString.toHttpUrlOrNull()
//    }
//}
