package com.github.charleslzq.dicom.store

import com.fatboyindustrial.gsonjodatime.Converters
import com.github.charleslzq.dicom.data.*
import com.google.gson.GsonBuilder
import org.joda.time.LocalDateTime
import java.io.File
import java.util.*

class DicomDataFileStore(
        private val basePath: String,
        private val saveHandler: DicomImageFileSaveHandler,
        private val listeners: MutableList<DicomDataListener> = emptyList<DicomDataListener>().toMutableList()
) : DicomDataStore {
    private val metaFileName = "meta.json"
    private val gson = Converters.registerLocalDateTime(GsonBuilder()).create()

    private val baseDir = File(basePath)
    private val patientFieldsToCheck = listOf<FieldChecker<DicomPatientMetaInfo, Any>>(
            FieldChecker(DicomPatientMetaInfo::uid::get),
            FieldChecker(DicomPatientMetaInfo::name::get),
            FieldChecker(DicomPatientMetaInfo::address::get),
            FieldChecker(DicomPatientMetaInfo::sex::get),
            FieldChecker(DicomPatientMetaInfo::birthday::get),
            FieldChecker(DicomPatientMetaInfo::birthTime::get),
            FieldChecker(DicomPatientMetaInfo::weight::get),
            FieldChecker(DicomPatientMetaInfo::idIssuer::get),
            FieldChecker(DicomPatientMetaInfo::position::get),
            FieldChecker(DicomPatientMetaInfo::pregnancyStatus::get)
    )
    private val studyFieldsToCheck = listOf<FieldChecker<DicomStudyMetaInfo, Any>>(
            FieldChecker(DicomStudyMetaInfo::uid::get),
            FieldChecker(DicomStudyMetaInfo::accessionNumber::get),
            FieldChecker(DicomStudyMetaInfo::instanceUID::get),
            FieldChecker(DicomStudyMetaInfo::modalities::get),
            FieldChecker(DicomStudyMetaInfo::bodyPart::get),
            FieldChecker(DicomStudyMetaInfo::patientAge::get),
            FieldChecker(DicomStudyMetaInfo::date::get),
            FieldChecker(DicomStudyMetaInfo::time::get),
            FieldChecker(DicomStudyMetaInfo::description::get)
    )
    private val seriesFieldsToCheck = listOf<FieldChecker<DicomSeriesMetaInfo, Any>>(
            FieldChecker(DicomSeriesMetaInfo::number::get),
            FieldChecker(DicomSeriesMetaInfo::instanceUID::get),
            FieldChecker(DicomSeriesMetaInfo::modality::get),
            FieldChecker(DicomSeriesMetaInfo::date::get),
            FieldChecker(DicomSeriesMetaInfo::time::get),
            FieldChecker(DicomSeriesMetaInfo::description::get),
            FieldChecker(DicomSeriesMetaInfo::imagePosition::get),
            FieldChecker(DicomSeriesMetaInfo::imageOrientation::get),
            FieldChecker(DicomSeriesMetaInfo::sliceThickness::get),
            FieldChecker(DicomSeriesMetaInfo::spacingBetweenSlices::get),
            FieldChecker(DicomSeriesMetaInfo::sliceLocation::get)
    )
    private val imageFieldsToCheck = listOf<FieldChecker<DicomImageMetaInfo, Any>>(
            FieldChecker(DicomImageMetaInfo::name::get),
            FieldChecker(DicomImageMetaInfo::imageType::get),
            FieldChecker(DicomImageMetaInfo::sopUID::get),
            FieldChecker(DicomImageMetaInfo::date::get),
            FieldChecker(DicomImageMetaInfo::time::get),
            FieldChecker(DicomImageMetaInfo::instanceNumber::get),
            FieldChecker(DicomImageMetaInfo::samplesPerPixel::get),
            FieldChecker(DicomImageMetaInfo::photometricInterpretation::get),
            FieldChecker(DicomImageMetaInfo::rows::get),
            FieldChecker(DicomImageMetaInfo::columns::get),
            FieldChecker(DicomImageMetaInfo::pixelSpacing::get),
            FieldChecker(DicomImageMetaInfo::bitsAllocated::get),
            FieldChecker(DicomImageMetaInfo::bitsStored::get),
            FieldChecker(DicomImageMetaInfo::highBit::get),
            FieldChecker(DicomImageMetaInfo::pixelRepresentation::get),
            FieldChecker(DicomImageMetaInfo::windowCenter::get),
            FieldChecker(DicomImageMetaInfo::windowWidth::get),
            FieldChecker(DicomImageMetaInfo::rescaleIntercept::get),
            FieldChecker(DicomImageMetaInfo::rescaleSlope::get),
            FieldChecker(DicomImageMetaInfo::rescaleType::get)
    )

    init {
        baseDir.mkdirs()
    }

    override fun getPatientIdList(): List<String> {
        return listValidSubDirs(basePath)
    }

    override fun getPatient(patientId: String): DicomPatient? {
        val metaInfo = getPatientMeta(patientId)
        if (metaInfo != null) {
            val studyList = this.getStudyIdList(patientId).mapNotNull { getStudy(patientId, it) }.toMutableList()
            return DicomPatient(metaInfo, studyList)
        }
        return null
    }

    override fun getPatientMeta(patientId: String): DicomPatientMetaInfo? {
        return loadMetaFile(DicomPatientMetaInfo::class.java, basePath, patientId)
    }

    override fun getStudyIdList(patientId: String): List<String> {
        return listValidSubDirs(basePath, patientId)
    }

    override fun getStudy(patientId: String, studyId: String): DicomStudy? {
        val metaInfo = getStudyMeta(patientId, studyId)
        if (metaInfo != null) {
            val seriesList = getSeriesIdList(patientId, studyId).mapNotNull { getSeries(patientId, studyId, it) }.toMutableList()
            return DicomStudy(metaInfo, seriesList)
        }
        return null
    }

    override fun getStudyMeta(patientId: String, studyId: String): DicomStudyMetaInfo? {
        return loadMetaFile(DicomStudyMetaInfo::class.java, basePath, patientId, studyId)
    }

    override fun getSeriesIdList(patientId: String, studyId: String): List<String> {
        return listValidSubDirs(basePath, patientId, studyId)
    }

    override fun getSeries(patientId: String, studyId: String, seriesId: String): DicomSeries? {
        val metaInfo = getSeriesMeta(patientId, studyId, seriesId)
        if (metaInfo != null) {
            val imageList = getImageIdList(patientId, studyId, seriesId)
                    .mapNotNull { getImageMeta(patientId, studyId, seriesId, it) }
                    .toMutableList()
            return DicomSeries(metaInfo, imageList)
        }
        return null
    }

    override fun getSeriesMeta(patientId: String, studyId: String, seriesId: String): DicomSeriesMetaInfo? {
        return loadMetaFile(DicomSeriesMetaInfo::class.java, basePath, patientId, studyId, seriesId)
    }

    override fun getImageIdList(patientId: String, studyId: String, seriesId: String): List<String> {
        return listValidSubDirs(basePath, patientId, studyId, seriesId)
    }

    override fun getImageMeta(patientId: String, studyId: String, seriesId: String, imageNum: String): DicomImageMetaInfo? {
        return loadMetaFile(DicomImageMetaInfo::class.java, basePath, patientId, studyId, seriesId, imageNum)
    }

    override fun savePatient(patient: DicomPatient) {
        savePatientMeta(patient.metaInfo)
        val patientId = patient.metaInfo.uid!!
        patient.studies.forEach { saveStudy(patientId, it) }
    }

    override fun savePatientMeta(patientMetaInfo: DicomPatientMetaInfo) {
        val patientId = patientMetaInfo.uid!!
        val oldMeta = getPatientMeta(patientId)
        val now = LocalDateTime.now()
        var save = false
        if (oldMeta == null) {
            patientMetaInfo.createTime = now
            patientMetaInfo.updateTime = now
            save = true
        } else {
            if (patientFieldsToCheck.any { !it.needUpdate(oldMeta, patientMetaInfo) }) {
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

    override fun saveStudy(patientId: String, study: DicomStudy) {
        saveStudyMeta(patientId, study.metaInfo)
        val studyId = study.metaInfo.instanceUID!!
        study.series.forEach { saveSeries(patientId, studyId, it) }
    }

    override fun saveStudyMeta(patientId: String, studyMetaInfo: DicomStudyMetaInfo) {
        val studyId = studyMetaInfo.instanceUID!!
        val oldMeta = getStudyMeta(patientId, studyId)
        val now = LocalDateTime.now()
        var save = false
        if (oldMeta == null) {
            studyMetaInfo.createTime = now
            studyMetaInfo.updateTime = now
            save = true
        } else {
            if (studyFieldsToCheck.any { it.needUpdate(oldMeta, studyMetaInfo) }) {
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

    override fun saveSeries(patientId: String, studyId: String, series: DicomSeries) {
        val seriesId = series.metaInfo.instanceUID!!
        saveSeriesMeta(patientId, studyId, series.metaInfo)
        series.images.forEach { saveImage(patientId, studyId, seriesId, it) }
    }

    override fun saveSeriesMeta(patientId: String, studyId: String, seriesMetaInfo: DicomSeriesMetaInfo) {
        val seriesId = seriesMetaInfo.instanceUID!!
        val oldMeta = getSeriesMeta(patientId, studyId, seriesId)
        val now = LocalDateTime.now()
        var save = false
        if (oldMeta == null) {
            seriesMetaInfo.createTime = now
            seriesMetaInfo.updateTime = now
            save = true
        } else {
            if (seriesFieldsToCheck.any { it.needUpdate(oldMeta, seriesMetaInfo) }) {
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

    override fun saveImage(patientId: String, studyId: String, seriesId: String, dicomImageMetaInfo: DicomImageMetaInfo) {
        val imageNum = dicomImageMetaInfo.instanceNumber!!
        val imageDirPath = Paths.get(basePath, patientId, studyId, seriesId, imageNum)
        val oldMeta = getImageMeta(patientId, studyId, seriesId, imageNum)
        val now = LocalDateTime.now()
        var save = false

        if (oldMeta == null) {
            dicomImageMetaInfo.createTime = now
            dicomImageMetaInfo.updateTime = now
            save = true
        } else {
            if (imageFieldsToCheck.any { it.needUpdate(oldMeta, dicomImageMetaInfo) }) {
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

    override fun saveDicomData(dicomData: DicomData) {
        val patient = DicomPatient(dicomData.patientMetaInfo)
        val study = DicomStudy(dicomData.studyMetaInfo)
        val series = DicomSeries(dicomData.seriesMetaInfo)
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