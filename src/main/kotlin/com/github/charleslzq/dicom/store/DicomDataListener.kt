package com.github.charleslzq.dicom.store

import com.github.charleslzq.dicom.data.ImageMeta
import com.github.charleslzq.dicom.data.Meta

interface DicomDataListener<in P : Meta, in T : Meta, in E : Meta, in I : ImageMeta> {
    fun onPatientMetaSaved(dicomPatientMetaInfo: P) {}
    fun onPatientDelete(patientId: String) {}
    fun onStudyMetaSaved(patientId: String, dicomStudyMetaInfo: T) {}
    fun onStudyDelete(patientId: String, studyId: String) {}
    fun onSeriesMetaSaved(patientId: String, studyId: String, dicomSeriesMetaInfo: E) {}
    fun onSeriesDelete(patientId: String, studyId: String, seriesId: String) {}
    fun onImageSaved(patientId: String, studyId: String, seriesId: String, dicomImageMetaInfo: I) {}
    fun onImageDelete(patientId: String, studyId: String, seriesId: String, imageNum: String) {}
}