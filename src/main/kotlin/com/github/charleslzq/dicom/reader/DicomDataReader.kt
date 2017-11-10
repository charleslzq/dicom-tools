package com.github.charleslzq.dicom.reader

import com.github.charleslzq.dicom.data.*
import org.dcm4che3.data.Tag
import org.dcm4che3.io.DicomInputStream
import java.io.File

class DicomDataReader(private val dicomImageReaders: List<DicomImageReader>) {
    private val dicomTagInfoReader = DicomTagInfoReader()

    fun parse(dcmFile: File, imageDir: String): DicomData {
        DicomInputStream(dcmFile).use {
            val tagMap = dicomTagInfoReader.parse(it).map { it.tag to it }.toMap()
            val patient = getPatient(tagMap)
            val study = getStudy(tagMap)
            val series = getSeries(tagMap)
            val image = getImage(tagMap)
            image.name = dcmFile.nameWithoutExtension
            image.files.put("raw", dcmFile.toURI())
            dicomImageReaders.forEach {
                val imageUri = it.convert(dcmFile, imageDir)
                image.files.put(it.prefix, imageUri)
            }
            return DicomData(patient, study, series, image)
        }
    }

    private fun getPatient(tagMap: Map<Int, DicomTagInfo>): DicomPatientMetaInfo {
        val dicomPatient = DicomPatientMetaInfo()
        getStringFromTagMap(tagMap, Tag.PatientID, dicomPatient::id::set)
        getStringFromTagMap(tagMap, Tag.PatientName, dicomPatient::name::set)
        getStringFromTagMap(tagMap, Tag.PatientAddress, dicomPatient::address::set)
        getStringFromTagMap(tagMap, Tag.PatientBirthDate, dicomPatient::birthday::set)
        getStringFromTagMap(tagMap, Tag.PatientBirthTime, dicomPatient::birthTime::set)
        getFloatFromTagMap(tagMap, Tag.PatientWeight, dicomPatient::weight::set)
        getStringFromTagMap(tagMap, Tag.PatientPosition, dicomPatient::position::set)
        getStringFromTagMap(tagMap, Tag.PatientSex, dicomPatient::sex::set)
        getStringFromTagMap(tagMap, Tag.IssuerOfPatientID, dicomPatient::idIssuer::set)
        getIntFromTagMap(tagMap, Tag.PregnancyStatus, dicomPatient::pregnancyStatus::set)
        return dicomPatient
    }

    private fun getStudy(tagMap: Map<Int, DicomTagInfo>): DicomStudyMetaInfo {
        val dicomStudy = DicomStudyMetaInfo()
        getStringFromTagMap(tagMap, Tag.StudyID, dicomStudy::id::set)
        getStringFromTagMap(tagMap, Tag.StudyInstanceUID, dicomStudy::instanceUID::set)
        getStringFromTagMap(tagMap, Tag.AccessionNumber, dicomStudy::accessionNumber::set)
        getStringFromTagMap(tagMap, Tag.ModalitiesInStudy, dicomStudy::modalities::set)
        getStringFromTagMap(tagMap, Tag.BodyPartExamined, dicomStudy::bodyPart::set)
        getStringFromTagMap(tagMap, Tag.PatientAge, { value -> dicomStudy.patientAge = value.substringBefore("Y").toInt() })
        getStringFromTagMap(tagMap, Tag.StudyDate, dicomStudy::date::set)
        getStringFromTagMap(tagMap, Tag.StudyTime, dicomStudy::time::set)
        getStringFromTagMap(tagMap, Tag.StudyDescription, dicomStudy::description::set)
        return dicomStudy
    }

