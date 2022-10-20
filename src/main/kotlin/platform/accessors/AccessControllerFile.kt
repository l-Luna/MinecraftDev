package com.demonwav.mcdev.platform.accessors

import com.demonwav.mcdev.platform.accessors.psi.AccessControlEntry
import com.jetbrains.rd.util.getOrCreate

/**
 * A file of an access controller language, exposing common features for use by inspections and tools.
 */
interface AccessControllerFile {
	
	fun entries(): List<AccessControlEntry>
	
	/*
	fun entriesByModifier(): Map<AccessModifier, List<AccessControlEntry>> {
		val entries = entries()
		val byModifiers = HashMap<AccessModifier, MutableList<AccessControlEntry>>(entries.size)
		for(entry in entries)
			for(modifier in entry.modifiers())
				(byModifiers.getOrCreate(modifier) { ArrayList() }) += entry
		return byModifiers
	}
	*/
	
	// fun addEntry...
}