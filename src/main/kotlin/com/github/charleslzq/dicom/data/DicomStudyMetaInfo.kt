package com.github.charleslzq.dicom.data

import com.google.common.collect.Maps
import java.time.LocalDateTime

data class DicomStudyMetaInfo(
        var id: String? = null,
        var accessionNumber: String? = null,
        var instanceUID: String? = null,
        var modalities: String? = null,
        var bodyPart: String? = null,
        var patientAge: Int? = null,
        var date: String? = null,
        var time: String? = null,
        var description: String? = null,
        val updateTime: MutableMap<String, LocalDateTime> = Maps.newHashMap()
)