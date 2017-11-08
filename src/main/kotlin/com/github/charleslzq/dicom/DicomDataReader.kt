package com.github.charleslzq.dicom

import org.dcm4che3.io.DicomInputStream
import java.io.File

class DicomDataReader(imageFileBase: String) {
    private val dicomTagInfoReader = DicomTagInfoReader()
    private val dicomImageReader = DicomImageReader(imageFileBase)

    fun parse(dcmFile: File): DicomData {
        val imageUri = dicomImageReader.convert(dcmFile)
        val dicomInputStream = DicomInputStream(dcmFile)
        val tagList = dicomTagInfoReader.parse(dicomInputStream)
        return DicomData(
                tagList.map { it.tagName to it }.toMap(),
                imageUri
        )
    }
}