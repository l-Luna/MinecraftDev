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

import com.demonwav.mcdev.platform.mcp.aw.psi.mixins.AwClassEntryMixin
import com.demonwav.mcdev.util.findQualifiedClass
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

abstract class AwClassEntryImplMixin(node: ASTNode) : AwEntryImplMixin(node), AwClassEntryMixin {
	
	override fun target(): PsiElement? {
		val targetName = targetClassName ?: return null
		return findQualifiedClass(targetName.replace('/', '.'), this)
	}
}
