package com.demonwav.mcdev.platform.accessors

/**
 * Marks a language as an access controller language and falling under [AccessorMetaLanguage].
 *
 * This language's [com.intellij.psi.PsiFile] implementation should implement [AccessControllerFile].
 */
interface AccessControllerLanguage

// fun createEntryText...