package com.github.charleslzq.dicom.store

import java.net.URI

interface DicomImageFileSaveHandler {
    fun save(path: String, imageMap: Map<String, URI>): Map<String, URI>
}