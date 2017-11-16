package com.github.charleslzq.dicom.store

import java.io.File

object Paths {
    fun get(vararg path: String?): Path {
        return Path(path.filterNotNull().joinToString(File.separator))
    }
}