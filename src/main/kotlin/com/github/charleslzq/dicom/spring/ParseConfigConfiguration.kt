package com.github.charleslzq.dicom.spring

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnMissingBean(DicomParseConfiguer::class)
open class ParseConfigConfiguration {

    @Bean
    open fun parseConfigurer(): DicomParseConfiguer {
        return DicomParseConfiguer()
    }
}