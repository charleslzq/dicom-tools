package com.github.charleslzq.dicom.store

import com.github.charleslzq.dicom.data.*

interface DicomDataStore {
    fun getPatientIdList(): List<String>
    fun getPatient(patientId: String): DicomPatient?
    fun getPatientMeta(patientId: String): DicomPatientMetaInfo?
    fun getStudyIdList(patientId: String): List<String>
    fun getStudy(patientId: String, studyId: String): DicomStudy?
    fun getStudyMeta(patientId: String, studyId: String): DicomStudyMetaInfo?
    fun getSeriesIdList(patientId: String, studyId: String): List<String>
    fun getSeries(patientId: String, studyId: String, seriesId: String): DicomSeries?
    fun getSeriesMeta(patientId: String, studyId: String, seriesId: String): DicomSeriesMetaInfo?
    fun getImageIdList(patientId: String, studyId: String, seriesId: String): List<String>
    fun getImageMeta(patientId: String, studyId: String, seriesId: String, imageNum: String): DicomImageMetaInfo?
    fun savePatient(patient: DicomPatient)
    fun savePatientMeta(patientMetaInfo: DicomPatientMetaInfo)
    fun saveStudy(patientId: String, study: DicomStudy)
    fun saveStudyMeta(patientId: String, studyMetaInfo: DicomStudyMetaInfo)
    fun saveSeries(patientId: String, studyId: String, series: DicomSeries)
    fun saveSeriesMeta(patientId: String, studyId: String, seriesMetaInfo: DicomSeriesMetaInfo)
    fun saveImage(patientId: String, studyId: String, seriesId: String, dicomImageMetaInfo: DicomImageMetaInfo)
    fun saveDicomData(dicomData: DicomData)
    fun clearPatient(patientId: String)
    fun clearStudy(patientId: String, studyId: String)
    fun clearSeries(patientId: String, studyId: String, seriesId: String)
    fun clearImage(patientId: String, studyId: String, seriesId: String, imageNum: String)
    fun clearData()
}