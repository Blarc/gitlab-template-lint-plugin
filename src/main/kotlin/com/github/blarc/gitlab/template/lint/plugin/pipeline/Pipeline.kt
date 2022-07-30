package com.github.blarc.gitlab.template.lint.plugin.pipeline

import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware.*
import com.github.blarc.gitlab.template.lint.plugin.widgets.LintStatusEnum
import com.github.blarc.gitlab.template.lint.plugin.widgets.LintStatusWidget
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.PsiFile
import java.util.*

@Service
class Pipeline(private val project: Project) {
    var gitlabLintResponse: GitlabLintResponse? = null
    var lintStatus: LintStatusEnum = LintStatusEnum.WAITING

    private val middlewares: Set<Middleware> = setOf(
        project.service<ResolveContext>(),
        project.service<ResolveRemoteId>(),
        project.service<LintContext>(),
        project.service<RecordHit>(),
        project.service<ShowSupportNotification>()
    )

    fun accept(file: PsiFile) {
        if (middlewares.isEmpty()) {
            throw IllegalStateException("No middleware registered")
        }

        val queue = PriorityQueue(middlewares)

        updateStatusWidget(LintStatusEnum.RUNNING)
        val result = next(queue, Pass(project, file))

        gitlabLintResponse = result?.first
        updateStatusWidget(result?.second ?: LintStatusEnum.INVALID)
    }

    private fun next(queue: PriorityQueue<Middleware>, pass: Pass) : Pair<GitlabLintResponse?, LintStatusEnum>? {
        val middleware = queue.remove()

        return middleware(pass) {
            return@middleware next(queue, pass);
        }
    }

    private fun updateStatusWidget(status: LintStatusEnum) {
        lintStatus = status
        WindowManager.getInstance().getStatusBar(project)?.updateWidget(LintStatusWidget.ID)
    }
}
