package com.github.charleslzq.dicom.data

import org.joda.time.LocalDateTime
import java.net.URI

data class DicomImageMetaInfo(
        val name: String = "",
        val imageType: String = "",
        val sopUID: String = "",
        val date: String = "",
        val time: String = "",
        val instanceNumber: String = "",
        val samplesPerPixel: Int = -1,
        val photometricInterpretation: String = "",
        val rows: Int = -1,
        val columns: Int = -1,
        val pixelSpacing: String = "",
        val bitsAllocated: Int = -1,
        val bitsStored: Int = -1,
        val highBit: Int = -1,
        val pixelRepresentation: Int = -1,
        val windowCenter: String = "",
        val windowWidth: String = "",
        val imagePosition: String = "",
        val imageOrientation: String = "",
        val rescaleIntercept: Float = 0f,
        val rescaleSlope: Float = 0f,
        val rescaleType: String = "",
        val sliceThickness: Float = 0f,
        val spacingBetweenSlices: Float = 0f,
        val sliceLocation: Float = 0f,
        val kvp: Float = 0f,
        val xRayTubCurrent: Int = -1,
        override val uid: String = instanceNumber,
        override val files: MutableMap<String, URI> = emptyMap<String, URI>().toMutableMap(),
        override val createTime: LocalDateTime = LocalDateTime.now(),
        override val updateTime: LocalDateTime = LocalDateTime.now()
) : ImageMeta