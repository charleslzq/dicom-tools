package com.github.charleslzq.dicom.store

import com.github.charleslzq.dicom.data.DicomImageMetaInfo
import com.github.charleslzq.dicom.data.DicomPatient
import com.github.charleslzq.dicom.data.DicomSeries
import com.github.charleslzq.dicom.data.DicomStudy

interface DicomDataListener {
    fun onPatientCreate(dicomPatient: DicomPatient) {}
    fun onPatientUpdate(oldPatient: DicomPatient, newPatient: DicomPatient) {}
    fun onPatientDelete(patientId: String) {}
    fun onStudyCreate(patientId: String, dicomStudy: DicomStudy) {}
    fun onStudyUpdate(patientId: String, oldStudy: DicomStudy, newStudy: DicomStudy) {}
    fun onStudyDelete(patientId: String, studyId: String) {}
    fun onSeriesCreate(patientId: String, studyId: String, series: DicomSeries) {}
    fun onSeriesUpdate(patientId: String, studyId: String, oldSeries: DicomSeries, newSeries: DicomSeries) {}
    fun onSeriesDelete(patientId: String, studyId: String, seriesId: String) {}
    fun onImageCreate(patientId: String, studyId: String, seriesId: String, dicomImageMetaInfo: DicomImageMetaInfo) {}
    fun onImageUpdate(patientId: String, studyId: String, seriesId: String, oldImageMetaInfo: DicomImageMetaInfo, newImageMetaInfo: DicomImageMetaInfo) {}
    fun onImageDelete(patientId: String, studyId: String, seriesId: String, imageNum: String) {}
}