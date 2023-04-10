package com.github.blarc.gitlab.template.lint.plugin.references

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReferenceBase
import net.jimblackler.jsonschemafriend.Schema

class GitlabLintReference(val schema: Schema?, element: PsiElement, textRange: TextRange) :
    PsiReferenceBase<PsiElement>(element, textRange) {
    override fun resolve(): PsiElement? {
        val psiFileFactory = PsiFileFactory.getInstance(element.project)

        if (schema == null) {
            return null
        }
        return psiFileFactory.createFileFromText("filename.yaml", element.language, "schema: $schema\nuri: ${schema.uri}\nref: ${schema.ref}")
    }
}