package com.github.charleslzq.dicom.data

import com.google.common.collect.Lists

data class DicomStore(
        val metaInfo: DicomStoreMetaInfo = DicomStoreMetaInfo(),
        val patients: List<DicomPatient> = Lists.newArrayList()
)