package com.github.charleslzq.dicom.data

import java.time.LocalDateTime

data class DicomSeriesMetaInfo(
        var number: Int? = null,
        var instanceUID: String? = null,
        var modality: String? = null,
        var date: String? = null,
        var time: String? = null,
        var description: String? = null,
        var imagePosition: String? = null,
        var imageOrientation: Float? = null,
        var sliceThickness: Float? = null,
        var spacingBetweenSlices: Float? = null,
        var sliceLocation: Float? = null,
        val updateTime: MutableMap<String, LocalDateTime> = emptyMap<String, LocalDateTime>().toMutableMap()
)