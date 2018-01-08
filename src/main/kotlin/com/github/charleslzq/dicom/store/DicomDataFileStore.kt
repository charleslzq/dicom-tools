package com.github.charleslzq.dicom.store

import com.fatboyindustrial.gsonjodatime.Converters
import com.github.charleslzq.dicom.data.*
import com.google.gson.GsonBuilder
import org.joda.time.LocalDateTime
import java.io.File
import java.util.*

class DicomDataFileStore<P : Meta, T : Meta, E : Meta, I : ImageMeta>(
        private val basePath: String,
        private val dicomDataFactory: DicomDataFactory<P, T, E, I>,
        private val saveHandler: DicomImageFileSaveHandler,
        private val listeners: MutableList<DicomDataListener<P, T, E, I>> = mutableListOf()
) : DicomDataStore<P, T, E, I> {
    private val metaFileName = "meta.json"
    private val gson = Converters.registerLocalDateTime(GsonBuilder()).create()

    private val baseDir = File(basePath)

    init {
        baseDir.mkdirs()
    }

    override fun getPatientIdList(): List<String> {
        return listValidSubDirs(basePath)
    }

    override fun getPatient(patientId: String): DicomPatient<P, T, E, I>? {
        val metaInfo = getPatientMeta(patientId)
        if (metaInfo != null) {
            val studyList = this.getStudyIdList(patientId).mapNotNull { getStudy(patientId, it) }.toMutableList()
            return DicomPatient(metaInfo, studyList)
        }
        return null
    }

    override fun getPatientMeta(patientId: String): P? {
        return loadMetaFile(dicomDataFactory.patientMetaClass.java, basePath, patientId)
    }

    override fun getStudyIdList(patientId: String): List<String> {
        return listValidSubDirs(basePath, patientId)
    }

    override fun getStudy(patientId: String, studyId: String): DicomStudy<T, E, I>? {
        val metaInfo = getStudyMeta(patientId, studyId)
        if (metaInfo != null) {
            val seriesList = getSeriesIdList(patientId, studyId).mapNotNull { getSeries(patientId, studyId, it) }.toMutableList()
            return DicomStudy(metaInfo, seriesList)
        }
        return null
    }

    override fun getStudyMeta(patientId: String, studyId: String): T? {
        return loadMetaFile(dicomDataFactory.studyMetaClass, basePath, patientId, studyId)
    }

    override fun getSeriesIdList(patientId: String, studyId: String): List<String> {
        return listValidSubDirs(basePath, patientId, studyId)
    }

    override fun getSeries(patientId: String, studyId: String, seriesId: String): DicomSeries<E, I>? {
        val metaInfo = getSeriesMeta(patientId, studyId, seriesId)
        if (metaInfo != null) {
            val imageList = getImageIdList(patientId, studyId, seriesId)
                    .mapNotNull { getImageMeta(patientId, studyId, seriesId, it) }
                    .toMutableList()
            return DicomSeries(metaInfo, imageList)
        }
        return null
    }

    override fun getSeriesMeta(patientId: String, studyId: String, seriesId: String): E? {
        return loadMetaFile(dicomDataFactory.seriesMetaClass, basePath, patientId, studyId, seriesId)
    }

    override fun getImageIdList(patientId: String, studyId: String, seriesId: String): List<String> {
        return listValidSubDirs(basePath, patientId, studyId, seriesId)
    }

    override fun getImageMeta(patientId: String, studyId: String, seriesId: String, imageNum: String): I? {
        return loadMetaFile(dicomDataFactory.imageMetaClass, basePath, patientId, studyId, seriesId, imageNum)
    }

    override fun savePatient(patient: DicomPatient<P, T, E, I>) {
        savePatientMeta(patient.metaInfo)
        val patientId = patient.metaInfo.uid
        patient.studies.forEach { saveStudy(patientId, it) }
    }

    override fun savePatientMeta(patientMetaInfo: P) {
        val patientId = patientMetaInfo.uid
        val oldMeta = getPatientMeta(patientId)
        val now = LocalDateTime.now()
        var save = false
        if (oldMeta == null) {
            patientMetaInfo.createTime = now
            patientMetaInfo.updateTime = now
            save = true
        } else {
            if (oldMeta != patientMetaInfo) {
                patientMetaInfo.createTime = oldMeta.createTime
                patientMetaInfo.updateTime = now
                save = true
            }
        }
        if (save) {
            saveMetaFile(patientMetaInfo, basePath, patientId)
            listeners.forEach { it.onPatientMetaSaved(patientMetaInfo) }
        }
    }

    override fun saveStudy(patientId: String, study: DicomStudy<T, E, I>) {
        saveStudyMeta(patientId, study.metaInfo)
        val studyId = study.metaInfo.uid
        study.series.forEach { saveSeries(patientId, studyId, it) }
    }

    override fun saveStudyMeta(patientId: String, studyMetaInfo: T) {
        val studyId = studyMetaInfo.uid!!
        val oldMeta = getStudyMeta(patientId, studyId)
        val now = LocalDateTime.now()
        var save = false
        if (oldMeta == null) {
            studyMetaInfo.createTime = now
            studyMetaInfo.updateTime = now
            save = true
        } else {
            if (oldMeta != studyMetaInfo) {
                studyMetaInfo.createTime = oldMeta.createTime
                studyMetaInfo.updateTime = now
                save = true
            }
        }

        if (save) {
            saveMetaFile(studyMetaInfo, basePath, patientId, studyId)
            listeners.forEach { it.onStudyMetaSaved(patientId, studyMetaInfo) }
        }
    }

    override fun saveSeries(patientId: String, studyId: String, series: DicomSeries<E, I>) {
        val seriesId = series.metaInfo.uid
        saveSeriesMeta(patientId, studyId, series.metaInfo)
        series.images.forEach { saveImage(patientId, studyId, seriesId, it) }
    }

    override fun saveSeriesMeta(patientId: String, studyId: String, seriesMetaInfo: E) {
        val seriesId = seriesMetaInfo.uid
        val oldMeta = getSeriesMeta(patientId, studyId, seriesId)
        val now = LocalDateTime.now()
        var save = false
        if (oldMeta == null) {
            seriesMetaInfo.createTime = now
            seriesMetaInfo.updateTime = now
            save = true
        } else {
            if (oldMeta != seriesMetaInfo) {
                seriesMetaInfo.createTime = oldMeta.createTime
                seriesMetaInfo.updateTime = now
                save = true
            }
        }

        if (save) {
            saveMetaFile(seriesMetaInfo, basePath, patientId, studyId, seriesId)
            listeners.forEach { it.onSeriesMetaSaved(patientId, studyId, seriesMetaInfo) }
        }
    }

    override fun saveImage(patientId: String, studyId: String, seriesId: String, dicomImageMetaInfo: I) {
        val imageNum = dicomImageMetaInfo.uid
        val imageDirPath = Paths.get(basePath, patientId, studyId, seriesId, imageNum)
        val oldMeta = getImageMeta(patientId, studyId, seriesId, imageNum)
        val now = LocalDateTime.now()
        var save = false

        if (oldMeta == null) {
            dicomImageMetaInfo.createTime = now
            dicomImageMetaInfo.updateTime = now
            save = true
        } else {
            if (oldMeta != dicomImageMetaInfo) {
                dicomImageMetaInfo.createTime = oldMeta.createTime
                dicomImageMetaInfo.updateTime = now
                save = true
            }
        }

        if (save) {
            val newImageMap = saveHandler.save(imageDirPath.toFile().absolutePath, dicomImageMetaInfo.files)
            dicomImageMetaInfo.files.clear()
            dicomImageMetaInfo.files.putAll(newImageMap)
            saveMetaFile(dicomImageMetaInfo, basePath, patientId, studyId, seriesId, imageNum)
            listeners.forEach { it.onImageSaved(patientId, studyId, seriesId, dicomImageMetaInfo) }
        }
    }

    override fun saveDicomData(dicomData: DicomData<P, T, E, I>) {
        val patient = DicomPatient<P, T, E, I>(dicomData.patientMetaInfo)
        val study = DicomStudy<T, E, I>(dicomData.studyMetaInfo)
        val series = DicomSeries<E, I>(dicomData.seriesMetaInfo)
        series.images.add(dicomData.imageMetaInfo)
        study.series.add(series)
        patient.studies.add(study)
        savePatient(patient)
    }

    override fun clearPatient(patientId: String) {
        Paths.get(basePath, patientId).toFile().deleteRecursively()
    }

    override fun clearStudy(patientId: String, studyId: String) {
        Paths.get(basePath, patientId, studyId).toFile().deleteRecursively()
    }

    override fun clearSeries(patientId: String, studyId: String, seriesId: String) {
        Paths.get(basePath, patientId, studyId, seriesId).toFile().deleteRecursively()
    }

    override fun clearImage(patientId: String, studyId: String, seriesId: String, imageNum: String) {
        Paths.get(basePath, patientId, studyId, seriesId, imageNum).toFile().deleteRecursively()
    }

    override fun clearData() {
        baseDir.deleteRecursively()
    }

    private fun listValidSubDirs(vararg paths: String): List<String> {
        return Paths.get(*paths).toFile().list(this::metaFileExists).toList()
    }

    private fun metaFileExists(dir: File, name: String): Boolean {
        val subDir = Paths.get(dir.absolutePath, name).toFile()
        return subDir.exists() && subDir.isDirectory && subDir.list({ _, nm -> metaFileName == nm }).isNotEmpty()
    }

    private fun <T> loadMetaFile(clazz: Class<T>, vararg dirNames: String): T? {
        val metaFile = Paths.get(*dirNames, metaFileName).toFile()
        if (metaFile.exists() && metaFile.isFile) {
            Scanner(metaFile).useDelimiter("\n").use {
                val content = it.next()
                return gson.fromJson(content, clazz)
            }
        } else {
            return null
        }
    }

    private fun saveMetaFile(target: Any, vararg path: String) {
        val directory = Paths.get(*path).toFile()
        directory.mkdirs()
        val metaFile = Paths.get(*path, metaFileName).toFile()
        metaFile.writeText(gson.toJson(target))
    }
}