package com.github.charleslzq.dicom.spring

import com.google.common.collect.Lists
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "dicom.watch")
data class DicomFileWatchProperties(
        var paths: List<String> = Lists.newArrayList(),
        var autoStart: Boolean = true,
        var enable: Boolean = true
)