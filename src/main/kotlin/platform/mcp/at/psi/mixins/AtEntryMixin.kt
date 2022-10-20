/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.mcp.at.psi.mixins

import com.demonwav.mcdev.facet.MinecraftFacet
import com.demonwav.mcdev.platform.accessors.AccessModifier
import com.demonwav.mcdev.platform.accessors.psi.AccessControlEntry
import com.demonwav.mcdev.platform.mcp.McpModuleType
import com.demonwav.mcdev.platform.mcp.at.AtElementFactory
import com.demonwav.mcdev.platform.mcp.at.AtMemberReference
import com.demonwav.mcdev.platform.mcp.at.gen.psi.AtAsterisk
import com.demonwav.mcdev.platform.mcp.at.gen.psi.AtClassName
import com.demonwav.mcdev.platform.mcp.at.gen.psi.AtEntry
import com.demonwav.mcdev.platform.mcp.at.gen.psi.AtFieldName
import com.demonwav.mcdev.platform.mcp.at.gen.psi.AtFunction
import com.demonwav.mcdev.platform.mcp.at.gen.psi.AtKeyword
import com.demonwav.mcdev.platform.mcp.at.psi.AtElement
import com.demonwav.mcdev.util.findQualifiedClass
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiElement

interface AtEntryMixin : AtElement, AccessControlEntry {

    val asterisk: AtAsterisk?
    val className: AtClassName
    val fieldName: AtFieldName?
    val function: AtFunction?
    val keyword: AtKeyword

    fun setEntry(entry: String)
    fun setKeyword(keyword: AtElementFactory.Keyword)
    fun setClassName(className: String)
    fun setFieldName(fieldName: String)
    fun setFunction(function: String)
    fun setAsterisk()

    fun replaceMember(element: AtElement) {
        // One of these must be true
        when {
            fieldName != null -> fieldName!!.replace(element)
            function != null -> function!!.replace(element)
            asterisk != null -> asterisk!!.replace(element)
            else -> addAfter(className, element)
        }
    }

    override fun modifiers(): List<AccessModifier> {
        val modifiers = ArrayList<AccessModifier>(2)
        var kwText = keyword.keywordValue.text
        if (kwText.endsWith("+f"))
            modifiers += AccessModifier.TO_FINAL
        else if (kwText.endsWith("-f"))
            modifiers += AccessModifier.TO_NON_FINAL

        if (modifiers.size > 0)
            kwText = kwText.substring(2)
        when (kwText) {
            "private" -> modifiers += AccessModifier.TO_PRIVATE
            "protected" -> modifiers += AccessModifier.TO_PROTECTED
            "default" -> modifiers += AccessModifier.TO_PACKAGE_LOCAL
            "public" -> modifiers += AccessModifier.TO_PUBLIC
        }

        return modifiers
    }

    override fun target(): PsiElement? {
        // TODO: deduplicate with AtGotoDeclarationHandler
        val module = ModuleUtilCore.findModuleForPsiElement(this) ?: return null
        val instance = MinecraftFacet.getInstance(module) ?: return null
        val mcpModule = instance.getModuleOfType(McpModuleType) ?: return null
        val srgMap = mcpModule.srgManager?.srgMapNow ?: return null

        return when {
            fieldName != null -> srgMap.mapToMcpField(AtMemberReference.get(this as AtEntry, fieldName!!) ?: return null).resolveMember(project)
            function != null -> srgMap.mapToMcpMethod(AtMemberReference.get(this as AtEntry, function!!) ?: return null).resolveMember(project)
            else -> findQualifiedClass(project, srgMap.mapToMcpClass(className.classNameText))
        }
    }
}
