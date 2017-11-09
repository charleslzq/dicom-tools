package com.github.charleslzq.dicom.reader

import org.dcm4che3.tool.dcm2jpg.Dcm2Jpg
import java.io.File
import java.net.URI
import java.nio.file.Paths

class DicomImageReader(
        formatName: String,
        private val suffix: String,
        val prefix: String = "default",
        clazz: String? = null,
        compressionType: String? = null,
        quality: Number? = null) {
    private val dcm2jpg = Dcm2Jpg()

    init {
        dcm2jpg.initImageWriter(formatName, suffix, clazz, compressionType, quality)
    }

    fun convert(dicomFile: File, imageDir: String): URI {
        val fileName = prefix + "-" + dicomFile.nameWithoutExtension + (if (suffix.startsWith(".")) suffix else "." + suffix)
        val filePath = Paths.get(imageDir, fileName)
        val destFile = filePath.toFile()
        dcm2jpg.convert(dicomFile, destFile)
        return destFile.toURI()
    }
}