package com.github.blarc.gitlab.template.lint.plugin.references

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReferenceBase

class GitlabLintReference(element: PsiElement, textRange: TextRange) :
    PsiReferenceBase<PsiElement>(element, textRange) {
    override fun resolve(): PsiElement? {
        val psiFileFactory = PsiFileFactory.getInstance(element.project)
        return psiFileFactory.createFileFromText("filename.yaml", element.language, "hello: world")
    }
}