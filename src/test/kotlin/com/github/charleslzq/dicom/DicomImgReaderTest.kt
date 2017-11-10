package com.github.charleslzq.dicom

import com.github.charleslzq.dicom.reader.DicomImageReader
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsArrayWithSize.arrayWithSize
import org.junit.Before
import org.junit.Test
import java.io.File

class DicomImgReaderTest {
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
    fun testImgGenerateSuccess() {
        val dicomFile = TestUtil.readFile(path)
        val dicomImageReader = DicomImageReader("PNG", "png")
        val dir = File(dirBase)
        assertThat("开始时目录为空", dir.listFiles(), arrayWithSize(0))

        val uri = dicomImageReader.convert(dicomFile, dirBase)
        val imgFile = File(uri)
        assertThat("目录中应有一个文件", dir.listFiles(), arrayWithSize(1))
        assertThat("png图片文件应存在", imgFile.exists() && imgFile.isFile && imgFile.name.endsWith(".png"))
    }
}