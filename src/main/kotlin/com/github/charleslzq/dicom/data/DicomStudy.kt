package com.github.charleslzq.dicom.data

data class DicomStudy<out T : Meta, E : Meta, I : ImageMeta>(
        val metaInfo: T,
        val series: MutableList<DicomSeries<E, I>> = mutableListOf()
)