package com.github.blarc.gitlabtemplatelintplugin

class GitlabLintProblem(
    val file: String,
    val line: Int,
    val column: Int,
    val level: Level,
    val message: String
) {
    enum class Level {
        WARNING, ERROR
    }
}
