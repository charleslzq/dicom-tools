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
    private val listeners: MutableMap<WatchKey, FileChangeListener> = Maps.newConcurrentMap()
    var pause: AtomicBoolean = AtomicBoolean(true)

    fun register(path: String, listener: FileChangeListener, vararg events: WatchEvent.Kind<Path>) {
        val dir = File(path)
        if (dir.exists() && dir.isDirectory) {
            val pathToMonitor = dir.toPath()
            if (events.isNotEmpty()) {
                val key = pathToMonitor.register(watchService, events)
                listeners.put(key, listener)
            } else {
                log.warn("No evnet configured for {}, will not register", path)
            }
        } else {
            log.warn("Target not exist or not directory: {}", path)
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
    }

    override fun getPhase(): Int {
        return 0
    }

    private fun work() {
        while (!pause.get()) {
            val key = watchService.take()
            val listener = listeners[key]
            if (key.isValid && listener != null) {
                key.pollEvents().forEach {
                    val kind = it.kind()
                    val context = it.context()
                    if (context is Path) {
                        when (kind) {
                            StandardWatchEventKinds.ENTRY_CREATE -> listener.onCreate(context)
                            StandardWatchEventKinds.ENTRY_MODIFY -> listener.onModify(context)
                            StandardWatchEventKinds.ENTRY_DELETE -> listener.onDelete(context)
                        }
                    }
                }
            }
        }
    }


}