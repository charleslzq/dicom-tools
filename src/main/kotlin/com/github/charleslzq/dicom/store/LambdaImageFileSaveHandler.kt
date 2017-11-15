package com.github.charleslzq.dicom.store

import java.net.URI
import java.nio.file.Path

class LambdaImageFileSaveHandler(
        private val saveHandler: (Path, Map<String, URI>) -> Map<String, URI>
): DicomImageFileSaveHandler {

    override fun save(path: Path, imageMap: Map<String, URI>): Map<String, URI> {
        return saveHandler.invoke(path, imageMap)
    }
}