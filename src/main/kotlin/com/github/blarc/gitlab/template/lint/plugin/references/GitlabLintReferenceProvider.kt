package com.github.blarc.gitlab.template.lint.plugin.references

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext

class GitlabLintReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {

        val psiReference = GitlabLintReference(element, TextRange(0, element.textLength))

        return if (psiReference.resolve() != null) {
            arrayOf(psiReference)
        } else PsiReference.EMPTY_ARRAY
    }
}