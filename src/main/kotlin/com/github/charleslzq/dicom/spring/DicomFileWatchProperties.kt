package com.github.charleslzq.dicom.spring

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "dicom.watch")
data class DicomFileWatchProperties(
        var paths: MutableList<String> = emptyList<String>().toMutableList(),
        var autoStart: Boolean = true,
        var enable: Boolean = true
)