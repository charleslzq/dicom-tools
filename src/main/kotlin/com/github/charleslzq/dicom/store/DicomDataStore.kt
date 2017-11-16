package com.github.charleslzq.dicom.store

import com.github.charleslzq.dicom.data.*

interface DicomDataStore {
    fun getStoreData(): DicomStore
    fun getPatient(patientId: String): DicomPatient?
    fun getStudy(patientId: String, studyId: String): DicomStudy?
    fun getSeries(patientId: String, studyId: String, seriesId: String): DicomSeries?
    fun loadStoreMeta(): DicomStoreMetaInfo?
    fun loadPatientMeta(patientId: String): DicomPatientMetaInfo?
    fun loadStudyMeta(patientId: String, studyId: String): DicomStudyMetaInfo?
    fun loadSeriesMeta(patientId: String, studyId: String, seriesId: String): DicomSeriesMetaInfo?
    fun loadImageMeta(patientId: String, studyId: String, seriesId: String, imageNum: String): DicomImageMetaInfo?
    fun savePatient(patient: DicomPatient)
    fun saveStudy(patientId: String, study: DicomStudy)
    fun saveSeries(patientId: String, studyId: String, series: DicomSeries)
    fun saveImage(patientId: String, studyId: String, seriesId: String, dicomImageMetaInfo: DicomImageMetaInfo)
    fun saveDicomData(dicomData: DicomData)
    fun reload()
    fun clearPatient(patientId: String)
    fun clearStudy(patientId: String, studyId: String)
    fun clearSeries(patientId: String, studyId: String, seriesId: String)
    fun clearImage(patientId: String, studyId: String, seriesId: String, imageNum: String)
    fun clearData()
}