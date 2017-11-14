package com.github.charleslzq.dicom.spring

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "dicom.parse")
data class DicomParseProperties(
        var retry: Int = 3,
        var formats: List<String> = listOf("dcm")
)