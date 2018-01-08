package com.github.charleslzq.dicom.spring

import com.github.charleslzq.dicom.DicomParseWorker
import com.github.charleslzq.dicom.data.ImageMeta
import com.github.charleslzq.dicom.data.Meta
import com.github.charleslzq.dicom.watch.FileChangeListener
import java.nio.file.Path
import java.nio.file.Paths

class DicomFileListener<P : Meta, T : Meta, E : Meta, I : ImageMeta>(
        private val dicomParseWorker: DicomParseWorker<P, T, E, I>
) : FileChangeListener {

    override fun onCreate(basePath: Path, name: String) {
        val file = Paths.get(basePath.toFile().absolutePath, name).toFile()
        if (file.exists() && file.isFile && !file.name.startsWith(".")) {
            dicomParseWorker.parse(file)
        }
    }
}