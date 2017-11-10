package com.github.charleslzq.dicom

import com.github.charleslzq.dicom.watch.FileChangeListenerSupport
import com.github.charleslzq.dicom.watch.FileWatcher
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.springframework.core.task.SimpleAsyncTaskExecutor
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds

class FileWatchTest {
    private val watchBase = "src/test/resources/watch"
    private val rawFilePath = "classpath:image-000001.dcm"

    @Before
    fun setup() {
        val dir = File(watchBase)
        if (dir.exists()) {
            dir.deleteRecursively()
        }
        dir.mkdirs()
    }

    @Test
    fun testFileChangeCapturedSuccess() {
        val dir = File(watchBase)
        val rawFile = TestUtil.readFile(rawFilePath)
        val fileWathcer = FileWatcher(SimpleAsyncTaskExecutor(), true)
        val listener = SimpleListener()
        fileWathcer.register(dir.absolutePath, listener, StandardWatchEventKinds.ENTRY_CREATE)
        fileWathcer.start()
        Files.copy(rawFile.toPath(), Paths.get(dir.absolutePath, rawFile.name))

        Thread.sleep(1000)
        assertThat("检测到文件创建事件", listener.fileCreated)
    }

    class SimpleListener : FileChangeListenerSupport() {
        var fileCreated = false

        override fun onCreate(path: Path) {
            println(path)
            fileCreated = true
        }
    }
}