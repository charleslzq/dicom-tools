package com.github.charleslzq.dicom.spring

import com.github.charleslzq.dicom.store.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Paths

@Configuration
@ConditionalOnMissingBean(DicomDataStore::class)
@EnableConfigurationProperties(DicomFileStoreProperties::class)
open class DicomStoreConfiguration {

    @Autowired
    private lateinit var dicomFileStoreProperties: DicomFileStoreProperties

    @Autowired(required = false)
    private var listenerList: MutableList<DicomDataListener> = emptyList<DicomDataListener>().toMutableList()

    @Bean
    @ConditionalOnMissingBean(DicomImageFileSaveHandler::class)
    open fun dicomImageFileSaveHandler(): DicomImageFileSaveHandler {
        return LocalFileSaveHandler()
    }

    @Bean(initMethod = "reload")
    open fun dicomDataFileStore(dicomImageFileSaveHandler: DicomImageFileSaveHandler): DicomDataStore {
        Paths.get(dicomFileStoreProperties.dir).toFile().mkdirs()
        return DicomDataFileStore(dicomFileStoreProperties.dir, dicomImageFileSaveHandler, listenerList)
    }

}