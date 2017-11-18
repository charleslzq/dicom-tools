package com.github.charleslzq.dicom.spring

import com.github.charleslzq.dicom.DicomParseWorker
import com.github.charleslzq.dicom.watch.FileWatcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds

@Configuration
@ConditionalOnProperty(
        value = "dicom.watch.enable"
)
@EnableConfigurationProperties(DicomFileWatchProperties::class)
open class DicomFileWatchConfiguration {

    @Autowired
    private lateinit var dicomFileWatchProperties: DicomFileWatchProperties

    @Autowired
    private lateinit var dicomParseConfigurer: DicomParseConfigurer

    @Bean
    open fun dicomFileWatchService(
            dicomParseWorker: DicomParseWorker
    ): FileWatcher {
        var fileWatcher = FileWatcher(dicomParseConfigurer.fileWatchExecutor(), dicomFileWatchProperties.autoStart)
        val listener = DicomFileListener(dicomParseWorker)
        dicomFileWatchProperties.paths.forEach {
            Paths.get(it).toFile().mkdirs()
            fileWatcher.register(it, listener, StandardWatchEventKinds.ENTRY_CREATE)
        }
        return fileWatcher
    }
}