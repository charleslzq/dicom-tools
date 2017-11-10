package com.github.charleslzq.dicom.spring

import com.github.charleslzq.dicom.store.DicomDataFileStore
import com.github.charleslzq.dicom.store.DicomDataStore
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

    @Bean
    open fun dicomDataFileStore(): DicomDataStore {
        Paths.get(dicomFileStoreProperties.dir).toFile().mkdirs()
        return DicomDataFileStore(dicomFileStoreProperties.dir)
    }

}