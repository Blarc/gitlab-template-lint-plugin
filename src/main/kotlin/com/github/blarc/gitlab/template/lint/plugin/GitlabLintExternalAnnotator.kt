package com.github.blarc.gitlab.template.lint.plugin

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiFile
import org.jetbrains.yaml.psi.YAMLFile

class GitlabLintExternalAnnotator : ExternalAnnotator<YAMLFile, List<GitlabLintResponse>>() {
    override fun collectInformation(file: PsiFile): YAMLFile? {
        if (file !is YAMLFile) {
            return null
        }

        return file
    }

    override fun collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): YAMLFile? {
        return collectInformation(file)
    }

    override fun doAnnotate(file: YAMLFile): List<GitlabLintResponse> {
        val fileManager = FileDocumentManager.getInstance()
        val fileSystem = LocalFileSystem.getInstance()
        val linter = file.project.service<GitlabLintRunner>()

        return linter.run(file.text, file.originalFile.virtualFile.path)
    }

    override fun apply(file: PsiFile, annotationResult: List<GitlabLintResponse>?, holder: AnnotationHolder) {
        super.apply(file, annotationResult, holder)
    }


}
