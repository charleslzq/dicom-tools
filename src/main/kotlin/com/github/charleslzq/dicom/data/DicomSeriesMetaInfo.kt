package com.github.charleslzq.dicom.data

import org.joda.time.LocalDateTime

data class DicomSeriesMetaInfo(
        var number: Int? = null,
        var instanceUID: String? = null,
        var modality: String? = null,
        var date: String? = null,
        var time: String? = null,
        var description: String? = null,
        var createTime: LocalDateTime = LocalDateTime.now(),
        var updateTime: LocalDateTime = LocalDateTime.now()
)