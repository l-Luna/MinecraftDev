package com.demonwav.mcdev.platform.accessors

import com.intellij.lang.Language
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.JBPopupListener
import com.intellij.openapi.ui.popup.LightweightWindowEvent
import com.intellij.psi.PsiElement
import java.util.function.Consumer

/**
 * Marks a language as an access controller language and falling under [AccessorMetaLanguage].
 *
 * This language's [com.intellij.psi.PsiFile] implementation should implement [AccessControllerFile].
 */
interface AccessControllerLanguage {
	
	/**
	 * Returns the short name of this file type, e.g. "AT" or "AW", for use in user-facing strings.
	 */
	fun shortName(): String
	
	/**
	 * Generates an entry for the given element. This should be syntactically correct if
	 * pasted into a file of this type, making the target element visible and/or non-final
	 * as appropriate.
	 */
	fun createEntryText(target: PsiElement): String?
	
	companion object {
		
		/**
		 * Returns a list of all access controller languages registered.
		 */
		fun allLanguages(): List<AccessControllerLanguage> =
			Language.findInstance(AccessorMetaLanguage::class.java).matchingLanguages.filterIsInstance<AccessControllerLanguage>()
		
		/**
		 * Returns a popup that allows the user to choose between a registered
		 * access controller language.
		 */
		fun languageChooser(callback: Consumer<AccessControllerLanguage?>): JBPopup {
			val names = allLanguages().filterIsInstance<Language>().map { it.displayName }
			var chosen: AccessControllerLanguage? = null
			return JBPopupFactory.getInstance().createPopupChooserBuilder(names)
				.setItemChosenCallback {
					chosen = allLanguages()[names.indexOf(it)]
				}.addListener(object: JBPopupListener {
					override fun onClosed(event: LightweightWindowEvent) {
						getApplication().invokeLater {
							callback.accept(chosen)
						}
					}
				}).createPopup()
		}
	}
}