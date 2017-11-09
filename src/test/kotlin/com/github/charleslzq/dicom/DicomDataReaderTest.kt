package com.github.charleslzq.dicom

import com.github.charleslzq.dicom.reader.DicomDataReader
import com.github.charleslzq.dicom.reader.DicomImageReader
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.collection.IsArrayWithSize.arrayWithSize
import org.junit.Before
import org.junit.Test
import org.springframework.util.ResourceUtils
import java.io.File

class DicomDataReaderTest {
    private val path = "classpath:image-000001.dcm"
    private val dirBase = "/tmp/dicom"

    private fun readFile(): File {
        return ResourceUtils.getFile(path)
    }

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
        val dicomFile = readFile()
        val dicomImageReader = DicomImageReader("PNG", "png")
        val dicomDataReader = DicomDataReader(dicomImageReader)
        val dir = File(dirBase)
        assertThat("开始时目录为空", dir.listFiles(), arrayWithSize(0))

        val dicomData = dicomDataReader.parse(dicomFile, dirBase)

        assertThat("病人信息不为空", dicomData.patient, notNullValue())
        assertThat("研究信息不为空", dicomData.study, notNullValue())
        assertThat("系列信息不为空", dicomData.series, notNullValue())

        val uri = dicomData.imageUri
        val imgFile = File(uri)
        assertThat("目录中应有一个文件", dir.listFiles(), arrayWithSize(1))
        assertThat("png图片文件应存在", imgFile.exists() && imgFile.isFile && imgFile.name.endsWith(".png"))
    }
}