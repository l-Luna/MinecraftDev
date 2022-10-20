/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.accessors.inspections

import com.demonwav.mcdev.platform.accessors.AccessControllerFile
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.psi.PsiFile

abstract class AbstractAccessControllerInspection : LocalInspectionTool() {

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is AccessControllerFile)
            return super.checkFile(file, manager, isOnTheFly)
        return checkAccessorFile(file, manager, isOnTheFly).toTypedArray()
    }

    abstract fun checkAccessorFile(file: AccessControllerFile, manager: InspectionManager, isOnTheFly: Boolean): List<ProblemDescriptor>
}
