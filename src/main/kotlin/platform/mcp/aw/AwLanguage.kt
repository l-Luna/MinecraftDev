/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.mcp.aw

import com.demonwav.mcdev.platform.accessors.AccessControllerLanguage
import com.demonwav.mcdev.util.descriptor
import com.demonwav.mcdev.util.internalName
import com.intellij.lang.Language
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod

object AwLanguage : Language("Access Widener"), AccessControllerLanguage {

    override fun shortName(): String = "AW"

    override fun createEntryText(target: PsiElement): String? = when (target) {
        is PsiClass -> "accessible class ${target.internalName}"
        is PsiField -> {
            val containing = target.containingClass?.internalName
            val desc = target.type.descriptor
            "accessible field $containing ${target.name} $desc"
        }
        is PsiMethod -> {
            val containing = target.containingClass?.internalName
            val desc = target.descriptor
            "accessible method $containing ${target.name} $desc"
        }
        else -> null
    }
}
