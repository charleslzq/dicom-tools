package com.github.charleslzq.dicom.data

data class DicomPatient(
        val metaInfo: DicomPatientMetaInfo,
        val studies: MutableList<DicomStudy> = emptyList<DicomStudy>().toMutableList()
)