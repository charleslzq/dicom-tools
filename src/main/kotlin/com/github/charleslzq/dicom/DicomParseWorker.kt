package com.github.charleslzq.dicom

import com.github.charleslzq.dicom.reader.DicomDataReader
import com.github.charleslzq.dicom.store.DicomDataStore
import com.google.common.collect.Lists
import com.google.common.collect.Maps
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
        private val imageTmpDir: String,
        private val acceptFormats: List<String>,
        private val retry: Int
) : InitializingBean {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val queue: BlockingQueue<File> = LinkedBlockingQueue()
    private var pause = true
    private val taskList: MutableList<StopWatch.TaskInfo> = Lists.newArrayList()
    private val failedFiles: MutableMap<File, Int> = Maps.newConcurrentMap()

    fun parse(dcmFile: File) {
        if (dcmFile.exists() && dcmFile.isFile && acceptFormats.contains(dcmFile.extension)) {
            queue.put(dcmFile)
            log.info("Task for {} submitted", dcmFile.absolutePath)
        }
    }

    private fun work() {
        while (!pause) {
            val dcmFile = queue.take()
            val failed = failedFiles.getOrDefault(dcmFile, 0)
            try {
                val path = dcmFile.absolutePath
                when (failed) {
                    0 -> log.info("Start to parse {}", path)
                    else -> log.info("Retry parsing {} with {} failed attempt(s)", path, failed)
                }
                val stopWatch = StopWatch()
                stopWatch.start(dcmFile.absolutePath)
                val dicomData = dicomDataReader.parse(dcmFile, imageTmpDir)
                dicomDataStore.saveDicomData(dicomData)
                dicomDataStore.reload()
                stopWatch.stop()
                taskList.add(stopWatch.lastTaskInfo)
                log.info("End parsing {} with {} milli-seconds, {} fail(s)", path, stopWatch.lastTaskTimeMillis, failed)
                logSummary()
                failedFiles.remove(dcmFile)
            } catch (throwable: Throwable) {
                when (failed) {
                    in 0..retry -> {
                        log.error("Error occur, skip parsing current file and retry later", throwable)
                        failedFiles.put(dcmFile, failed + 1)
                        queue.put(dcmFile)
                    }
                    else -> log.error("Retry limit reached, giving up", throwable)
                }
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