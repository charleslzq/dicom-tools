package com.github.charleslzq.dicom

import com.github.charleslzq.dicom.reader.DicomDataReader
import com.github.charleslzq.dicom.store.DicomDataStore
import com.google.common.collect.Lists
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
    private val taskList: MutableList<StopWatch.TaskInfo> = Lists.newArrayList()

    fun parse(dcmFile: File) {
        queue.put(dcmFile)
        log.info("Task for {} submitted", dcmFile.absolutePath)
    }

    private fun work() {
        while (!pause) {
            try {
                val dcmFile = queue.take()
                val path = dcmFile.absolutePath
                log.info("Start to parse {}", path)
                val stopWatch = StopWatch()
                stopWatch.start(dcmFile.absolutePath)
                val dicomData = dicomDataReader.parse(dcmFile, imageTmpDir)
                dicomDataStore.saveDicomData(dicomData)
                dicomDataStore.reload()
                stopWatch.stop()
                taskList.add(stopWatch.lastTaskInfo)
                log.info("End parsing {} with {} milli-seconds", path, stopWatch.lastTaskTimeMillis)
                logSummary()
            } catch (throwable: Throwable) {
                log.error("Error occur, skip parsing current file", throwable)
            }
        }
    }

    override fun afterPropertiesSet() {
        taskExecutor.submit(this::work)
        pause = false
    }

    private fun logSummary() {
        if (taskList.isNotEmpty()) {
            val totalTime = taskList.map { it.timeSeconds }.sum()
            log.info("Complete {} task(s), using {} second(s), average {} second(s) per task",
                    taskList.size, totalTime, totalTime / taskList.size)
        }
    }
}