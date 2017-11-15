package com.github.charleslzq.dicom.data

data class DicomSeries(
        val metaInfo: DicomSeriesMetaInfo,
        val images: MutableList<DicomImageMetaInfo> = emptyList<DicomImageMetaInfo>().toMutableList()
)