/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.accessors.psi

import com.demonwav.mcdev.platform.accessors.AccessModifier
import com.intellij.psi.PsiElement

interface AccessControlEntry : PsiElement {

    fun target(): PsiElement?

    fun modifiers(): List<AccessModifier>
}
