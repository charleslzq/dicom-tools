package com.github.charleslzq.dicom.spring

import com.github.charleslzq.dicom.DicomParseWorker
import com.github.charleslzq.dicom.watch.FileChangeListenerSupport
import java.nio.file.Path

class DicomFileListener(
        private val dicomParseWorker: DicomParseWorker
) : FileChangeListenerSupport() {

    override fun onCreate(path: Path) {
        dicomParseWorker.parse(path.toFile())
    }
}