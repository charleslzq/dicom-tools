package com.github.charleslzq.dicom.data

data class DicomSeriesMetaInfo(
        var number: Int? = null,
        var instanceUID: String? = null,
        var date: String? = null,
        var time: String? = null,
        var description: String? = null
)