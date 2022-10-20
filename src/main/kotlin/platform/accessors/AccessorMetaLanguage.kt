/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.accessors

import com.intellij.lang.Language
import com.intellij.lang.MetaLanguage

/**
 * Acts as a language that consists of all access controller files (ATs and AWs).
 */
class AccessorMetaLanguage : MetaLanguage("MC Access Controllers") {

    override fun matchesLanguage(language: Language): Boolean = language is AccessControllerLanguage
}
