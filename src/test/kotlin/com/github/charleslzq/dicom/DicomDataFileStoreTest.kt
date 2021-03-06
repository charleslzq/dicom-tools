package com.github.charleslzq.dicom

import com.github.charleslzq.dicom.data.DicomImageMetaInfo
import com.github.charleslzq.dicom.data.DicomPatient
import com.github.charleslzq.dicom.data.DicomSeries
import com.github.charleslzq.dicom.data.DicomStudy
import com.github.charleslzq.dicom.reader.DicomDataReader
import com.github.charleslzq.dicom.reader.DicomImageReader
import com.github.charleslzq.dicom.store.DicomDataFileStore
import com.github.charleslzq.dicom.store.DicomDataListener
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
        dir.mkdir()
        val dataStore = DicomDataFileStore(dirBase, LocalFileSaveHandler(), listOf(SimpleStoreListener()).toMutableList())
        val dicomFile = TestUtil.readFile(path)
        val dicomImageReader = DicomImageReader("PNG", "png")
        val dicomDataReader = DicomDataReader(listOf(dicomImageReader))
        val dicomData = dicomDataReader.parse(dicomFile, dirBase)
        dataStore.saveDicomData(dicomData)
        dataStore.reload()

        assertThat("根目录下应有一个文件夹", dir.listFiles { file, name ->
            Paths.get(file.absolutePath, name).toFile().isDirectory
        }.size, `is`(1))
        assertThat("新信息已加载", dataStore.getStoreData().patients.isNotEmpty())
    }

    @Test
    fun testDataLoadSuccess() {
        val dataStore = DicomDataFileStore(TestUtil.readFile(loadBase).absolutePath, LocalFileSaveHandler())
        dataStore.reload()
        val patients = dataStore.getStoreData().patients

        assertThat("有一个病人", patients.size, `is`(1))
        assertThat("有一个研究", patients[0].studies.size, `is`(1))
        assertThat("有一个系列", patients[0].studies[0].series.size, `is`(1))
        assertThat("有一个图像", patients[0].studies[0].series[0].images.size, `is`(1))
        assertThat("有两个文件", patients[0].studies[0].series[0].images[0].files.size, `is`(2))
    }

    class SimpleStoreListener : DicomDataListener {
        override fun onPatientCreate(dicomPatient: DicomPatient) {
            println(dicomPatient.toString())
        }

        override fun onStudyCreate(patientId: String, dicomStudy: DicomStudy) {
            println(dicomStudy.toString())
        }

        override fun onSeriesCreate(patientId: String, studyId: String, series: DicomSeries) {
            println(series.toString())
        }

        override fun onImageCreate(patientId: String, studyId: String, seriesId: String, dicomImageMetaInfo: DicomImageMetaInfo) {
            println(dicomImageMetaInfo.toString())
        }
    }
}