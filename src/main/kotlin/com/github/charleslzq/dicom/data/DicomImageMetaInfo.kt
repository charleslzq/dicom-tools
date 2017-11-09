package com.github.charleslzq.dicom.data

import com.google.common.collect.Maps
import java.net.URI

data class DicomImageMetaInfo(
        var name: String? = null,
        val files: MutableMap<String, URI> = Maps.newHashMap()
)