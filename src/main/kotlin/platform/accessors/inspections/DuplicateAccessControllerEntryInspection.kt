package com.demonwav.mcdev.platform.accessors.inspections

import com.demonwav.mcdev.platform.accessors.AccessControllerFile
import com.demonwav.mcdev.platform.accessors.AccessModifier
import com.demonwav.mcdev.platform.accessors.psi.AccessControlEntry
import com.demonwav.mcdev.platform.mcp.aw.psi.mixins.AwEntryMixin
import com.demonwav.mcdev.platform.mcp.aw.psi.mixins.AwMemberNameMixin
import com.demonwav.mcdev.util.childOfType
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.jetbrains.rd.util.getOrCreate
import org.jetbrains.plugins.groovy.codeInspection.fixes.RemoveElementQuickFix

class DuplicateAccessControllerEntryInspection : AbstractAccessControllerInspection() {
	
	override fun checkAccessorFile(
		file: AccessControllerFile,
		manager: InspectionManager,
		isOnTheFly: Boolean
	): List<ProblemDescriptor> {
		val collected = HashMap<Pair<PsiElement, AccessModifier>, MutableList<AccessControlEntry>>()
		file.entries().forEach {
			val target = it.target()
			val accessKind = it.modifiers()
			if (target != null)
				for(modifier in accessKind)
					(collected.getOrCreate(Pair(target, modifier)) { ArrayList() }) += it
		}
		val problems = ArrayList<ProblemDescriptor>()
		collected.forEach { (sort, matches) ->
			if (sort.first is PsiNamedElement)
				if (matches.size > 1)
					for (match in matches)
						problems += manager.createProblemDescriptor(
							match,
							"Duplicate entry for \"${sort.second}  ${(sort.first as PsiNamedElement).name}\"",
							RemoveElementQuickFix("Remove duplicate"),
							ProblemHighlightType.WARNING,
							isOnTheFly
						)
		}
		return problems
	}
	
	override fun runForWholeFile(): Boolean = true
	
	override fun getDisplayName(): String = "Duplicate accessor controller entry"
	
	override fun getStaticDescription(): String =
		"Warns when the same element has its accessibility, mutability, or extensibility changed multiple times in one file."
}