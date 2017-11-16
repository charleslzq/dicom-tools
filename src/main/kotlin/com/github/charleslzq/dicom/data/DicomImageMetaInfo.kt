package com.github.charleslzq.dicom.data

import java.net.URI
import org.joda.time.LocalDateTime

data class DicomImageMetaInfo(
        var name: String? = null,
        var imageType: String? = null,
        var sopUID: String? = null,
        var date: String? = null,
        var time: String? = null,
        var instanceNumber: String? = null,
        var samplesPerPixel: Int? = null,
        var photometricInterpretation: String? = null,
        var rows: Int? = null,
        var columns: Int? = null,
        var pixelSpacing: String? = null,
        var bitsAllocated: Int? = null,
        var bitsStored: Int? = null,
        var highBit: Int? = null,
        var pixelRepresentation: Int? = null,
        var windowCenter: String? = null,
        var windowWidth: String? = null,
        var rescaleIntercept: Float? = null,
        var rescaleSlope: Float? = null,
        var rescaleType: String? = null,
        val files: MutableMap<String, URI> = emptyMap<String, URI>().toMutableMap(),
        val updateTime: MutableMap<String, LocalDateTime> = emptyMap<String, LocalDateTime>().toMutableMap()
)