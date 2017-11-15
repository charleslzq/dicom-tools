package com.github.charleslzq.dicom.data

data class DicomStore(
        val metaInfo: DicomStoreMetaInfo = DicomStoreMetaInfo(),
        val patients: List<DicomPatient> = emptyList<DicomPatient>()
)