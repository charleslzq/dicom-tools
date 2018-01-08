package com.github.charleslzq.dicom.data

data class DicomPatient<out P : Meta, T : Meta, E : Meta, I : ImageMeta>(
        val metaInfo: P,
        val studies: MutableList<DicomStudy<T, E, I>> = mutableListOf()
)