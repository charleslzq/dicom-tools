package com.github.charleslzq.dicom

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsArrayWithSize.arrayWithSize
import org.junit.Before
import org.junit.Test
import org.springframework.util.ResourceUtils
import java.io.File

class DicomImgReaderTest {
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
    fun testImgGenerateSuccess() {
        val dicomFile = readFile()
        val dicomImageReader = DicomImageReader(dirBase)
        val dir = File(dirBase)
        assertThat("开始时目录为空", dir.listFiles(), arrayWithSize(0))

        val uri = dicomImageReader.convert(dicomFile)
        val imgFile = File(uri)
        assertThat("目录中应有一个文件", dir.listFiles(), arrayWithSize(1))
        assertThat("JPG图片文件应存在", imgFile.exists() && imgFile.isFile && imgFile.name.endsWith(".jpg"))
    }
}