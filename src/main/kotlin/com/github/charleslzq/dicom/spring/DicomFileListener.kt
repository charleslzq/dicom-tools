package com.github.charleslzq.dicom.spring

import com.github.charleslzq.dicom.DicomParseWorker
import com.github.charleslzq.dicom.watch.FileChangeListenerSupport
import com.github.charleslzq.dicom.watch.FileWatcher
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds

class DicomFileListener(
        private val dicomParseWorker: DicomParseWorker,
        private val fileWatcher: FileWatcher
) : FileChangeListenerSupport() {

    override fun onCreate(basePath: Path, name: String) {
        val file = Paths.get(basePath.toFile().absolutePath, name).toFile()
        when (file.isFile) {
            true -> dicomParseWorker.parse(file)
            false -> {
                fileWatcher.register(file.absolutePath, this, StandardWatchEventKinds.ENTRY_CREATE)
                file.listFiles().filter { it != null && it.isFile }.forEach(dicomParseWorker::parse)
            }
        }
    }
}