package com.github.blarc.gitlabtemplatelintplugin

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.openapi.editor.Editor
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
        println("doAnnotate")
        return emptyList()
    }

    override fun apply(file: PsiFile, annotationResult: List<GitlabLintProblem>?, holder: AnnotationHolder) {
        super.apply(file, annotationResult, holder)
    }


}
