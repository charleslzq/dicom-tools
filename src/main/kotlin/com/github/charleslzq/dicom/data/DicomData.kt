package com.github.charleslzq.dicom.data

data class DicomData<out P : Meta, out T : Meta, out E : Meta, out I : ImageMeta>(
        val patientMetaInfo: P,
        val studyMetaInfo: T,
        val seriesMetaInfo: E,
        val imageMetaInfo: I
)