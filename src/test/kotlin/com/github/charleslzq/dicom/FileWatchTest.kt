package com.github.charleslzq.dicom

import com.github.charleslzq.dicom.watch.FileChangeListener
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
        val listener = SimpleListener(fileWathcer)
        val testDir = Paths.get(dir.absolutePath, "test").toFile()
        if (testDir.exists()) {
            testDir.deleteRecursively()
        }
        fileWathcer.register(dir.absolutePath, listener, StandardWatchEventKinds.ENTRY_CREATE)
        fileWathcer.start()
        testDir.mkdirs()
        Thread.sleep(1000)

        Files.copy(rawFile.toPath(), Paths.get(dir.absolutePath, "test", rawFile.name))

        Thread.sleep(1000)
        assertThat("检测到文件创建事件", listener.fileCreated)
    }

    class SimpleListener(
            private val fileWatcher: FileWatcher
    ) : FileChangeListener {
        var fileCreated = false

        override fun onCreate(basePath: Path, name: String) {
            val file = Paths.get(basePath.toFile().absolutePath, name).toFile()
            if (file.isDirectory) {
                fileWatcher.register(file.absolutePath, this, StandardWatchEventKinds.ENTRY_CREATE)
            } else {
                fileCreated = true
            }

        }
    }
}