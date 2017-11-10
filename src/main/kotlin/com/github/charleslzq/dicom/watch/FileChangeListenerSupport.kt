package com.github.charleslzq.dicom.watch

import java.nio.file.Path

abstract class FileChangeListenerSupport : FileChangeListener {

    override fun onCreate(basePath: Path, name: String) {

    }

    override fun onModify(basePath: Path, name: String) {

    }

    override fun onDelete(basePath: Path, name: String) {

    }

}