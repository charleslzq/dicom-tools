package com.github.charleslzq.dicom.watch

import com.google.common.collect.Maps
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.SmartLifecycle
import org.springframework.core.task.AsyncTaskExecutor
import java.io.File
import java.nio.file.*
import java.util.concurrent.atomic.AtomicBoolean

class FileWatcher(private val taskExecutor: AsyncTaskExecutor, private var autoStart: Boolean) : SmartLifecycle, InitializingBean {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val watchService = FileSystems.getDefault().newWatchService()
    private val watchDirs: MutableMap<WatchKey, Pair<Path, FileChangeListener>> = Maps.newConcurrentMap()
    private var pause: AtomicBoolean = AtomicBoolean(true)

    fun register(path: String, listener: FileChangeListener, vararg events: WatchEvent.Kind<Path>) {
        val dir = File(path)
        register(dir, listener, *events)
    }

    fun register(dir: File, listener: FileChangeListener, vararg events: WatchEvent.Kind<Path>) {
        if (dir.exists() && dir.isDirectory) {
            val pathToMonitor = dir.toPath()
            if (events.isNotEmpty()) {
                val key = pathToMonitor.register(watchService, events)
                watchDirs.put(key, Pair(dir.toPath(), listener))
                log.info("Start to monitor directory ${dir.absolutePath}")
            } else {
                log.warn("No evnet configured for ${dir.absolutePath}, will not register")
            }
        } else {
            log.warn("Target not exist or not directory: ${dir.absolutePath}")
        }
    }

    override fun afterPropertiesSet() {
        if (this.isAutoStartup) {
            this.start()
        }
    }

    override fun isRunning(): Boolean {
        return !pause.get()
    }

    override fun start() {
        if (pause.get()) {
            taskExecutor.submit(this::work)
            pause.set(false)
            log.info("File Watcher started")
        }
    }

    override fun isAutoStartup(): Boolean {
        return autoStart
    }

    override fun stop(callback: Runnable?) {
        stop()
        callback?.run()
    }

    override fun stop() {
        pause.set(true)
        log.info("File Watcher stopped")
    }

    override fun getPhase(): Int {
        return 0
    }

    private fun work() {
        while (!pause.get()) {
            val key = watchService.take()
            val pair = watchDirs[key]
            if (key.isValid && pair != null) {
                key.pollEvents().forEach {
                    val kind = it.kind()
                    val context = it.context()
                    if (context is Path) {
                        when (kind) {
                            StandardWatchEventKinds.ENTRY_CREATE -> pair.second.onCreate(pair.first, context.toString())
                            StandardWatchEventKinds.ENTRY_MODIFY -> pair.second.onModify(pair.first, context.toString())
                            StandardWatchEventKinds.ENTRY_DELETE -> pair.second.onDelete(pair.first, context.toString())
                        }
                    }
                }
            }
            key.reset()
        }
    }


}