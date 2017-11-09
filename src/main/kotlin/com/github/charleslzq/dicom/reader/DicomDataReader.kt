package com.github.charleslzq.dicom.reader

import com.github.charleslzq.dicom.data.*
import org.dcm4che3.data.Tag
import org.dcm4che3.io.DicomInputStream
import java.io.File

class DicomDataReader(private val dicomImageReaders: List<DicomImageReader>) {
    private val dicomTagInfoReader = DicomTagInfoReader()

    fun parse(dcmFile: File, imageDir: String): DicomData {
        val dicomInputStream = DicomInputStream(dcmFile)
        val tagMap = dicomTagInfoReader.parse(dicomInputStream).map { it.tag to it }.toMap()
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

    private fun getPatient(tagMap: Map<Int, DicomTagInfo>): DicomPatientMetaInfo {
        val dicomPatient = DicomPatientMetaInfo()
        getFromTagMap(tagMap, Tag.PatientID, dicomPatient::id::set)
        getFromTagMap(tagMap, Tag.PatientName, dicomPatient::name::set)
        getFromTagMap(tagMap, Tag.PatientAddress, dicomPatient::address::set)
        getFromTagMap(tagMap, Tag.PatientBirthDate, dicomPatient::birthday::set)
        getFromTagMap(tagMap, Tag.PatientBirthTime, dicomPatient::birthTime::set)
        getFromTagMap(tagMap, Tag.PatientWeight, { value -> dicomPatient.weight = value.toDouble()})
        getFromTagMap(tagMap, Tag.PatientPosition, dicomPatient::position::set)
        getFromTagMap(tagMap, Tag.PatientSex, dicomPatient::sex::set)
        getFromTagMap(tagMap, Tag.IssuerOfPatientID, dicomPatient::idIssuer::set)
        getFromTagMap(tagMap, Tag.PregnancyStatus, dicomPatient::pregnancyStatus::set)
        return dicomPatient
    }

    private fun getStudy(tagMap: Map<Int, DicomTagInfo>): DicomStudyMetaInfo {
        val dicomStudy = DicomStudyMetaInfo()
        getFromTagMap(tagMap, Tag.StudyID, dicomStudy::id::set)
        getFromTagMap(tagMap, Tag.StudyInstanceUID, dicomStudy::instanceUID::set)
        getFromTagMap(tagMap, Tag.AccessionNumber, dicomStudy::accessionNumber::set)
        getFromTagMap(tagMap, Tag.ModalitiesInStudy, dicomStudy::modalities::set)
        getFromTagMap(tagMap, Tag.BodyPartExamined, dicomStudy::bodyPart::set)
        getFromTagMap(tagMap, Tag.PatientAge, { value -> dicomStudy.patientAge = value.substringBefore("Y").toInt() })
        getFromTagMap(tagMap, Tag.StudyDate, dicomStudy::date::set)
        getFromTagMap(tagMap, Tag.StudyTime, dicomStudy::time::set)
        getFromTagMap(tagMap, Tag.StudyDescription, dicomStudy::description::set)
        return dicomStudy
    }

    private fun getSeries(tagMap: Map<Int, DicomTagInfo>): DicomSeriesMetaInfo {
        val dicomSeries = DicomSeriesMetaInfo();
        getFromTagMap(tagMap, Tag.SeriesNumber, { value -> dicomSeries.number = value.toInt() })
        getFromTagMap(tagMap, Tag.SeriesInstanceUID, dicomSeries::instanceUID::set)
        getFromTagMap(tagMap, Tag.Modality, dicomSeries::modality::set)
        getFromTagMap(tagMap, Tag.ImageOrientation, dicomSeries::imageOrientation::set)
        getFromTagMap(tagMap, Tag.ImagePosition, dicomSeries::imagePosition::set)
        getFromTagMap(tagMap, Tag.SliceThickness, dicomSeries::sliceThickness::set)
        getFromTagMap(tagMap, Tag.SpacingBetweenSlices, dicomSeries::spacingBetweenSlices::set)
        getFromTagMap(tagMap, Tag.SliceLocation, dicomSeries::sliceLocation::set)
        getFromTagMap(tagMap, Tag.MRAcquisitionType, dicomSeries::acquisition::set)
        getFromTagMap(tagMap, Tag.SeriesDate, dicomSeries::date::set)
        getFromTagMap(tagMap, Tag.SeriesTime, dicomSeries::time::set)
        getFromTagMap(tagMap, Tag.SeriesDescription, dicomSeries::description::set)
        return dicomSeries
    }

    private fun getImage(tagMap: Map<Int, DicomTagInfo>): DicomImageMetaInfo {
        val dicomImage = DicomImageMetaInfo()
        getFromTagMap(tagMap, Tag.ImageType, dicomImage::imageType::set)
        getFromTagMap(tagMap, Tag.SOPInstanceUID, dicomImage::sopUID::set)
        getFromTagMap(tagMap, Tag.ContentDate, dicomImage::date::set)
        getFromTagMap(tagMap, Tag.ContentTime, dicomImage::time::set)
        getFromTagMap(tagMap, Tag.InstanceNumber, dicomImage::instanceNumber::set)
        getFromTagMap(tagMap, Tag.SamplesPerPixel, dicomImage::samplesPerPixel::set)
        getFromTagMap(tagMap, Tag.PhotometricInterpretation, dicomImage::photometricInterpretation::set)
        getFromTagMap(tagMap, Tag.Rows, dicomImage::rows::set)
        getFromTagMap(tagMap, Tag.Columns, dicomImage::columns::set)
        getFromTagMap(tagMap, Tag.PixelSpacing, dicomImage::pixelSpacing::set)
        getFromTagMap(tagMap, Tag.BitsAllocated, dicomImage::bitsAllocated::set)
        getFromTagMap(tagMap, Tag.BitsStored, dicomImage::bitsStored::set)
        getFromTagMap(tagMap, Tag.HighBit, dicomImage::highBits::set)
        getFromTagMap(tagMap, Tag.PixelRepresentation, dicomImage::pixelRepresentation::set)
        getFromTagMap(tagMap, Tag.WindowCenter, dicomImage::windowCenter::set)
        getFromTagMap(tagMap, Tag.WindowWidth, dicomImage::windowWidth::set)
        getFromTagMap(tagMap, Tag.RescaleIntercept, dicomImage::rescaleIntercept::set)
        getFromTagMap(tagMap, Tag.RescaleSlope, dicomImage::rescaleSlope::set)
        getFromTagMap(tagMap, Tag.RescaleType, dicomImage::rescaleType::set)
        return dicomImage
    }

    private fun getFromTagMap(tagMap: Map<Int, DicomTagInfo>, tagNo: Int, consumer: (String) -> Unit) {
        val tag = tagMap.get(tagNo)
        if (tag != null) {
            consumer(tag.stringValue)
        }
    }
}