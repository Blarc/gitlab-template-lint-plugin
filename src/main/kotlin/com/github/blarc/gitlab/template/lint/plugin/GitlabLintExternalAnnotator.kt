package com.github.blarc.gitlab.template.lint.plugin

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiFile
import org.jetbrains.yaml.psi.YAMLFile

class GitlabLintExternalAnnotator : ExternalAnnotator<YAMLFile, List<GitlabLintProblem>>() {
    override fun collectInformation(file: PsiFile): YAMLFile? {
        if (file !is YAMLFile) {
            return null
        }

        return file
    }

    override fun collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): YAMLFile? {
        return collectInformation(file)
    }

    override fun doAnnotate(file: YAMLFile): List<GitlabLintProblem> {
        val fileManager = FileDocumentManager.getInstance()
        val fileSystem = LocalFileSystem.getInstance()
        val linter = file.project.service<GitlabLintRunner>()

        linter.run(file.text)
        println("doAnnotate")
        return emptyList()
    }

    override fun apply(file: PsiFile, annotationResult: List<GitlabLintProblem>?, holder: AnnotationHolder) {
        super.apply(file, annotationResult, holder)
    }


}
