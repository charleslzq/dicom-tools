package com.github.charleslzq.dicom.watch

import java.nio.file.Path

abstract class FileChangeListenerSupport : FileChangeListener {

    override fun onCreate(path: Path) {

    }

    override fun onModify(path: Path) {

    }

    override fun onDelete(path: Path) {

    }
}