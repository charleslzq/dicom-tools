package com.github.charleslzq.dicom.spring

import com.github.charleslzq.dicom.DicomParseWorker
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
    open fun dicomDataReader(): DicomDataReader {
        val imageConfigList = dicomImageProperties.configs
        if (imageConfigList.find { it.label == "default" } == null && dicomImageProperties.useDefault) {
            imageConfigList.add(DicomImageConfig())
        }
        val imageReaderList = imageConfigList.map {
            DicomImageReader(it.format, it.suffix, it.label, it.clazz, it.compressionType, it.quality)
        }.toList()

        return DicomDataReader(imageReaderList)
    }

    @Bean
    open fun dicomParseWorker(
            dicomDataReader: DicomDataReader,
            dicomDataStore: DicomDataStore
    ): DicomParseWorker {
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