package com.github.blarc.gitlab.template.lint.plugin.pipeline

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware.LintContext
import com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware.Middleware
import com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware.ResolveContext
import com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware.ResolveRemoteId
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import java.util.*

@Service
class Pipeline(private val project: Project) {
    private val middlewares: Set<Middleware> = setOf(
        project.service<ResolveContext>(),
        project.service<ResolveRemoteId>(),
        project.service<LintContext>()
    )

    fun accept(file: PsiFile) : GitlabLintResponse? {
        if (middlewares.isEmpty()) {
            throw IllegalStateException("No middleware registered")
        }

        val queue = PriorityQueue(middlewares)

        return next(queue, Pass(project, file))
    }

    private fun next(queue: PriorityQueue<Middleware>, pass: Pass) : GitlabLintResponse? {
        val middleware = queue.remove()

        return middleware(pass) {
            return@middleware next(queue, pass);
        }
    }
}
