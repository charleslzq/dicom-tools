package com.github.charleslzq.dicom

import com.github.charleslzq.dicom.reader.DicomTagInfoReader
import org.dcm4che3.io.DicomInputStream
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsEmptyCollection.empty
import org.junit.Test
import org.springframework.util.ResourceUtils
import java.io.File


class DicomTagInfoReaderTest {
    private val path = "classpath:image-000001.dcm"

    private fun readFile(): File {
        return ResourceUtils.getFile(path)
    }

    @Test
    fun testReadTagSuccess() {
        val dicomInputStream = DicomInputStream(readFile())
        val dicomTagInfoReader = DicomTagInfoReader()
        val tagList = dicomTagInfoReader.parse(dicomInputStream)
        assertThat("应至少读取一条tag信息", tagList, not(empty()))
    }
}