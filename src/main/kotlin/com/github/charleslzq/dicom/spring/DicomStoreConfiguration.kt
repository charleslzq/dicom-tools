package com.github.charleslzq.dicom.spring

import com.github.charleslzq.dicom.data.DicomDataFactory
import com.github.charleslzq.dicom.data.ImageMeta
import com.github.charleslzq.dicom.data.Meta
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

    @Autowired
    private lateinit var dicomParseConfigurer: DicomParseConfigurer

    @Bean
    @ConditionalOnMissingBean(DicomImageFileSaveHandler::class)
    open fun dicomImageFileSaveHandler(): DicomImageFileSaveHandler {
        return LocalFileSaveHandler()
    }

    @Bean
    open fun <P : Meta, T : Meta, E : Meta, I : ImageMeta> dicomDataFileStore(
            dicomDataFactory: DicomDataFactory<P, T, E, I>,
            dicomImageFileSaveHandler: DicomImageFileSaveHandler,
            @Autowired(required = false) listenerList: MutableList<DicomDataListener<P, T, E, I>> = mutableListOf()
    ): DicomDataStore<P, T, E, I> {
        Paths.get(dicomFileStoreProperties.dir).toFile().mkdirs()
        val listeners = listenerList.map { AsyncDicomDataListener(dicomParseConfigurer.dataListenerExecutor(), it) as DicomDataListener<P, T, E, I> }
                .toMutableList()
        return DicomDataFileStore(dicomFileStoreProperties.dir, dicomDataFactory, dicomImageFileSaveHandler, listeners)
    }

}