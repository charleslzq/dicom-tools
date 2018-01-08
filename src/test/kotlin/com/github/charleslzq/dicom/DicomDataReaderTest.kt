package com.github.charleslzq.dicom

import com.github.charleslzq.dicom.data.DicomDataFactory
import com.github.charleslzq.dicom.reader.DicomDataReader
import com.github.charleslzq.dicom.reader.DicomImageReader
import org.assertj.core.util.Lists
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsArrayWithSize.arrayWithSize
import org.junit.Before
import org.junit.Test
import java.io.File

class DicomDataReaderTest {
    private val path = "classpath:image-000001.dcm"
    private val dirBase = "/tmp/dicom"

    @Before
    fun setup() {
        val dir = File(dirBase)
        if (dir.exists() && dir.isDirectory) {
            dir.deleteRecursively()
        }
        dir.mkdir()
    }

    @Test
    fun testReadDataSuccess() {
        val dicomFile = TestUtil.readFile(path)
        val dicomImageReader = DicomImageReader("PNG", "png")
        val dicomDataReader = DicomDataReader(DicomDataFactory.Default(), Lists.newArrayList(dicomImageReader))
        val dir = File(dirBase)
        assertThat("开始时目录为空", dir.listFiles(), arrayWithSize(0))

        val dicomData = dicomDataReader.parse(dicomFile, dirBase)

        assertThat("病人信息不为空", dicomData.patientMetaInfo, notNullValue())
        assertThat("研究信息不为空", dicomData.studyMetaInfo, notNullValue())
        assertThat("系列信息不为空", dicomData.seriesMetaInfo, notNullValue())
        assertThat("图像信息不为空", dicomData.imageMetaInfo, notNullValue())

        val uri = dicomData.imageMetaInfo.files
        assertThat("目录中应有两个文件", uri.size, `is`(2))
    }
}