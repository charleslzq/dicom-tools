package com.github.charleslzq.dicom

data class DicomSeries(
        var number: Int = -1,
        var instanceUID: String = "",
        var date: String = "",
        var time: String = "",
        var description: String = ""
)