package com.github.blarc.gitlab.template.lint.plugin

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import org.jetbrains.yaml.YAMLFileType

class GitlabLintAction : AnAction(Icons.GITLAB_LOGO) {
    override fun actionPerformed(e: AnActionEvent) {
        e.dataContext.getData(PlatformDataKeys.PSI_FILE)?.let { file ->
            lint(file)
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)

        e.dataContext.getData(PlatformDataKeys.PSI_FILE)?.let { file ->
            e.presentation.isEnabledAndVisible = file.fileType == YAMLFileType.YML
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}