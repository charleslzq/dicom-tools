package com.github.charleslzq.dicom

import org.hamcrest.MatcherAssert.assertThat
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
    }

    @Test
    fun testReadDataSuccess() {
        val dicomFile = readFile()
        val dicomDataReader = DicomDataReader(dirBase)
        val dir = File(dirBase)
        assertThat("开始时目录为空", dir.listFiles(), arrayWithSize(0))

        val dicomData = dicomDataReader.parse(dicomFile)

        val tagMap = dicomData.metaData
        assertThat("应至少读取一条tag信息", tagMap.isNotEmpty())

        val uri = dicomData.imageUri
        val imgFile = File(uri)
        assertThat("目录中应有一个文件", dir.listFiles(), arrayWithSize(1))
        assertThat("JPG图片文件应存在", imgFile.exists() && imgFile.isFile && imgFile.name.endsWith(".jpg"))
    }
}