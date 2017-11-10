package com.github.charleslzq.dicom.store

import com.github.charleslzq.dicom.data.*
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.gson.Gson
import org.springframework.beans.factory.InitializingBean
import java.io.File
import java.io.FileWriter
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class DicomDataFileStore(val baseDir: String) : DicomDataStore, InitializingBean {
    private val metaFileName = "meta.json"
    private val patients: MutableList<DicomPatient> = Lists.newArrayList()
    private val gson = Gson()
    private var needLoad = AtomicBoolean(true)

    override fun listPatient(): List<DicomPatient> {
        return patients.toList()
    }

    override fun findPatient(patientId: String): DicomPatient? {
        return patients.find { (metaInfo, _) -> metaInfo.id == patientId }
    }

    override fun saveDicomData(dicomData: DicomData) {
        val patientId = dicomData.patientMetaInfo.id
        val studyUID = dicomData.studyMetaInfo.instanceUID
        val seriesUID = dicomData.seriesMetaInfo.instanceUID
        val imageNum = dicomData.imageMetaInfo.instanceNumber
        val patientDir = Paths.get(baseDir, patientId).toFile()
        val studyDir = Paths.get(patientDir.absolutePath, studyUID).toFile()
        val seriesDir = Paths.get(studyDir.absolutePath, seriesUID).toFile()
        val imageDir = Paths.get(seriesDir.absolutePath, imageNum).toFile()
        updateMeta(patientDir, dicomData.patientMetaInfo)
        updateMeta(studyDir, dicomData.studyMetaInfo)
        updateMeta(seriesDir, dicomData.seriesMetaInfo)

        val newImages: MutableMap<String, URI> = Maps.newHashMap()
        if (!imageDir.exists() || imageDir.isFile) {
            imageDir.mkdir()
        } else {
            val oldImageMeta = loadMetaFile(imageDir.absolutePath, DicomImageMetaInfo::class.java)
            newImages.putAll(oldImageMeta.files)
        }
        newImages.putAll(dicomData.imageMetaInfo.files.map {
            it.key to copyFile(it.value, imageDir.absolutePath)
        }.toMap())
        dicomData.imageMetaInfo.files.clear()
        dicomData.imageMetaInfo.files.putAll(newImages)
        updateMeta(imageDir, dicomData.imageMetaInfo)
        needLoad.set(true)
    }

    private fun copyFile(uri: URI, newDir: String): URI {
        val rawPath = Paths.get(uri)
        val fileName = rawPath.toFile().name
        val filePath = Paths.get(newDir, fileName)
        Files.copy(rawPath, filePath, StandardCopyOption.REPLACE_EXISTING)
        return filePath.toUri()
    }

    override fun loadMetaFile() {
        if (needLoad.get()) {
            synchronized(patients) {
                patients.clear()
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
            needLoad.compareAndSet(true, false)
        }
    }

    override fun afterPropertiesSet() {
        loadMetaFile()
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

    private fun updateMeta(dir: File, target: Any) {
        if (metaFileExists(dir, metaFileName)) {
            Paths.get(dir.absolutePath, metaFileName).toFile().delete()
        } else if (!dir.exists() || dir.isFile) {
            dir.mkdirs()
        }

        val metaFilePath = Paths.get(dir.absolutePath, metaFileName)
        val content = gson.toJson(target)
        FileWriter(metaFilePath.toFile()).use {
            it.write(content)
        }

    }
}