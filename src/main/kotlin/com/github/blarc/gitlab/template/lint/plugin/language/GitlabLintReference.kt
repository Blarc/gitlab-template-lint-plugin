package com.github.blarc.gitlab.template.lint.plugin.language

import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabFile
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pipeline
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.FakePsiElement
import net.jimblackler.jsonschemafriend.Schema
import org.jetbrains.yaml.psi.YAMLKeyValue
import org.jetbrains.yaml.psi.YAMLScalar


class GitlabLintReference(private val schema: Schema?, element: PsiElement, textRange: TextRange) :
    PsiReferenceBase<PsiElement>(element, textRange) {

    class ResolveFilePsiElement(private val schema: Schema, private val element: PsiElement) : FakePsiElement() {
        override fun getParent(): PsiElement {
            return element
        }

        override fun navigate(requestFocus: Boolean) {
            val psiFileFactory = PsiFileFactory.getInstance(element.project)

            ReadAction.nonBlocking {
                val resolvedFile = resolveFile(schema.uri.toString(), element as YAMLScalar) ?: return@nonBlocking
                val psiFile = psiFileFactory.createFileFromText(
                    resolvedFile.fileName,
                    element.language,
                    resolvedFile.content
                )
                OpenFileDescriptor(element.project, psiFile.virtualFile).navigate(requestFocus)
            }.inSmartMode(element.project).executeSynchronously()
        }

        override fun getPresentableText(): String {
            return "test"
        }

        override fun getName(): String? {
            return super.getName()
        }

        override fun getTextRange(): TextRange? {
            return element.textRange
        }

        private fun resolveFile(schema: String, element: YAMLScalar): GitlabFile? {
            val value = element.textValue
            val pipeline = element.project.service<Pipeline>()

            GitlabLintSchema.IncludeItem.values()
                .filter { it.value == schema }
                .first() {
                    return pipeline.runResolveInclude(element.containingFile, value, it, getProperties()) as GitlabFile?
                }
            return null
        }

        private fun getProperties(): Map<String, String> {
            return findParent(element).children
                .filterIsInstance<YAMLKeyValue>()
                .associate {
                    it.keyText to it.valueText
                }
        }

        private fun findParent(element: PsiElement): PsiElement {
            if (element.children.isEmpty()) {
                return findParent(element.parent)
            }
            if (element.children.first() is YAMLKeyValue) {
                return element
            }
            return findParent(element.parent)
        }
    }

    override fun resolve(): PsiElement? {

        if (schema == null) {
            return null
        }

        if (!element.isValid) {
            return null
        }


        return ResolveFilePsiElement(schema, myElement)


//        val resolvedFile = resolveFile(schema.uri.toString(), element as YAMLScalar) ?: return null
//        return psiFileFactory.createFileFromText(
//            resolvedFile.fileName,
//            element.language,
//            resolvedFile.content
//        )
    }


}