package com.github.charleslzq.dicom.store

import com.github.charleslzq.dicom.data.*
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class DicomDataFileStore(
        private val baseDir: String,
        private val saveHandler: DicomImageFileSaveHandler,
        val listeners: MutableList<DicomDataListener> = emptyList<DicomDataListener>().toMutableList()
) : DicomDataStore {
    private val metaFileName = "meta.json"
    private var dicomStore = DicomStore()
    private val gson = Gson()
    private var needLoad = AtomicBoolean(true)

    init {
        File(baseDir).mkdirs()
    }

    override fun getStoreData(): DicomStore {
        synchronized(dicomStore) {
            return dicomStore
        }
    }

    override fun getPatient(patientId: String): DicomPatient? {
        return getPatientFromStore(patientId, dicomStore)
    }

    private fun getPatientFromStore(patientId: String, store: DicomStore): DicomPatient? {
        return store.patients.find { it.metaInfo.id == patientId }
    }

    override fun getStudy(patientId: String, studyId: String): DicomStudy? {
        return getStudyFromStore(patientId, studyId, dicomStore)
    }

    private fun getStudyFromStore(patientId: String, studyId: String, store: DicomStore): DicomStudy? {
        return getPatientFromStore(patientId, store)?.studies?.find { it.metaInfo.instanceUID == studyId }
    }

    override fun getSeries(patientId: String, studyId: String, seriesId: String): DicomSeries? {
        return getSeriesFromStore(patientId, studyId, seriesId, dicomStore)
    }

    private fun getSeriesFromStore(patientId: String, studyId: String, seriesId: String, store: DicomStore): DicomSeries? {
        return getStudyFromStore(patientId, studyId, store)?.series?.find { it.metaInfo.instanceUID == seriesId }
    }

    override fun loadStoreMeta(): DicomStoreMetaInfo? {
        return loadMetaFile(baseDir, DicomStoreMetaInfo::class.java)
    }

    override fun loadPatientMeta(patientId: String): DicomPatientMetaInfo? {
        val patientDir = Paths.get(baseDir, patientId).toFile()
        return loadMetaFile(patientDir.absolutePath, DicomPatientMetaInfo::class.java)
    }

    override fun loadStudyMeta(patientId: String, studyId: String): DicomStudyMetaInfo? {
        val studyDir = Paths.get(baseDir, patientId, studyId).toFile()
        return loadMetaFile(studyDir.absolutePath, DicomStudyMetaInfo::class.java)
    }

    override fun loadSeriesMeta(patientId: String, studyId: String, seriesId: String): DicomSeriesMetaInfo? {
        val seriesDir = Paths.get(baseDir, patientId, studyId, seriesId).toFile()
        return loadMetaFile(seriesDir.absolutePath, DicomSeriesMetaInfo::class.java)
    }

    override fun loadImageMeta(patientId: String, studyId: String, seriesId: String, imageNum: String): DicomImageMetaInfo? {
        val imageDir = Paths.get(baseDir, patientId, studyId, seriesId, imageNum).toFile()
        return loadMetaFile(imageDir.absolutePath, DicomImageMetaInfo::class.java)
    }

    override fun savePatient(patient: DicomPatient) {
        val patientId = patient.metaInfo.id
        val patientPathDir = Paths.get(baseDir, patientId)
        val patientDir = patientPathDir.toFile()
        if (!patientDir.exists()) {
            patientDir.mkdirs()
        }
        val oldMeta = loadPatientMeta(patientId!!)
        val updateTime = LocalDateTime.now()
        patient.studies.forEach { saveStudy(patientId, it) }
        if (oldMeta != null) {
            patient.metaInfo.updateTime.putAll(oldMeta.updateTime)
        }
        patient.metaInfo.updateTime.putAll(patient.studies.map { it.metaInfo.instanceUID!! to updateTime }.toMap())
        updateMeta(patientDir, patient.metaInfo)
        updateStoreMetaTime(patientId, updateTime)

    }

    override fun saveStudy(patientId: String, study: DicomStudy) {
        val studyId = study.metaInfo.instanceUID
        val studyDirPath = Paths.get(baseDir, patientId, studyId)
        val studyDir = studyDirPath.toFile()
        if (!studyDir.exists()) {
            studyDir.mkdirs()
        }
        val oldMeta = loadStudyMeta(patientId, studyId!!)
        val updateTime = LocalDateTime.now()
        study.series.forEach { saveSeries(patientId, studyId, it) }
        study.metaInfo.updateTime.clear()
        if (oldMeta != null) {
            study.metaInfo.updateTime.putAll(oldMeta.updateTime)
        }
        study.metaInfo.updateTime.putAll(study.series.map { it.metaInfo.instanceUID!! to updateTime }.toMap())
        updateMeta(studyDir, study.metaInfo)
        updatePatientMetaTime(patientId, studyId, updateTime)
        updateStoreMetaTime(patientId, updateTime)
    }

    override fun saveSeries(patientId: String, studyId: String, series: DicomSeries) {
        val seriesId = series.metaInfo.instanceUID
        val seriesDirPath = Paths.get(baseDir, patientId, studyId, seriesId)
        val seriesDir = seriesDirPath.toFile()
        if (!seriesDir.exists()) {
            seriesDir.mkdirs()
        }
        val oldMeta = loadSeriesMeta(patientId, studyId, seriesId!!)
        val updateTime = LocalDateTime.now()
        series.images.forEach { saveImage(patientId, studyId, seriesId, it) }
        series.metaInfo.updateTime.clear()
        if (oldMeta != null) {
            series.metaInfo.updateTime.putAll(oldMeta.updateTime)
        }
        series.metaInfo.updateTime.putAll(series.images.map { it.instanceNumber!! to updateTime }.toMap())
        updateMeta(seriesDir, series.metaInfo)
        updateStudyMetaTime(patientId, studyId, seriesId, updateTime)
        updatePatientMetaTime(patientId, studyId, updateTime)
        updateStoreMetaTime(patientId, updateTime)
    }

    override fun saveImage(patientId: String, studyId: String, seriesId: String, dicomImageMetaInfo: DicomImageMetaInfo) {
        val imageNum = dicomImageMetaInfo.instanceNumber
        val imageDirPath = Paths.get(baseDir, patientId, studyId, seriesId, imageNum)
        val imageDir = imageDirPath.toFile()
        if (!imageDir.exists()) {
            imageDir.mkdirs()
        }
        val oldMeta = loadImageMeta(patientId, studyId, seriesId, imageNum!!)
        val updateTime = LocalDateTime.now()
        val newImageMap = saveHandler.save(imageDirPath, dicomImageMetaInfo.files)
        dicomImageMetaInfo.files.clear()
        dicomImageMetaInfo.updateTime.clear()
        if (oldMeta != null) {
            dicomImageMetaInfo.files.putAll(oldMeta.files)
            dicomImageMetaInfo.updateTime.putAll(oldMeta.updateTime)
        }
        dicomImageMetaInfo.files.putAll(newImageMap)
        dicomImageMetaInfo.updateTime.putAll(newImageMap.map { it.key to updateTime }.toMap())
        updateMeta(imageDir, dicomImageMetaInfo)
        updateSeriesMetaTime(patientId, studyId, seriesId, imageNum, updateTime)
        updateStudyMetaTime(patientId, studyId, seriesId, updateTime)
        updatePatientMetaTime(patientId, studyId, updateTime)
        updateStoreMetaTime(patientId, updateTime)
    }

    override fun saveDicomData(dicomData: DicomData) {
        val patient = DicomPatient(dicomData.patientMetaInfo)
        val study = DicomStudy(dicomData.studyMetaInfo)
        val series = DicomSeries(dicomData.seriesMetaInfo)
        series.images.add(dicomData.imageMetaInfo)
        study.series.add(series)
        patient.studies.add(study)
        savePatient(patient)

        needLoad.set(true)
    }

    override fun reload() {
        if (needLoad.get()) {
            val storeMeta = loadStoreMeta()
            if (storeMeta != null) {
                val patientList = storeMeta.updateTime.keys.map { patientId ->
                    val patientMeta = loadPatientMeta(patientId)
                    if (patientMeta != null) {
                        val studyList = patientMeta.updateTime.keys.map { studyId ->
                            val studyMeta = loadStudyMeta(patientId, studyId)
                            if (studyMeta != null) {
                                val seriesList = studyMeta.updateTime.keys.map { seriesId ->
                                    val seriesMeta = loadSeriesMeta(patientId, studyId, seriesId)
                                    if (seriesMeta != null) {
                                        val images = seriesMeta.updateTime.keys.map { imageNum ->
                                            loadImageMeta(patientId, studyId, seriesId, imageNum)
                                        }.filterNotNull().toMutableList()
                                        DicomSeries(seriesMeta, images)
                                    } else {
                                        null
                                    }
                                }.filterNotNull().toMutableList()
                                DicomStudy(studyMeta, seriesList)
                            } else {
                                null
                            }
                        }.filterNotNull().toMutableList()
                        DicomPatient(patientMeta, studyList)
                    } else {
                        null
                    }
                }.filterNotNull().toMutableList()
                val newStore = DicomStore(storeMeta, patientList)
                synchronized(dicomStore) {
                    if (listeners.isNotEmpty()) {
                        detectChanges(newStore)
                    }
                    dicomStore = newStore
                    needLoad.compareAndSet(true, false)
                }
            }
        }
    }

    private fun metaFileExists(dir: File, name: String): Boolean {
        val subDir = Paths.get(dir.absolutePath, name).toFile()
        return subDir.exists() && subDir.isDirectory && subDir.list({ _, nm -> metaFileName == nm }).isNotEmpty()
    }

    private fun <T> loadMetaFile(dirName: String, clazz: Class<T>): T? {
        val metaFile = Paths.get(dirName, metaFileName).toFile()
        if (metaFile.exists() && metaFile.isFile) {
            Scanner(metaFile).useDelimiter("\n").use {
                val content = it.next()
                return gson.fromJson(content, clazz)
            }
        } else {
            return null
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

    private fun updateStoreMetaTime(patientId: String, updateTime: LocalDateTime) {
        val storeMeta = loadStoreMeta()
        if (storeMeta != null) {
            val storeDir = Paths.get(baseDir).toFile()
            storeMeta.updateTime[patientId] = updateTime
            updateMeta(storeDir, storeMeta)
        } else {
            val newMeta = DicomStoreMetaInfo()
            newMeta.updateTime[patientId] = updateTime
            updateMeta(Paths.get(baseDir).toFile(), newMeta)
        }
    }

    private fun updatePatientMetaTime(patientId: String, studyId: String, updateTime: LocalDateTime) {
        val patientMeta = loadPatientMeta(patientId)
        if (patientMeta != null) {
            val patientDir = Paths.get(baseDir, patientId).toFile()
            patientMeta.updateTime[studyId] = updateTime
            updateMeta(patientDir, patientMeta)
        }
    }

    private fun updateStudyMetaTime(patientId: String, studyId: String, seriesId: String, updateTime: LocalDateTime) {
        val studyMeta = loadStudyMeta(patientId, studyId)
        if (studyMeta != null) {
            val studyDir = Paths.get(baseDir, patientId, studyId).toFile()
            studyMeta.updateTime[seriesId] = updateTime
            updateMeta(studyDir, studyMeta)
        }
    }

    private fun updateSeriesMetaTime(patientId: String, studyId: String, seriesId: String, imageNum: String, updateTime: LocalDateTime) {
        val seriesMeta = loadSeriesMeta(patientId, studyId, seriesId)
        if (seriesMeta != null) {
            val seriesDir = Paths.get(baseDir, patientId, studyId, seriesId).toFile()
            seriesMeta.updateTime[imageNum] = updateTime
            updateMeta(seriesDir, seriesMeta)
        }
    }

    private fun detectChanges(store: DicomStore) {
        val patientCompareResult = compare(dicomStore.metaInfo.updateTime, store.metaInfo.updateTime)
        patientCompareResult.first.mapNotNull { getPatientFromStore(it.key, store) }
                .forEach { patient -> listeners.forEach { it.onPatientCreate(patient) } }
        patientCompareResult.third.forEach { id, _ -> listeners.forEach { it.onPatientDelete(id) } }
        patientCompareResult.second.forEach { patientId, _ ->
            val oldPatient = getPatient(patientId)
            val newPatient = getPatientFromStore(patientId, store)
            if (oldPatient != null && newPatient != null) {
                listeners.forEach { it.onPatientUpdate(oldPatient, newPatient) }
                val studyCompareResult = compare(oldPatient.metaInfo.updateTime, newPatient.metaInfo.updateTime)
                studyCompareResult.first.mapNotNull { getStudyFromStore(patientId, it.key, store) }
                        .forEach { study -> listeners.forEach { it.onStudyCreate(patientId, study) } }
                studyCompareResult.third.forEach { studyId, _ -> listeners.forEach { it.onStudyDelete(patientId, studyId) } }
                studyCompareResult.second.forEach { studyId, _ ->
                    val oldStudy = getStudy(patientId, studyId)
                    val newStudy = getStudyFromStore(patientId, studyId, store)
                    if (oldStudy != null && newStudy != null) {
                        listeners.forEach { it.onStudyUpdate(patientId, oldStudy, newStudy) }
                        val seriesCompareResult = compare(oldStudy.metaInfo.updateTime, newStudy.metaInfo.updateTime)
                        seriesCompareResult.first.mapNotNull { getSeriesFromStore(patientId, studyId, it.key, store) }
                                .forEach { series -> listeners.forEach { it.onSeriesCreate(patientId, studyId, series) } }
                        seriesCompareResult.third.forEach { seriesId, _ -> listeners.forEach { it.onSeriesDelete(patientId, studyId, seriesId) } }
                        seriesCompareResult.second.forEach { seriesId, _ ->
                            val oldSeries = getSeries(patientId, studyId, seriesId)
                            val newSeries = getSeriesFromStore(patientId, studyId, seriesId, store)
                            if (oldSeries != null && newSeries != null) {
                                listeners.forEach { it.onSeriesUpdate(patientId, studyId, oldSeries, newSeries) }
                                val imagesCompareResult = compare(oldSeries.metaInfo.updateTime, newSeries.metaInfo.updateTime)
                                imagesCompareResult.first.mapNotNull { (imageNum, _) -> newSeries.images.find { it.instanceNumber == imageNum } }
                                        .forEach { image -> listeners.forEach { it.onImageCreate(patientId, studyId, seriesId, image) } }
                                imagesCompareResult.third.forEach { imageNum, _ -> listeners.forEach { it.onImageDelete(patientId, studyId, seriesId, imageNum) } }
                                imagesCompareResult.second.forEach { imageNum, _ ->
                                    val oldImage = oldSeries.images.find { it.instanceNumber == imageNum }
                                    val newImage = newSeries.images.find { it.instanceNumber == imageNum }
                                    if (oldImage != null && newImage != null) {
                                        listeners.forEach { it.onImageUpdate(patientId, studyId, seriesId, oldImage, newImage) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun <S, T> compare(oldMap: MutableMap<S, T>, newMap: MutableMap<S, T>): Triple<Map<S, T>, Map<S, Pair<T, T>>, Map<S, T>> {
        val entriesToCreate = newMap.minus(oldMap.keys)
        val entriesToDelete = oldMap.minus(newMap.keys)
        val entriesToUpdate = newMap.filter { oldMap.containsKey(it.key) }.map { it.key to Pair(oldMap[it.key]!!, it.value) }.toMap()
        return Triple(entriesToCreate, entriesToUpdate, entriesToDelete)
    }
}