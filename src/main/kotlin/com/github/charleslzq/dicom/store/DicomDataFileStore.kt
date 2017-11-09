package com.github.charleslzq.dicom.store

import com.github.charleslzq.dicom.data.*
import com.google.common.collect.Lists
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

class DicomDataFileStore(val baseDir: String) : DicomDataStore {
    private val metaFileName = "meta.json"
    private val patients: MutableList<DicomPatient> = Lists.newArrayList()
    private val gson = Gson()

    override fun listPatient(): List<DicomPatient> {
        return patients.toList()
    }

    override fun getDicomImages(patientId: String, studyId: String?, seriesNo: Int?, imageName: String?, label: String?): List<URI> {
        return patients.filter { (metaInfo, _) -> metaInfo.id == patientId }
                .map(DicomPatient::studies).flatMap { it -> it }
                .filter { (metaInfo, _) -> studyId == null || metaInfo.id == studyId }
                .map(DicomStudy::series).flatMap { it -> it }
                .filter { (metaInfo, _) -> seriesNo == null || metaInfo.number == seriesNo }
                .map(DicomSeries::images).flatMap { it -> it }
                .filter { (name, _) -> imageName == null || name == imageName }
                .map { it -> if (label == null) it.files.values else Lists.newArrayList(it.files[label]) }
                .flatMap { it -> it }.filterNotNull().toList()
    }

    override fun saveDicomData(dicomData: DicomData) {
        val patientId = dicomData.patientMetaInfo.id
        val studyId = dicomData.studyMetaInfo.id
        val seriesNumber = dicomData.seriesMetaInfo.number
        val imageName = dicomData.imageMetaInfo.name
        val patientDir = Paths.get(baseDir, patientId).toFile()
        val studyDir = Paths.get(patientDir.absolutePath, studyId).toFile()
        val seriesDir = Paths.get(studyDir.absolutePath, seriesNumber.toString()).toFile()
        val imageDir = Paths.get(seriesDir.absolutePath, imageName).toFile()
        createMetaIfNecessary(patientDir, dicomData.patientMetaInfo)
        createMetaIfNecessary(studyDir, dicomData.studyMetaInfo)
        createMetaIfNecessary(seriesDir, dicomData.seriesMetaInfo)

        if (!imageDir.exists() || imageDir.isFile) {
            imageDir.mkdir()
        }
        val newImages = dicomData.imageMetaInfo.files.map {
            it.key to copyFile(it.value, imageDir.absolutePath)
        }.toMap()
        dicomData.imageMetaInfo.files.clear()
        dicomData.imageMetaInfo.files.putAll(newImages)
        createMetaIfNecessary(imageDir, dicomData.imageMetaInfo)
    }

    fun copyFile(uri: URI, newDir: String): URI {
        val rawPath = Paths.get(uri)
        val fileName = rawPath.toFile().name
        val filePath = Paths.get(newDir, fileName)
        Files.copy(rawPath, filePath, StandardCopyOption.REPLACE_EXISTING)
        return filePath.toUri()
    }

    fun loadMetaFile() {
        val storeDir = File(baseDir)
        if (storeDir.exists() && storeDir.isDirectory) {
            val patientsFromFile = storeDir.list(this::metaFileExists).map { patientDir ->
                val patientPath = Paths.get(baseDir, patientDir)
                val patientMeta = loadMetaFile(patientPath.toFile().absolutePath, DicomPatientMetaInfo::class.java)
                val studies = patientPath.toFile().list(this::metaFileExists).map { studyDir ->
                    val studyPath = Paths.get(baseDir, patientDir, studyDir)
                    val studyMeta = loadMetaFile(studyPath.toFile().absolutePath, DicomStudyMetaInfo::class.java)
                    val series = studyPath.toFile().list(this::metaFileExists).map { seriesDir ->
                        val seriesPath = Paths.get(baseDir, patientDir, studyDir, seriesDir)
                        val seriesMeta = loadMetaFile(seriesPath.toFile().absolutePath, DicomSeriesMetaInfo::class.java)
                        val images = seriesPath.toFile().list(this::metaFileExists).map { imageDir ->
                            val imagePath = Paths.get(seriesPath.toFile().absolutePath, imageDir)
                            loadMetaFile(imagePath.toFile().absolutePath, DicomImageMetaInfo::class.java)
                        }.toList()
                        val dicomSeries = DicomSeries(seriesMeta)
                        dicomSeries.images.addAll(images)
                        dicomSeries
                    }.toList()
                    val dicomStudy = DicomStudy(studyMeta)
                    dicomStudy.series.addAll(series)
                    dicomStudy
                }.toList()
                val dicomPatient = DicomPatient(patientMeta)
                dicomPatient.studies.addAll(studies)
                dicomPatient
            }.toList()
            patients.addAll(patientsFromFile)
        } else {
            storeDir.mkdirs()
        }
    }

    private fun metaFileExists(dir: File, name: String): Boolean {
        val subDir = Paths.get(dir.absolutePath, name).toFile()
        return subDir.exists() && subDir.isDirectory && subDir.list({ _, nm -> metaFileName == nm }).isNotEmpty()
    }

    private fun <T> loadMetaFile(dirName: String, clazz: Class<T>): T {
        Scanner(Paths.get(dirName, metaFileName).toFile()).useDelimiter("\n").use {
            val content = it.next()
            return gson.fromJson(content, clazz)
        }
    }

    private fun createMetaIfNecessary(dir: File, target: Any) {
        if (!metaFileExists(dir, metaFileName)) {
            if (!dir.exists() || dir.isFile) {
                dir.mkdirs()
            }
            val metaFilePath = Paths.get(dir.absolutePath, metaFileName)
            val content = gson.toJson(target)
            FileWriter(metaFilePath.toFile()).use {
                it.write(content)
            }
        }
    }
}