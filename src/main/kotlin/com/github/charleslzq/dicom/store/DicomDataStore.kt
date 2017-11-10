package com.github.charleslzq.dicom.store

import com.github.charleslzq.dicom.data.DicomData
import com.github.charleslzq.dicom.data.DicomPatient

interface DicomDataStore {
    fun listPatient(): List<DicomPatient>
    fun findPatient(patientId: String): DicomPatient?
    fun saveDicomData(dicomData: DicomData)
    fun loadMetaFile()
}