package com.github.blarc.gitlab.template.lint.plugin.pipeline

import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware.LintContext
import com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware.Middleware
import com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware.ResolveContext
import com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware.ResolveRemoteId
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
        project.service<LintContext>()
    )

    fun accept(file: PsiFile) {
        if (middlewares.isEmpty()) {
            throw IllegalStateException("No middleware registered")
        }

        val queue = PriorityQueue(middlewares)

        updateStatusWidget(LintStatusEnum.RUNNING)
        gitlabLintResponse = next(queue, Pass(project, file))
        updateStatusWidget(if (gitlabLintResponse?.valid == true) LintStatusEnum.VALID else LintStatusEnum.INVALID)
    }

    private fun next(queue: PriorityQueue<Middleware>, pass: Pass) : GitlabLintResponse? {
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
