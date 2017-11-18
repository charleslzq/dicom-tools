package com.github.charleslzq.dicom.spring

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnMissingBean(DicomParseConfigurer::class)
open class ParseConfigConfiguration {

    @Bean
    open fun parseConfigurer(): DicomParseConfigurer {
        return DicomParseConfigurer()
    }
}