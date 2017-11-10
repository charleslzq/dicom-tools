package com.github.charleslzq.dicom

import com.github.charleslzq.dicom.reader.DicomDataReader
import com.github.charleslzq.dicom.store.DicomDataStore
import org.slf4j.LoggerFactory
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.util.StopWatch
import java.io.File

class DicomParseWorker(
        private val dicomDataReader: DicomDataReader,
        private val dicomDataStore: DicomDataStore,
        private val taskExecutor: AsyncTaskExecutor,
        private val imageTmpDir: String
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val stopWatch = StopWatch()

    fun parse(dcmFile: File) {
        taskExecutor.submit {
            val path = dcmFile.absolutePath
            log.info("Start to parse {}", path)
            stopWatch.start(path)
            val dicomData = dicomDataReader.parse(dcmFile, imageTmpDir)
            dicomDataStore.saveDicomData(dicomData)
            dicomDataStore.loadMetaFile()
            stopWatch.stop()
            log.info("End parsing {} with {} milli-seconds", path, stopWatch.lastTaskTimeMillis)
            log.info("Complete {} task(s), using {} second(s), average {} second(s) per task",
                    stopWatch.taskCount, stopWatch.totalTimeSeconds, stopWatch.totalTimeSeconds / stopWatch.taskCount)
        }
    }

}