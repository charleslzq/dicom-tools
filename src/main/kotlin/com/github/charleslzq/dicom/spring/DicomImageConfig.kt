package com.github.charleslzq.dicom.spring

data class DicomImageConfig(
        var format: String = "PNG",
        var suffix: String = "png",
        var label: String = "default",
        var clazz: String? = null,
        var compressionType: String? = null,
        var quality: Number? = null
)