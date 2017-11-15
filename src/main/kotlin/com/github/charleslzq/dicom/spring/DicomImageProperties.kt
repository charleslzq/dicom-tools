package com.github.charleslzq.dicom.spring

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "dicom.image")
data class DicomImageProperties(
        var useDefault: Boolean = true,
        var configs: MutableList<DicomImageConfig> = emptyList<DicomImageConfig>().toMutableList(),
        var imgTmpDir: String = "/tmp/dicom/images"
)