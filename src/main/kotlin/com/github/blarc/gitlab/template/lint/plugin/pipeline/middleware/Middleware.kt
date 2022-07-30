package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.widgets.LintStatusEnum

interface Middleware : Comparable<Middleware> {
    val priority: Int

    operator fun invoke(pass: Pass, next: () -> Pair<GitlabLintResponse?, LintStatusEnum>?) : Pair<GitlabLintResponse?, LintStatusEnum>?

    override fun compareTo(other: Middleware): Int {
        return priority - other.priority
    }
}
