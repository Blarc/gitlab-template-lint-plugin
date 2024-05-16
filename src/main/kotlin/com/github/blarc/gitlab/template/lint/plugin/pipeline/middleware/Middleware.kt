package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusEnum

interface Middleware : Comparable<Middleware> {
    val priority: Int

    suspend operator fun invoke(pass: Pass, next: suspend () -> Pair<GitlabLintResponse?, LintStatusEnum>?) : Pair<GitlabLintResponse?, LintStatusEnum>?

    override fun compareTo(other: Middleware): Int {
        return priority - other.priority
    }
}
