/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.mcp.aw.psi.mixins.impl

import com.demonwav.mcdev.platform.accessors.AccessModifier
import com.demonwav.mcdev.platform.mcp.aw.gen.psi.AwTypes
import com.demonwav.mcdev.platform.mcp.aw.psi.mixins.AwEntryMixin
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

abstract class AwEntryImplMixin(node: ASTNode) : ASTWrapperPsiElement(node), AwEntryMixin {

    override val accessKind: String?
        get() = findChildByType<PsiElement>(AwTypes.ACCESS)?.text

    override val targetClassName: String?
        get() = findChildByType<PsiElement>(AwTypes.CLASS_NAME)?.text
	
	override fun modifiers(): List<AccessModifier> {
		return when(accessKind){
			"accessible", "accessible-transitive" -> listOf(AccessModifier.TO_PUBLIC)
			"mutable", "mutable-transitive", "extendable", "extendable-transitive" -> listOf(AccessModifier.TO_NON_FINAL)
			else -> listOf()
		}
	}
}
