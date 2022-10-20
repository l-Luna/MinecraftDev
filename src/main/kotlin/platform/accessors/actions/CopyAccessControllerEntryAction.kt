/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.accessors.actions

import com.demonwav.mcdev.platform.accessors.AccessControllerLanguage
import com.demonwav.mcdev.platform.mcp.actions.SrgActionBase
import com.demonwav.mcdev.platform.mcp.actions.SrgActionBase.Companion.showBalloon
import com.demonwav.mcdev.util.getDataFromActionEvent
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.VisualPosition
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMember
import com.intellij.psi.PsiReference
import com.intellij.ui.awt.RelativePoint
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class CopyAccessControllerEntryAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val data = getDataFromActionEvent(e) ?: return showBalloon("Unknown failure", e)
        val editor = data.editor
        val element = data.element

        // check for the existence of an AT/AW file
        val lang = findAppropriateLanguage()
        if (lang == null) {
            val pos = editor.offsetToVisualPosition(element.textRange.startOffset)
            val at = RelativePoint(
                editor.contentComponent,
                editor.visualPositionToXY(VisualPosition(pos.line + 1, pos.column))
            )

            AccessControllerLanguage.languageChooser {
                if (it != null) {
                    performWithChosenLang(e, it)
                }
            }.show(at)
        } else {
            performWithChosenLang(e, lang)
        }
    }

    private fun performWithChosenLang(e: AnActionEvent, lang: AccessControllerLanguage) {
        val data = getDataFromActionEvent(e) ?: return showBalloon("Unknown failure", e)
        val editor = data.editor

        val element = data.element
        if (element !is PsiIdentifier) {
            showBalloon("Invalid element", e)
            return
        }

        val target = when (val parent = element.parent) {
            is PsiMember -> parent
            is PsiReference -> parent.resolve()
            else -> null
        } ?: return showBalloon("Invalid element", e)

        doCopy(target, element, editor, lang, e)
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        val langName = findAppropriateLanguage()?.shortName() ?: "AT/AW"
        e.presentation.description = "$langName entry"
    }

    fun findAppropriateLanguage(): AccessControllerLanguage? = null // TODO: find appropriate type!

    companion object {

        fun doCopy(
            target: PsiElement,
            element: PsiElement,
            editor: Editor?,
            lang: AccessControllerLanguage,
            e: AnActionEvent?
        ) {
            val text = lang.createEntryText(target) ?: return maybeShow("Could not create entry", e)
            copyToClipboard(editor, element, text)
        }

        private fun copyToClipboard(editor: Editor?, element: PsiElement, text: String) {
            val stringSelection = StringSelection(text)
            val clpbrd = Toolkit.getDefaultToolkit().systemClipboard
            clpbrd.setContents(stringSelection, null)
            if (editor != null) {
                SrgActionBase.showSuccessBalloon(editor, element, "Copied: \"$text\"")
            }
        }

        private fun maybeShow(text: String, e: AnActionEvent?) {
            if (e != null)
                showBalloon(text, e)
        }
    }
}
