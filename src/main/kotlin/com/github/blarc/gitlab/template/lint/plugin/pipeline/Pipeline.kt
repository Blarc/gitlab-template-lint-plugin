package com.github.blarc.gitlab.template.lint.plugin.pipeline

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabObject
import com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware.*
import com.github.blarc.gitlab.template.lint.plugin.widget.PipelineStatusEnum
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import java.util.*

@Service
class Pipeline(val project: Project) {
    var pipelineStatus: PipelineStatusEnum = PipelineStatusEnum.HIDDEN
    var pipelineResult: GitlabObject? = null

    private val middlewares: MutableSet<Middleware> = mutableSetOf(
        project.service<ResolveContext>(),
        project.service<ResolveGitlabToken>(),
        project.service<ShowSupportNotification>(),
        project.service<RecordHit>(),
        project.service<ResolveRemoteId>()
    )

    private fun accept(pass: Pass, middleware: Middleware) {

        val queue = PriorityQueue(middlewares.plus(middleware))

        GitlabLintUtils.updateStatusWidget(project, PipelineStatusEnum.RUNNING)

        val result = next(queue, pass)
        pipelineResult = result?.first

        GitlabLintUtils.updateStatusWidget(project, result?.second ?: PipelineStatusEnum.INVALID)
    }

    private fun next(queue: PriorityQueue<Middleware>, pass: Pass) : Pair<GitlabObject?, PipelineStatusEnum>? {
        val middleware = queue.remove()

        return middleware(pass) {
            return@middleware next(queue, pass);
        }
    }

    fun runLint(file: PsiFile) {
        accept(Pass(project, file), project.service<LintTemplate>())
    }

    fun funResolveInclude(includePsiElement: PsiElement) {
        accept(PassResolveInclude(project, includePsiElement), project.service<ResolveInclude>())
    }
}