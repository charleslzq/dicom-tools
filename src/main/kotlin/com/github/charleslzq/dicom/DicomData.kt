package com.github.charleslzq.dicom

import java.net.URI

data class DicomData(
        val patient: DicomPatient,
        val study: DicomStudy,
        val series: DicomSeries,
        val imageUri: URI
)