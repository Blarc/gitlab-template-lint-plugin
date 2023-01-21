package com.github.blarc.gitlab.template.lint.plugin.providers

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager

private const val GITLAB_LINT_EDITOR_PROVIDER: String = "GitlabLintEditorProvider"
class GitlabLintEditorProvider : FileEditorProvider, DumbAware {
    override fun getEditorTypeId() = GITLAB_LINT_EDITOR_PROVIDER

    override fun accept(project: Project, file: VirtualFile): Boolean {
        if (!file.isValid) return false
        PsiManager.getInstance(project).findFile(file) ?: return false
        return GitlabLintUtils.matchesGitlabLintGlob(file.path) && AppSettings.instance.showMergedPreview
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        return EditorWithMergedPreview.create(file, project)
    }

    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.HIDE_DEFAULT_EDITOR
}

class EditorWithMergedPreview private constructor(
    editor: TextEditor,
    private val preview: TextEditor
) : TextEditorWithPreview(editor, preview) {

    fun setPreviewText(text: String) {
        preview.editor.document.setText(text)
    }
    companion object {
        fun create(file: VirtualFile, project: Project): EditorWithMergedPreview {
            val textEditorProvider = TextEditorProvider.getInstance()

            val mainEditor = textEditorProvider.createEditor(project, file) as TextEditor
            val editorFactory = EditorFactory.getInstance()

            val viewer = editorFactory.createEditor(
                editorFactory.createDocument(""),
                project,
                file.fileType,
                true
            )

            // Removes vertical line
            viewer.settings.isRightMarginShown = false

            Disposer.register(mainEditor) { editorFactory.releaseEditor(viewer) }

            val previewEditor = textEditorProvider.getTextEditor(viewer)

            return EditorWithMergedPreview(mainEditor, previewEditor)
        }
    }
}