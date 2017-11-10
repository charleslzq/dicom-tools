package com.github.charleslzq.dicom.watch

import java.nio.file.Path

interface FileChangeListener {
    fun onCreate(path: Path)
    fun onModify(path: Path)
    fun onDelete(path: Path)
}