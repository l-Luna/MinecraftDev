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
import com.demonwav.mcdev.platform.mcp.aw.psi.mixins.AwFieldEntryMixin
import com.demonwav.mcdev.util.MemberReference
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

abstract class AwFieldEntryImplMixin(node: ASTNode) : AwEntryImplMixin(node), AwFieldEntryMixin {
    override val fieldName: String?
        get() = findChildByType<PsiElement>(AwTypes.MEMBER_NAME)?.text

    override val fieldDescriptor: String?
        get() = findChildByType<PsiElement>(AwTypes.FIELD_DESC)?.text

    override fun target(): PsiElement? {
        val name = fieldName ?: return null
        val owner = targetClassName?.replace('/', '.')
        return MemberReference(name, null, owner).resolveMember(project, resolveScope)
    }
}
