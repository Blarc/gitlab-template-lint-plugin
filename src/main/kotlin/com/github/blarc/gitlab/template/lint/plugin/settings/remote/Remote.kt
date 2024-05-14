package com.github.blarc.gitlab.template.lint.plugin.settings.remote

data class Remote(
    var remoteUrl: String = "",
    var gitlabUrl: String? = null,
    var remoteId: Long? = null
)
