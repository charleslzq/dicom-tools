package com.github.charleslzq.dicom.spring

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "dicom.store")
data class DicomFileStoreProperties(
        var dir: String = "/var/dicom/store"
)