package com.demonwav.mcdev.platform.mcp.aw.inlays

import com.demonwav.mcdev.facet.MinecraftFacet
import com.demonwav.mcdev.platform.fabric.FabricModuleType
import com.demonwav.mcdev.platform.mcp.aw.AwFile
import com.demonwav.mcdev.platform.mcp.aw.gen.psi.AwClassEntry
import com.demonwav.mcdev.platform.mcp.aw.gen.psi.AwClassName
import com.demonwav.mcdev.platform.mcp.aw.psi.mixins.AwClassEntryMixin
import com.demonwav.mcdev.platform.mcp.aw.psi.mixins.AwEntryMixin
import com.demonwav.mcdev.platform.mcp.aw.psi.mixins.AwMemberNameMixin
import com.demonwav.mcdev.util.childOfType
import com.demonwav.mcdev.util.findModule
import com.intellij.codeInsight.hints.ChangeListener
import com.intellij.codeInsight.hints.FactoryInlayHintsCollector
import com.intellij.codeInsight.hints.ImmediateConfigurable
import com.intellij.codeInsight.hints.InlayHintsCollector
import com.intellij.codeInsight.hints.InlayHintsProvider
import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.codeInsight.hints.NoSettings
import com.intellij.codeInsight.hints.SettingsKey
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.editor.BlockInlayPriority
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiMember
import com.intellij.refactoring.suggested.startOffset
import com.jetbrains.rd.util.getOrCreate
import javax.swing.JPanel

@Suppress("UnstableApiUsage")
class AwAffectedInlayProvider : InlayHintsProvider<NoSettings> {
	
	override val key: SettingsKey<NoSettings>
		get() = settingsKey
	override val name: String
		get() = "Member affected by access widener"
	override val previewText: String?
		get() = null
	
	override fun createSettings(): NoSettings = NoSettings()
	
	override fun getCollectorFor(
		file: PsiFile,
		editor: Editor,
		settings: NoSettings,
		sink: InlayHintsSink
	): InlayHintsCollector? {
		val module = file.findModule() ?: return null
		val fabricModule = MinecraftFacet.getInstance(module, FabricModuleType) ?: return null
		val accessWidener = fabricModule.accessWidenerFile ?: return null
		if (file.fileType != JavaFileType.INSTANCE) {
			return null
		}
		val awFile = (PsiManager.getInstance(file.project).findFile(accessWidener) as? AwFile) ?: return null
		
		return Collector(awFile, editor)
	}
	
	override fun createConfigurable(settings: NoSettings): ImmediateConfigurable = object : ImmediateConfigurable {
		override fun createComponent(listener: ChangeListener) = JPanel()
	}
	
	private class Collector(val awFile: AwFile, val editor: Editor)
		: FactoryInlayHintsCollector(editor) {
		
		override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
			val elements = HashMap<PsiElement, MutableList<AwEntryMixin>>()
			for (entry in awFile.entries) {
				/*val target = entry.reference?.resolve()*/
				val target: PsiElement? =
					if(entry is AwClassEntry)
						entry.className.resolve()
					else
						entry.childOfType<AwMemberNameMixin>()?.resolve()
				if (target != null)
					(elements.getOrCreate(target) { ArrayList() }) += entry
			}
			if (element is PsiMember) {
				elements.entries.firstOrNull{it.key.isEquivalentTo(element)}?.let {kv ->
					val entries = kv.value
					val text: String = entries.mapNotNull{it.accessKind}.joinToString(separator = " "){it}
					val offset = element.startOffset
					val document = editor.document
					val presentation = with(factory) {
						val base = roundWithBackground(psiSingleReference(smallText(text)) {entries[0]})
						// offset to the member
						// see AnnotationInlayProvider for reference
						val width = EditorUtil.getPlainSpaceWidth(editor)
						val line = document.getLineNumber(offset)
						val startOffset = document.getLineStartOffset(line)
						val column = offset - startOffset
						inset(base, left = column * width)
					}
					sink.addBlockElement(offset, false, true, BlockInlayPriority.ANNOTATIONS, presentation)
				}
			}
			return true
		}
	}
	
	companion object{
		
		private val settingsKey = SettingsKey<NoSettings>("AwAffectedInlayProviderKey")
	}
}