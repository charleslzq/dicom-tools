package com.github.charleslzq.dicom.store

import java.io.File

class Path(private val absolutePath: String) {
    fun toFile() = File(absolutePath)
}