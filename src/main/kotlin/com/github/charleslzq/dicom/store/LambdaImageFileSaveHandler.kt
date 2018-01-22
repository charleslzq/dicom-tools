package com.github.charleslzq.dicom.store

import java.net.URI

class LambdaImageFileSaveHandler(
        private val saveHandler: (String, Map<String, URI>) -> Map<String, URI>
) : DicomImageFileSaveHandler {

    override fun save(path: String, imageMap: Map<String, URI>) = saveHandler.invoke(path, imageMap)
}