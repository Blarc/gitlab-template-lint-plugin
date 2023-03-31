package com.github.blarc.gitlab.template.lint.plugin.pipeline

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils.updateStatusWidget
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware.*
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusEnum
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import java.util.*

@Service
class Pipeline(private val project: Project) {
    var gitlabLintResponse: GitlabLintResponse? = null
    var lintStatus: LintStatusEnum = LintStatusEnum.HIDDEN

    private val middlewares: Set<Middleware> = setOf(
        project.service<ResolveContext>(),
        project.service<ResolveGitlabToken>(),
        project.service<ShowSupportNotification>(),
        project.service<RecordHit>(),
        project.service<ResolveRemoteId>(),
        project.service<LintContext>()
    )

    fun accept(file: PsiFile) {

        if (middlewares.isEmpty()) {
            throw IllegalStateException("No middleware registered")
        }

        val queue = PriorityQueue(middlewares)

        updateStatusWidget(project, LintStatusEnum.RUNNING)
        val result = next(queue, Pass(project, file))

        gitlabLintResponse = result?.first
        updateStatusWidget(project,result?.second ?: LintStatusEnum.INVALID)
    }

    private fun next(queue: PriorityQueue<Middleware>, pass: Pass) : Pair<GitlabLintResponse?, LintStatusEnum>? {
        val middleware = queue.remove()

        return middleware(pass) {
            return@middleware next(queue, pass);
        }
    }
}
