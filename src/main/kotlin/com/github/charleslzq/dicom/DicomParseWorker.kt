package com.github.charleslzq.dicom

import com.github.charleslzq.dicom.reader.DicomDataReader
import com.github.charleslzq.dicom.store.DicomDataStore
import org.slf4j.LoggerFactory
import org.springframework.core.task.AsyncTaskExecutor
import java.io.File

class DicomParseWorker(
        private val dicomDataReader: DicomDataReader,
        private val dicomDataStore: DicomDataStore,
        private val taskExecutor: AsyncTaskExecutor,
        private val imageTmpDir: String
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun parse(dcmFile: File) {
        taskExecutor.submit {
            log.info("Start to parse {}", dcmFile.absolutePath)
            val dicomData = dicomDataReader.parse(dcmFile, imageTmpDir)
            dicomDataStore.saveDicomData(dicomData)
            dicomDataStore.loadMetaFile()
        }
    }

}