package com.github.charleslzq.dicom.data

data class DicomPatientMetaInfo(
        var id: String? = null,
        var name: String? = null,
        var address: String? = null,
        var sex: String? = null,
        var birthday: String? = null,
        var birthTime: String? = null,
        var weight: Double? = null,
        var idIssuer: String? = null,
        var position: String? = null,
        var pregnancyStatus: String? = null
)