package com.github.charleslzq.dicom.data

import com.google.common.collect.Lists

data class DicomPatient(
        val metaInfo: DicomPatientMetaInfo,
        val studies: MutableList<DicomStudy> = Lists.newArrayList()
)