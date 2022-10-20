/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.mcp.aw.psi.mixins

import com.demonwav.mcdev.platform.accessors.psi.AccessControlEntry
import com.demonwav.mcdev.platform.mcp.aw.psi.AwElement

interface AwEntryMixin : AwElement, AccessControlEntry {

    val accessKind: String?
    val targetClassName: String?
}
