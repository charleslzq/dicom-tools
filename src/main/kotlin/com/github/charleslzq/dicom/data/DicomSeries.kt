package com.github.charleslzq.dicom.data

import com.google.common.collect.Lists

data class DicomSeries(
        val metaInfo: DicomSeriesMetaInfo,
        val images: MutableList<DicomImageMetaInfo> = Lists.newArrayList()
)