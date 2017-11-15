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
        private val imageTmpDir: String,
        private val acceptFormats: List<String>,
        private val retry: Int
) : InitializingBean {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val queue: BlockingQueue<File> = LinkedBlockingQueue()
    private var pause = true
    private val taskList: MutableList<StopWatch.TaskInfo> = emptyList<StopWatch.TaskInfo>().toMutableList()
    private val failedFiles: MutableMap<File, Int> = emptyMap<File, Int>().toMutableMap()

    fun parse(dcmFile: File) {
        if (dcmFile.exists() && dcmFile.isFile && acceptFormats.contains(dcmFile.extension)) {
            queue.put(dcmFile)
            log.info("Task for ${dcmFile.absolutePath} submitted")
        }
    }

    private fun work() {
        while (!pause) {
            val dcmFile = queue.take()
            val path = dcmFile.absolutePath
            val failed = failedFiles.getOrDefault(dcmFile, 0)
            try {
                when (failed) {
                    0 -> log.info("Start to parse $path", path)
                    else -> log.info("Retry parsing $path with $failed failed attempt(s)")
                }
                val stopWatch = StopWatch()
                stopWatch.start(dcmFile.absolutePath)
                val dicomData = dicomDataReader.parse(dcmFile, imageTmpDir)
                dicomDataStore.saveDicomData(dicomData)
                dicomDataStore.reload()
                stopWatch.stop()
                taskList.add(stopWatch.lastTaskInfo)
                log.info("End parsing $path with ${stopWatch.lastTaskTimeMillis} milli-seconds, $failed fail(s)")
                logSummary()
                failedFiles.remove(dcmFile)
            } catch (throwable: Throwable) {
                when (failed) {
                    in 0..retry -> {
                        log.error("Error occur, skip parsing $path and retry later", throwable)
                        failedFiles.put(dcmFile, failed + 1)
                        queue.put(dcmFile)
                    }
                    else -> {
                        log.error("Retry limit reached, give up parsing $path", throwable)
                        failedFiles.remove(dcmFile)
                    }
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
            log.info("Complete ${taskList.size} task(s), using $totalTime second(s), average ${totalTime / taskList.size} second(s) per task")
        }
    }
}