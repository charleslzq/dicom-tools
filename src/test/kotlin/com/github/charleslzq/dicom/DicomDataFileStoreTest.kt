package com.github.charleslzq.dicom

import com.github.charleslzq.dicom.data.DicomDataFactory
import com.github.charleslzq.dicom.reader.DicomDataReader
import com.github.charleslzq.dicom.reader.DicomImageReader
import com.github.charleslzq.dicom.store.DicomDataFileStore
import com.github.charleslzq.dicom.store.LocalFileSaveHandler
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.io.File
import java.nio.file.Paths

class DicomDataFileStoreTest {
    private val path = "classpath:image-000001.dcm"
    private val dirBase = "/tmp/dicomStore"
    private val loadBase = "classpath:dicom"

    @Test
    fun testFilesCreatedSuccess() {
        val dir = File(dirBase)
        if (dir.exists() && dir.isDirectory) {
            dir.deleteRecursively()
        }
        dir.mkdirs()
        val dataStore = DicomDataFileStore(dirBase, DicomDataFactory.Default(), LocalFileSaveHandler())
        val dicomFile = TestUtil.readFile(path)
        val dicomImageReader = DicomImageReader("PNG", "png")
        val dicomDataReader = DicomDataReader(DicomDataFactory.Default(), listOf(dicomImageReader))
        val dicomData = dicomDataReader.parse(dicomFile, dirBase)
        dataStore.saveDicomData(dicomData)

        assertThat("根目录下应有一个文件夹", dir.listFiles { file, name ->
            Paths.get(file.absolutePath, name).toFile().isDirectory
        }.size, `is`(1))
        assertThat("新信息已加载", dataStore.getPatientIdList().isNotEmpty())
    }

    @Test
    fun testDataLoadSuccess() {
        val dataStore = DicomDataFileStore(TestUtil.readFile(loadBase).absolutePath, DicomDataFactory.Default(), LocalFileSaveHandler())
        val patients = dataStore.getPatientIdList()

        assertThat("有一个病人", patients.size, `is`(1))
        val patient = dataStore.getPatient(patients[0])
        assertThat("有一个研究", patient?.studies?.size, `is`(1))
        val study = patient?.studies?.get(0)
        assertThat("有一个系列", study?.series?.size, `is`(1))
        val series = study?.series?.get(0)
        assertThat("有一个图像", series?.images?.size, `is`(1))
        val image = series?.images?.get(0)
        assertThat("有两个文件", image?.files?.size, `is`(2))
    }
}