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

import com.demonwav.mcdev.platform.mcp.aw.gen.psi.AwTypes
import com.demonwav.mcdev.platform.mcp.aw.psi.mixins.AwMethodEntryMixin
import com.demonwav.mcdev.util.MemberReference
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

abstract class AwMethodEntryImplMixin(node: ASTNode) : AwEntryImplMixin(node), AwMethodEntryMixin {
    override val methodName: String?
        get() = findChildByType<PsiElement>(AwTypes.MEMBER_NAME)?.text

    override val methodDescriptor: String?
        get() = findChildByType<PsiElement>(AwTypes.METHOD_DESC)?.text
	
	override fun target(): PsiElement? {
		val name = methodName ?: return null
		val desc = methodDescriptor
		val owner = targetClassName?.replace('/', '.')
		return MemberReference(name, desc, owner).resolveMember(project, resolveScope)
			// fallback if descriptor is invalid
			?: MemberReference(name, null, owner).resolveMember(project, resolveScope)
	}
}
