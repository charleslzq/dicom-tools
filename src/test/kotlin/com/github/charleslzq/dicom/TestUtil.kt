package com.github.charleslzq.dicom

import org.springframework.util.ResourceUtils
import java.io.File

object TestUtil {
    fun readFile(path: String): File {
        return ResourceUtils.getFile(path)
    }
}