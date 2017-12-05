package com.github.charleslzq.dicom.data

import org.joda.time.LocalDateTime
import java.net.URI

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
        var imagePosition: String? = null,
        var imageOrientation: String? = null,
        var rescaleIntercept: Float? = null,
        var rescaleSlope: Float? = null,
        var rescaleType: String? = null,
        var sliceThickness: Float? = null,
        var spacingBetweenSlices: Float? = null,
        var sliceLocation: Float? = null,
        var kvp: Float? = null,
        var xRayTubCurrent: Int? = null,
        val files: MutableMap<String, URI> = emptyMap<String, URI>().toMutableMap(),
        var createTime: LocalDateTime = LocalDateTime.now(),
        var updateTime: LocalDateTime = LocalDateTime.now()
)