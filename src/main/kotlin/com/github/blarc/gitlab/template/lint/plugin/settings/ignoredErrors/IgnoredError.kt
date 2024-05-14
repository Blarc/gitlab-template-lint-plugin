package com.github.blarc.gitlab.template.lint.plugin.settings.ignoredErrors


class IgnoredError(
    var filePath: String,
    var errorMessage: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as IgnoredError
        return filePath == other.filePath && errorMessage == other.errorMessage
    }

    override fun hashCode(): Int {
        return filePath.hashCode() + errorMessage.hashCode()
    }
}
