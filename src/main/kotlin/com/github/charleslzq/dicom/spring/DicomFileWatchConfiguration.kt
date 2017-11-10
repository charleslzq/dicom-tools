package com.github.charleslzq.dicom.spring

import com.github.charleslzq.dicom.DicomParseWorker
import com.github.charleslzq.dicom.watch.FileWatcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.core.task.SimpleAsyncTaskExecutor
import java.nio.file.StandardWatchEventKinds

@Configuration
@ConditionalOnProperty(
        value = "dicom.watch.paths"
)
@EnableConfigurationProperties(DicomFileWatchProperties::class)
open class DicomFileWatchConfiguration {

    @Autowired
    private lateinit var dicomFileWatchProperties: DicomFileWatchProperties

    @Bean
    @ConditionalOnMissingBean(name = arrayOf("fileWatchExecutor"))
    open fun fileWatchExecutor(): AsyncTaskExecutor {
        return SimpleAsyncTaskExecutor()
    }

    @Bean
    open fun dicomFileWatchService(
            dicomParseWorker: DicomParseWorker,
            @Qualifier("fileWatchExecutor") asyncTaskExecutor: AsyncTaskExecutor
    ): FileWatcher {
        val listener = DicomFileListener(dicomParseWorker)
        var fileWatcher = FileWatcher(asyncTaskExecutor, dicomFileWatchProperties.autoStart)
        dicomFileWatchProperties.paths.forEach {
            fileWatcher.register(it, listener, StandardWatchEventKinds.ENTRY_CREATE)
        }
        return fileWatcher
    }
}