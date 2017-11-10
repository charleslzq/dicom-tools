package com.github.charleslzq.dicom.spring

import com.google.common.collect.Lists
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "dicom.image")
data class DicomImageProperties(
        var configs: MutableList<DicomImageConfig> = Lists.newArrayList(),
        var imgTmpDir: String = "/tmp/dicom/images"
)