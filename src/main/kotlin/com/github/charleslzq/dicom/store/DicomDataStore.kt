package com.github.charleslzq.dicom.store

import com.github.charleslzq.dicom.data.*

interface DicomDataStore<P : Meta, T : Meta, E : Meta, I : ImageMeta> {
    fun getPatientIdList(): List<String>
    fun getPatient(patientId: String): DicomPatient<P, T, E, I>?
    fun getPatientMeta(patientId: String): P?
    fun getStudyIdList(patientId: String): List<String>
    fun getStudy(patientId: String, studyId: String): DicomStudy<T, E, I>?
    fun getStudyMeta(patientId: String, studyId: String): T?
    fun getSeriesIdList(patientId: String, studyId: String): List<String>
    fun getSeries(patientId: String, studyId: String, seriesId: String): DicomSeries<E, I>?
    fun getSeriesMeta(patientId: String, studyId: String, seriesId: String): E?
    fun getImageIdList(patientId: String, studyId: String, seriesId: String): List<String>
    fun getImageMeta(patientId: String, studyId: String, seriesId: String, imageNum: String): I?
    fun savePatient(patient: DicomPatient<P, T, E, I>)
    fun savePatientMeta(patientMetaInfo: P)
    fun saveStudy(patientId: String, study: DicomStudy<T, E, I>)
    fun saveStudyMeta(patientId: String, studyMetaInfo: T)
    fun saveSeries(patientId: String, studyId: String, series: DicomSeries<E, I>)
    fun saveSeriesMeta(patientId: String, studyId: String, seriesMetaInfo: E)
    fun saveImage(patientId: String, studyId: String, seriesId: String, dicomImageMetaInfo: I)
    fun saveDicomData(dicomData: DicomData<P, T, E, I>)
    fun clearPatient(patientId: String)
    fun clearStudy(patientId: String, studyId: String)
    fun clearSeries(patientId: String, studyId: String, seriesId: String)
    fun clearImage(patientId: String, studyId: String, seriesId: String, imageNum: String)
    fun clearData()
}