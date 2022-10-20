/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.mcp.at

import com.demonwav.mcdev.platform.accessors.AccessControllerLanguage
import com.demonwav.mcdev.util.findMcpModule
import com.intellij.lang.Language
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod

object AtLanguage : Language("Access Transformers"), AccessControllerLanguage {
	
	override fun shortName(): String = "AT"
	
	override fun createEntryText(target: PsiElement): String? {
		return target.findMcpModule()?.srgManager?.srgMap?.then {
			return@then when(target) {
				is PsiClass -> it.getSrgClass(target)
				is PsiField -> {
					val containing = target.containingClass ?: return@then null
					val classSrg = it.getSrgClass(containing) ?: return@then null
					val srg = it.getSrgField(target) ?: return@then null
					return@then "$classSrg ${srg.name} # ${target.name}"
				}
				is PsiMethod -> {
					val containing = target.containingClass ?: return@then null
					val classSrg = it.getSrgClass(containing) ?: return@then null
					val srg = it.getSrgMethod(target) ?: return@then null
					return@then "$classSrg ${srg.name}${srg.descriptor} # ${target.name}"
				}
				else -> null
			}
		}?.blockingGet(1)
	}
}
