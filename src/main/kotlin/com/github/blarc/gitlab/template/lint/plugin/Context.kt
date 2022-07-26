package com.github.blarc.gitlab.template.lint.plugin

import com.intellij.openapi.vfs.VirtualFile

sealed class Context(val file: VirtualFile)
