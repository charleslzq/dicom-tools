package com.github.charleslzq.dicom.spring

import com.github.charleslzq.dicom.DicomParseWorker
import com.github.charleslzq.dicom.watch.FileChangeListener
import java.nio.file.Path
import java.nio.file.Paths

class DicomFileListener(
        private val dicomParseWorker: DicomParseWorker
) : FileChangeListener {

    override fun onCreate(basePath: Path, name: String) {
        val file = Paths.get(basePath.toFile().absolutePath, name).toFile()
        if (file.exists() && file.isFile && !file.name.startsWith(".")) {
            dicomParseWorker.parse(file)
        }
    }
}