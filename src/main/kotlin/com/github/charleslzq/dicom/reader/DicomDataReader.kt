package com.github.charleslzq.dicom.reader

import com.github.charleslzq.dicom.data.*
import org.dcm4che3.data.Tag
import org.dcm4che3.io.DicomInputStream
import java.io.File

class DicomDataReader<out P: Meta, out T: Meta, out E: Meta, out I: ImageMeta>(
        private val dicomDataFactory: DicomDataFactory<P, T, E, I>,
        private val dicomImageReaders: List<DicomImageReader>
) {
    private val dicomTagInfoReader = DicomTagInfoReader()

    fun parse(dcmFile: File, imageDir: String): DicomData<P, T, E, I> {
        DicomInputStream(dcmFile).use {
            val tagMap = dicomTagInfoReader.parse(it).map { it.tag to it }.toMap()
            val dicomData = dicomDataFactory.from(tagMap, dcmFile)
            dicomData.imageMetaInfo.files.put("raw", dcmFile.toURI())
            dicomImageReaders.forEach {
                dicomData.imageMetaInfo.files.put(it.prefix, it.convert(dcmFile, imageDir))
            }
            return dicomData
        }
    }
}