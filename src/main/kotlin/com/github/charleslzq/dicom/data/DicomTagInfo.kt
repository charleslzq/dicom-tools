package com.github.charleslzq.dicom.data

import org.dcm4che3.data.VR

data class DicomTagInfo(
        val vr: VR,
        val tag: Int,
        val tagId: String,
        val tagName: String,
        val stringValue: String
)