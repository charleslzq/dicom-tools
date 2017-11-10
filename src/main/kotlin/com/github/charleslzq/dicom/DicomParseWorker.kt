package com.github.charleslzq.dicom

import com.github.charleslzq.dicom.reader.DicomDataReader
import com.github.charleslzq.dicom.store.DicomDataStore
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.util.StopWatch
import java.io.File
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class DicomParseWorker(
        private val dicomDataReader: DicomDataReader,
        private val dicomDataStore: DicomDataStore,
        private val taskExecutor: AsyncTaskExecutor,
        private val imageTmpDir: String
) : InitializingBean {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val queue: BlockingQueue<File> = LinkedBlockingQueue()
    private var pause = true
    private val stopWatch = StopWatch()

    fun parse(dcmFile: File) {
        queue.put(dcmFile)
        log.info("Task for {} submitted", dcmFile.absolutePath)
    }

    private fun work() {
        while (!pause) {
            val dcmFile = queue.take()
            val path = dcmFile.absolutePath
            log.info("Start to parse {}", path)
            stopWatch.start(dcmFile.absolutePath)
            val dicomData = dicomDataReader.parse(dcmFile, imageTmpDir)
            dicomDataStore.saveDicomData(dicomData)
            dicomDataStore.loadMetaFile()
            stopWatch.stop()
            log.info("End parsing {} with {} milli-seconds", path, stopWatch.lastTaskTimeMillis)
            log.info("Complete {} task(s), using {} second(s), average {} second(s) per task",
                    stopWatch.taskCount, stopWatch.totalTimeSeconds, stopWatch.totalTimeSeconds / stopWatch.taskCount)
        }
    }

    override fun afterPropertiesSet() {
        taskExecutor.submit(this::work)
        pause = false
    }
}