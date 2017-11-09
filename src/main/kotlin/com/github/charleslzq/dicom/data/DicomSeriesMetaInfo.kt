package com.github.charleslzq.dicom.data

data class DicomSeriesMetaInfo(
        var number: Int? = null,
        var instanceUID: String? = null,
        var modality: String? = null,
        var date: String? = null,
        var time: String? = null,
        var description: String? = null,
        var imagePosition: String? = null,
        var imageOrientation: String? = null,
        var sliceThickness: String? = null,
        var spacingBetweenSlices: String? = null,
        var sliceLocation: String? = null,
        var acquisition: String? = null
)