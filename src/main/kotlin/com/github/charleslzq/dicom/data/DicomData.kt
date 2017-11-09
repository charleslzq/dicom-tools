package com.github.charleslzq.dicom.data

data class DicomData(
        val patientMetaInfo: DicomPatientMetaInfo,
        val studyMetaInfo: DicomStudyMetaInfo,
        val seriesMetaInfo: DicomSeriesMetaInfo,
        val imageMetaInfo: DicomImageMetaInfo
)