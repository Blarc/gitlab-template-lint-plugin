package com.github.blarc.gitlab.template.lint.plugin.references

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import net.jimblackler.jsonschemafriend.Schema
import org.jetbrains.yaml.psi.YAMLKeyValue
import org.jetbrains.yaml.psi.YAMLPsiElement
import org.jetbrains.yaml.psi.YAMLScalar

class GitlabLintReferenceProvider(val schema: Schema) : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {


        val pathForKey = pathForKey(element as YAMLScalar)
        val value = element.textValue

        val schemaForPath = schemaForPath(schema, pathForKey)

        val psiReference = GitlabLintReference(schemaForPath, element, TextRange(0, element.textLength))
        return if (psiReference.resolve() != null) {
            arrayOf(psiReference)
        } else PsiReference.EMPTY_ARRAY
    }

    private fun pathForKey(keyValue: YAMLPsiElement): List<String> {
        // Get the tree of keys leading up to this one
        val keys: MutableList<String> = ArrayList()
        var currentKey: YAMLPsiElement? = keyValue
        while (currentKey != null) {
            currentKey.name?.let { keys.add(it) }
            currentKey = PsiTreeUtil.getParentOfType(currentKey, YAMLKeyValue::class.java)
        }

        // We have iterated from the inside out, so flip this around to get it in the correct direction for the ModelProvider
        keys.reverse()
        return keys.toList()
    }

    private fun schemaForPath(schema: Schema, path: List<String>): Schema? {
        if (path.isEmpty() && schema.pattern != null) {
            return schema
        }

        if (path.isNotEmpty() && schema.properties != null && schema.properties.containsKey(path[0])) {
            return schemaForPath(schema.properties[path[0]]!!, path.subList(1, path.size))
        }

        if (schema.items != null) {
            return schemaForPath(schema.items, path)
        }

        if (schema.oneOf != null) {
            return schema.oneOf.firstNotNullOfOrNull { schemaForPath(it, path) }
        }

        return null
    }
}