    private fun getSeries(tagMap: Map<Int, DicomTagInfo>): DicomSeriesMetaInfo {
        val dicomSeries = DicomSeriesMetaInfo();
        getIntFromTagMap(tagMap, Tag.SeriesNumber, dicomSeries::number::set)
        getStringFromTagMap(tagMap, Tag.SeriesInstanceUID, dicomSeries::instanceUID::set)
        getStringFromTagMap(tagMap, Tag.Modality, dicomSeries::modality::set)
        getFloatFromTagMap(tagMap, Tag.ImageOrientation, dicomSeries::imageOrientation::set)
        getStringFromTagMap(tagMap, Tag.ImagePosition, dicomSeries::imagePosition::set)
        getFloatFromTagMap(tagMap, Tag.SliceThickness, dicomSeries::sliceThickness::set)
        getFloatFromTagMap(tagMap, Tag.SpacingBetweenSlices, dicomSeries::spacingBetweenSlices::set)
        getFloatFromTagMap(tagMap, Tag.SliceLocation, dicomSeries::sliceLocation::set)
        getStringFromTagMap(tagMap, Tag.SeriesDate, dicomSeries::date::set)
        getStringFromTagMap(tagMap, Tag.SeriesTime, dicomSeries::time::set)
        getStringFromTagMap(tagMap, Tag.SeriesDescription, dicomSeries::description::set)
        return dicomSeries
    }

    private fun getImage(tagMap: Map<Int, DicomTagInfo>): DicomImageMetaInfo {
        val dicomImage = DicomImageMetaInfo()
        getStringFromTagMap(tagMap, Tag.ImageType, dicomImage::imageType::set)
        getStringFromTagMap(tagMap, Tag.SOPInstanceUID, dicomImage::sopUID::set)
        getStringFromTagMap(tagMap, Tag.ContentDate, dicomImage::date::set)
        getStringFromTagMap(tagMap, Tag.ContentTime, dicomImage::time::set)
        getStringFromTagMap(tagMap, Tag.InstanceNumber, dicomImage::instanceNumber::set)
        getIntFromTagMap(tagMap, Tag.SamplesPerPixel, dicomImage::samplesPerPixel::set)
        getStringFromTagMap(tagMap, Tag.PhotometricInterpretation, dicomImage::photometricInterpretation::set)
        getIntFromTagMap(tagMap, Tag.Rows, dicomImage::rows::set)
        getIntFromTagMap(tagMap, Tag.Columns, dicomImage::columns::set)
        getStringFromTagMap(tagMap, Tag.PixelSpacing, dicomImage::pixelSpacing::set)
        getIntFromTagMap(tagMap, Tag.BitsAllocated, dicomImage::bitsAllocated::set)
        getIntFromTagMap(tagMap, Tag.BitsStored, dicomImage::bitsStored::set)
        getIntFromTagMap(tagMap, Tag.HighBit, dicomImage::highBit::set)
        getIntFromTagMap(tagMap, Tag.PixelRepresentation, dicomImage::pixelRepresentation::set)
        getStringFromTagMap(tagMap, Tag.WindowCenter, dicomImage::windowCenter::set)
        getStringFromTagMap(tagMap, Tag.WindowWidth, dicomImage::windowWidth::set)
        getFloatFromTagMap(tagMap, Tag.RescaleIntercept, dicomImage::rescaleIntercept::set)
        getFloatFromTagMap(tagMap, Tag.RescaleSlope, dicomImage::rescaleSlope::set)
        getStringFromTagMap(tagMap, Tag.RescaleType, dicomImage::rescaleType::set)
        return dicomImage
    }

    private fun getStringFromTagMap(tagMap: Map<Int, DicomTagInfo>, tagNo: Int, consumer: (String) -> Unit) {
        val tag = tagMap.get(tagNo)
        if (tag != null) {
            consumer(tag.stringValue)
        }
    }

    private fun getIntFromTagMap(tagMap: Map<Int, DicomTagInfo>, tagNo: Int, consumer: (Int) -> Unit) {
        val tag = tagMap.get(tagNo)
        if (tag != null) {
            consumer(tag.stringValue.toInt())
        }
    }

    private fun getFloatFromTagMap(tagMap: Map<Int, DicomTagInfo>, tagNo: Int, consumer: (Float) -> Unit) {
        val tag = tagMap.get(tagNo)
        if (tag != null) {
            consumer(tag.stringValue.toFloat())
        }
    }
}