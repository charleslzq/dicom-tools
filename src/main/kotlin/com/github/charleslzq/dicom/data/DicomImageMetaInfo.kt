package com.github.charleslzq.dicom.data

import com.google.common.collect.Maps
import java.net.URI

data class DicomImageMetaInfo(
        var name: String? = null,
        var imageType: String? = null,
        var sopUID: String? = null,
        var date: String? = null,
        var time: String? = null,
        var instanceNumber: String? = null,
        var samplesPerPixel: String? = null,
        var photometricInterpretation: String? = null,
        var rows: String? = null,
        var columns: String? = null,
        var pixelSpacing: String? = null,
        var bitsAllocated: String? = null,
        var bitsStored: String? = null,
        var highBits: String? = null,
        var pixelRepresentation: String? = null,
        var windowCenter: String? = null,
        var windowWidth: String? = null,
        var rescaleIntercept: String? = null,
        var rescaleSlope: String? = null,
        var rescaleType: String? = null,
        val files: MutableMap<String, URI> = Maps.newHashMap()
)