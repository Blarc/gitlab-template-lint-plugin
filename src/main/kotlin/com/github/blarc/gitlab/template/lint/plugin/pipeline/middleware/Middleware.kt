package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabObject
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.pipeline.PipelineStatusEnum

interface Middleware : Comparable<Middleware> {
    val priority: Int

    operator fun invoke(pass: Pass, next: () -> Pair<GitlabObject?, PipelineStatusEnum>?) : Pair<GitlabObject?, PipelineStatusEnum>?

    override fun compareTo(other: Middleware): Int {
        return priority - other.priority
    }
}
