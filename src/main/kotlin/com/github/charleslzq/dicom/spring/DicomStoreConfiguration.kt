package com.github.charleslzq.dicom.spring

import com.github.charleslzq.dicom.store.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.core.task.SimpleAsyncTaskExecutor
import java.nio.file.Paths

@Configuration
@ConditionalOnMissingBean(DicomDataStore::class)
@EnableConfigurationProperties(DicomFileStoreProperties::class)
open class DicomStoreConfiguration {

    @Autowired
    private lateinit var dicomFileStoreProperties: DicomFileStoreProperties

    @Autowired(required = false)
    private var listenerList: MutableList<DicomDataListener> = emptyList<DicomDataListener>().toMutableList()

    @Autowired
    private lateinit var dicomParseConfigurer: DicomParseConfiguer

    @Bean
    @ConditionalOnMissingBean(DicomImageFileSaveHandler::class)
    open fun dicomImageFileSaveHandler(): DicomImageFileSaveHandler {
        return LocalFileSaveHandler()
    }

    @Bean(initMethod = "reload")
    open fun dicomDataFileStore(
            dicomImageFileSaveHandler: DicomImageFileSaveHandler
    ): DicomDataStore {
        Paths.get(dicomFileStoreProperties.dir).toFile().mkdirs()
        val listeners = listenerList.map { AsyncDicomDataListener(dicomParseConfigurer.dataListenerExecutor(), it) as DicomDataListener }
                .toMutableList()
        return DicomDataFileStore(dicomFileStoreProperties.dir, dicomImageFileSaveHandler, listeners)
    }

}