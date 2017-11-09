package com.github.charleslzq.dicom.store

import com.github.charleslzq.dicom.data.DicomData
import com.github.charleslzq.dicom.data.DicomPatient
import java.net.URI

interface DicomDataStore {
    fun listPatient(): List<DicomPatient>
    fun getDicomImages(patientId: String, studyId: String?, seriesNo: Int?, imageName: String?, label: String?): List<URI>
    fun saveDicomData(dicomData: DicomData)
    fun loadMetaFile()
}