package com.github.charleslzq.dicom.spring

import com.github.charleslzq.dicom.DicomParseWorker
import com.github.charleslzq.dicom.data.DicomDataFactory
import com.github.charleslzq.dicom.data.ImageMeta
import com.github.charleslzq.dicom.data.Meta
import com.github.charleslzq.dicom.reader.DicomDataReader
import com.github.charleslzq.dicom.reader.DicomImageReader
import com.github.charleslzq.dicom.store.DicomDataStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Paths

@Configuration
@EnableConfigurationProperties(DicomImageProperties::class, DicomParseProperties::class)
open class DicomDataReaderConfiguration {

    @Autowired
    private lateinit var dicomImageProperties: DicomImageProperties

    @Autowired
    private lateinit var dicomParseProperties: DicomParseProperties

    @Autowired
    private lateinit var dicomParseConfigurer: DicomParseConfigurer

    @Bean
    open fun <P : Meta, T : Meta, E : Meta, I : ImageMeta> dicomDataReader(
            dicomDataFactory: DicomDataFactory<P, T, E, I>
    ): DicomDataReader<P, T, E, I> {
        val imageConfigList = dicomImageProperties.configs
        if (imageConfigList.find { it.label == "default" } == null && dicomImageProperties.useDefault) {
            imageConfigList.add(DicomImageConfig())
        }
        val imageReaderList = imageConfigList.map {
            DicomImageReader(it.format, it.suffix, it.label, it.clazz, it.compressionType, it.quality)
        }.toList()

        return DicomDataReader(dicomDataFactory, imageReaderList)
    }

    @Bean
    open fun <P : Meta, T : Meta, E : Meta, I : ImageMeta> dicomParseWorker(
            dicomDataReader: DicomDataReader<P, T, E, I>,
            dicomDataStore: DicomDataStore<P, T, E, I>
    ): DicomParseWorker<P, T, E, I> {
        Paths.get(dicomImageProperties.imgTmpDir).toFile().mkdirs()
        return DicomParseWorker(
                dicomDataReader,
                dicomDataStore,
                dicomParseConfigurer.parseExecutor(),
                dicomImageProperties.imgTmpDir,
                dicomParseProperties.formats,
                dicomParseProperties.retry,
                dicomParseProperties.deleteAfterParse
        )
    }
}