package com.github.blarc.gitlab.template.lint.plugin.extensions

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.layout.ValidationInfoBuilder

fun ValidationInfoBuilder.notBlank(value: String): ValidationInfo? =
    if (value.isBlank()) error(message("validation.required")) else null

fun ValidationInfoBuilder.isLong(value: String): ValidationInfo? {
    if (value.isBlank()){
        return null
    }

    value.toLongOrNull().let {
        if (it == null) {
            return error(message("validation.number"))
        } else {
            return null
        }
    }
}
