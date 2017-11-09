package com.github.charleslzq.dicom.reader

import com.github.charleslzq.dicom.data.*
import org.dcm4che3.data.Tag
import org.dcm4che3.io.DicomInputStream
import java.io.File

class DicomDataReader(private val dicomImageReader: DicomImageReader) {
    private val dicomTagInfoReader = DicomTagInfoReader()

    fun parse(dcmFile: File, imageDir: String): DicomData {
        val imageUri = dicomImageReader.convert(dcmFile, imageDir)
        val dicomInputStream = DicomInputStream(dcmFile)
        val tagMap = dicomTagInfoReader.parse(dicomInputStream).map { it.tag to it }.toMap()
        val patient = getPatient(tagMap)
        val study = getStudy(tagMap)
        val series = getSeries(tagMap)
        return DicomData(patient, study, series, imageUri)
    }

    private fun getPatient(tagMap: Map<Int, DicomTagInfo>): DicomPatient {
        val dicomPatient = DicomPatient()
        getFromTagMap(tagMap, Tag.PatientID, dicomPatient::id::set)
        getFromTagMap(tagMap, Tag.PatientName, dicomPatient::name::set)
        getFromTagMap(tagMap, Tag.PatientAddress, dicomPatient::address::set)
        getFromTagMap(tagMap, Tag.PatientBirthDate, dicomPatient::birthday::set)
        getFromTagMap(tagMap, Tag.PatientPosition, dicomPatient::position::set)
        getFromTagMap(tagMap, Tag.PatientSex, dicomPatient::sex::set)
        getFromTagMap(tagMap, Tag.IssuerOfPatientID, dicomPatient::idIssuer::set)
        getFromTagMap(tagMap, Tag.PatientAge, { value -> dicomPatient.age = value.substringBefore("Y").toInt() })
        return dicomPatient
    }

    private fun getStudy(tagMap: Map<Int, DicomTagInfo>): DicomStudy {
        val dicomStudy = DicomStudy()
        getFromTagMap(tagMap, Tag.StudyID, dicomStudy::id::set)
        getFromTagMap(tagMap, Tag.StudyInstanceUID, dicomStudy::instanceUID::set)
        getFromTagMap(tagMap, Tag.StudyDate, dicomStudy::date::set)
        getFromTagMap(tagMap, Tag.StudyTime, dicomStudy::time::set)
        getFromTagMap(tagMap, Tag.StudyDescription, dicomStudy::description::set)
        return dicomStudy
    }

    private fun getSeries(tagMap: Map<Int, DicomTagInfo>): DicomSeries {
        val dicomSeries = DicomSeries();
        getFromTagMap(tagMap, Tag.SeriesNumber, { value -> dicomSeries.number = value.toInt() })
        getFromTagMap(tagMap, Tag.SeriesInstanceUID, dicomSeries::instanceUID::set)
        getFromTagMap(tagMap, Tag.SeriesDate, dicomSeries::date::set)
        getFromTagMap(tagMap, Tag.SeriesTime, dicomSeries::time::set)
        getFromTagMap(tagMap, Tag.SeriesDescription, dicomSeries::description::set)
        return dicomSeries
    }


    private fun getFromTagMap(tagMap: Map<Int, DicomTagInfo>, tagNo: Int, consumer: (String) -> Unit) {
        val tag = tagMap.get(tagNo)
        if (tag != null) {
            consumer(tag.stringValue)
        }
    }
}