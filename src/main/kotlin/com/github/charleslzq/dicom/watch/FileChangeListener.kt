package com.github.charleslzq.dicom.watch

import java.nio.file.Path

interface FileChangeListener {
    fun onCreate(basePath: Path, name: String)
    fun onModify(basePath: Path, name: String)
    fun onDelete(basePath: Path, name: String)
}