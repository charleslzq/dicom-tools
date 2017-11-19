package com.github.charleslzq.dicom.data

import org.joda.time.LocalDateTime

data class DicomPatientMetaInfo(
        var id: String? = null,
        var name: String? = null,
        var address: String? = null,
        var sex: String? = null,
        var birthday: String? = null,
        var birthTime: String? = null,
        var weight: Float? = null,
        var idIssuer: String? = null,
        var position: String? = null,
        var pregnancyStatus: Int? = null,
        var createTime: LocalDateTime = LocalDateTime.now(),
        var updateTime: LocalDateTime = LocalDateTime.now()
)