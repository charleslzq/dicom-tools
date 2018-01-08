package com.github.charleslzq.dicom.spring

import com.github.charleslzq.dicom.data.DicomDataFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class DefaultDicomDataFactoryConfiguration {

    @Bean
    @ConditionalOnMissingBean(DicomDataFactory::class)
    open fun dicomDataFactory() = DicomDataFactory.Default()
}