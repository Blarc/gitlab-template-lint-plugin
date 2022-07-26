package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass

interface Middleware : Comparable<Middleware> {
    val priority: Int

    operator fun invoke(pass: Pass, next: () -> GitlabLintResponse?) : GitlabLintResponse?

    override fun compareTo(other: Middleware): Int {
        return priority - other.priority
    }
}
