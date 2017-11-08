package com.github.charleslzq.dicom

data class DicomStudy(
        var id: String = "",
        var instanceUID: String = "",
        var date: String = "",
        var time: String = "",
        var description: String = ""
)