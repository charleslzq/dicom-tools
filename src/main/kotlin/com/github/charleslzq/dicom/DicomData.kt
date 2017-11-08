package com.github.charleslzq.dicom

import java.net.URI

data class DicomData(
        val metaData: Map<String, DicomTagInfo>,
        val imageUri: URI
)