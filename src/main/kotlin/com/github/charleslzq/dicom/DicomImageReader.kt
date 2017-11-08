package com.github.charleslzq.dicom

import org.dcm4che3.tool.dcm2jpg.Dcm2Jpg
import java.io.File
import java.net.URI
import java.nio.file.Paths
import java.util.*

class DicomImageReader(private val dirBase: String) {
    private val dcm2jpg = Dcm2Jpg()

    init {
        dcm2jpg.initImageWriter("PNG", "png", null, null, null)
        val dir = File(dirBase)
        if (!dir.exists() || dir.isFile) {
            dir.mkdirs()
        }
    }

    fun convert(dicomFile: File): URI {
        val fileName = dicomFile.name + ".png"
        val filePath = Paths.get(dirBase, fileName)
        val destFile = filePath.toFile()
        dcm2jpg.convert(dicomFile, destFile)
        return destFile.toURI()
    }
}