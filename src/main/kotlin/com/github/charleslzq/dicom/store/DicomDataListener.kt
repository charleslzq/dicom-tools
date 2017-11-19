package com.github.charleslzq.dicom.store

import com.github.charleslzq.dicom.data.DicomImageMetaInfo
import com.github.charleslzq.dicom.data.DicomPatientMetaInfo
import com.github.charleslzq.dicom.data.DicomSeriesMetaInfo
import com.github.charleslzq.dicom.data.DicomStudyMetaInfo

interface DicomDataListener {
    fun onPatientMetaSaved(dicomPatientMetaInfo: DicomPatientMetaInfo) {}
    fun onPatientDelete(patientId: String) {}
    fun onStudyMetaSaved(patientId: String, dicomStudyMetaInfo: DicomStudyMetaInfo) {}
    fun onStudyDelete(patientId: String, studyId: String) {}
    fun onSeriesMetaSaved(patientId: String, studyId: String, dicomSeriesMetaInfo: DicomSeriesMetaInfo) {}
    fun onSeriesDelete(patientId: String, studyId: String, seriesId: String) {}
    fun onImageSaved(patientId: String, studyId: String, seriesId: String, dicomImageMetaInfo: DicomImageMetaInfo) {}
    fun onImageDelete(patientId: String, studyId: String, seriesId: String, imageNum: String) {}
}