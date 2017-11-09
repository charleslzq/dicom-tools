package com.github.charleslzq.dicom.data

data class DicomStudy(
        var id: String? = null,
        var instanceUID: String? = null,
        var date: String? = null,
        var time: String? = null,
        var description: String? = null
)