package com.github.blarc.gitlab.template.lint.plugin.settings

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.FormBuilder
import org.jetbrains.annotations.NotNull
import javax.swing.JComponent
import javax.swing.JPanel

class AppSettingsComponent {
    val panel: JPanel

    private val gitlabTokenField = JBPasswordField()

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Gitlab token: "), gitlabTokenField, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    val preferredFocusedComponent: JComponent
        get() = gitlabTokenField

    @get:NotNull
    var gitlabToken: CharArray?
        get() = gitlabTokenField.password
        set(newText) {
            gitlabTokenField.text = newText.toString()
        }
}
