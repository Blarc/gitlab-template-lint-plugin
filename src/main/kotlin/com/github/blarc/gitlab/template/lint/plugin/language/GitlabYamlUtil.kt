package com.github.blarc.gitlab.template.lint.plugin.language

import com.github.blarc.gitlab.template.lint.plugin.language.psi.GitlabYamlFile
import com.github.blarc.gitlab.template.lint.plugin.language.psi.GitlabYamlProperty
import com.google.common.collect.Lists
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import java.util.*


class GitlabYamlUtil {

  companion object {
    /**
     * Searches the entire project for Simple language files with instances of the Simple property with the given key.
     *
     * @param project current project
     * @param key     to check
     * @return matching properties
     */
    fun findProperties(project: Project, key: String): List<GitlabYamlProperty> {
      val result: MutableList<GitlabYamlProperty> = ArrayList()
      val virtualFiles: Collection<VirtualFile> = FileTypeIndex.getFiles(GitlabYamlFileType.INSTANCE, GlobalSearchScope.allScope(project))
      for (virtualFile in virtualFiles) {
        val gitlabYamlFile = PsiManager.getInstance(project).findFile(virtualFile) as GitlabYamlFile
        val properties: Array<GitlabYamlProperty>? = PsiTreeUtil.getChildrenOfType(gitlabYamlFile, GitlabYamlProperty::class.java)
        if (properties != null) {
          for (property in properties) {
            if (key == property.key) {
              result.add(property)
            }
          }
        }
      }
      return result
    }

    fun findProperties(project: Project): List<GitlabYamlProperty> {
      val result: ArrayList<GitlabYamlProperty> = ArrayList()
      val virtualFiles: Collection<VirtualFile> = FileTypeIndex.getFiles(GitlabYamlFileType.INSTANCE, GlobalSearchScope.allScope(project))
      for (virtualFile in virtualFiles) {
        val gitlabYamlFile = PsiManager.getInstance(project).findFile(virtualFile) as GitlabYamlFile
        val properties: Array<GitlabYamlProperty>? = PsiTreeUtil.getChildrenOfType(gitlabYamlFile, GitlabYamlProperty::class.java)
        if (properties != null) {
          result.plus(properties)
        }
      }
      return result
    }

    /**
     * Attempts to collect any comment elements above the Simple key/value pair.
     */
    fun findDocumentationComment(property: GitlabYamlProperty): String {
      val result: MutableList<String> = LinkedList()
      var element: PsiElement = property.prevSibling
      while (element is PsiComment || element is PsiWhiteSpace) {
        if (element is PsiComment) {
          val commentText: String = element.getText().replaceFirst("[!# ]+", "")
          result.add(commentText)
        }
        element = element.prevSibling
      }
      return StringUtil.join(Lists.reverse(result), "\n ")
    }
  }
}
