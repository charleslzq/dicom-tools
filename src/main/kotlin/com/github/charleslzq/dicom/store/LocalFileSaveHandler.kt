package com.github.charleslzq.dicom.store

import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class LocalFileSaveHandler : DicomImageFileSaveHandler {

    override fun save(path: Path, imageMap: Map<String, URI>): Map<String, URI> {
        return imageMap.map { it.key to copyFile(it.value, path.toFile().absolutePath) }.toMap()
    }

    private fun copyFile(uri: URI, newDir: String): URI {
        val rawPath = Paths.get(uri)
        val fileName = rawPath.toFile().name
        val filePath = Paths.get(newDir, fileName)
        Files.copy(rawPath, filePath, StandardCopyOption.REPLACE_EXISTING)
        return filePath.toUri()
    }
}