package com.github.charleslzq.dicom.store

import java.net.URI
import java.nio.file.Path

interface DicomImageFileSaveHandler {
    fun save(path: Path, imageMap: Map<String, URI>): Map<String, URI>
}