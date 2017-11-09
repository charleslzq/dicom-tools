package com.github.charleslzq.dicom.data

import com.google.common.collect.Lists

data class DicomStudy(
        val metaInfo: DicomStudyMetaInfo,
        val series: MutableList<DicomSeries> = Lists.newArrayList()
)