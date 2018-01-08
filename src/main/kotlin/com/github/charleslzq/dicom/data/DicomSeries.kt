package com.github.charleslzq.dicom.data

data class DicomSeries<out E : Meta, I : ImageMeta>(
        val metaInfo: E,
        val images: MutableList<I> = mutableListOf()
)