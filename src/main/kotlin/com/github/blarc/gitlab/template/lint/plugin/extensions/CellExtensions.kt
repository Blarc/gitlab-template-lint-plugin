package com.github.blarc.gitlab.template.lint.plugin.extensions

import com.intellij.ui.dsl.builder.Cell
import com.intellij.util.ui.ComponentWithEmptyText
import javax.swing.JComponent

// Adds emptyText method to all cells that contain a component that implements ComponentWithEmptyText class
fun <T>Cell<T>.emptyText(emptyText: String) : Cell<T> where T : JComponent, T : ComponentWithEmptyText {
    this.component.emptyText.text = emptyText
    return this
}