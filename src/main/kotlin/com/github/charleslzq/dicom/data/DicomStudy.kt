package com.github.charleslzq.dicom.data

data class DicomStudy(
        val metaInfo: DicomStudyMetaInfo,
        val series: MutableList<DicomSeries> = emptyList<DicomSeries>().toMutableList()
)