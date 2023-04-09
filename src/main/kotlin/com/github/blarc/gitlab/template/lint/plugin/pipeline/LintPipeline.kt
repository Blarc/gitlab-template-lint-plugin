package com.github.blarc.gitlab.template.lint.plugin.pipeline

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils.updateStatusWidget
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLint
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabObject
import com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware.*
import com.github.blarc.gitlab.template.lint.plugin.widget.PipelineStatusEnum
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import java.util.*

@Service
class LintPipeline(private val project: Project) {
    var gitlabLint: GitlabLint? = null
    var lintStatus: PipelineStatusEnum = PipelineStatusEnum.HIDDEN

    private val middlewares: Set<Middleware> = setOf(
        project.service<ResolveContext>(),
        project.service<ResolveGitlabToken>(),
        project.service<ShowSupportNotification>(),
        project.service<RecordHit>(),
        project.service<ResolveRemoteId>(),
        project.service<LintTemplate>()
    )

    fun accept(file: PsiFile) {

        if (middlewares.isEmpty()) {
            throw IllegalStateException("No middleware registered")
        }

        val queue = PriorityQueue(middlewares)

        updateStatusWidget(project, PipelineStatusEnum.RUNNING)
        val result = next(queue, Pass(project, file))

        gitlabLint = result?.first as GitlabLint?
        updateStatusWidget(project,result?.second ?: PipelineStatusEnum.INVALID)
    }

    private fun next(queue: PriorityQueue<Middleware>, pass: Pass) : Pair<GitlabObject?, PipelineStatusEnum>? {
        val middleware = queue.remove()

        return middleware(pass) {
            return@middleware next(queue, pass);
        }
    }
}
