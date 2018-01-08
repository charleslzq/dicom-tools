package com.github.charleslzq.dicom.data

import org.dcm4che3.data.Tag
import java.io.File
import kotlin.reflect.KClass

abstract class DicomDataFactory<P : Meta, T : Meta, E : Meta, I : ImageMeta> {
    abstract val patientMetaClass: KClass<P>
    abstract val studyMetaClass: Class<T>
    abstract val seriesMetaClass: Class<E>
    abstract val imageMetaClass: Class<I>
    abstract fun from(tagMap: Map<Int, DicomTagInfo>, dcmFile: File): DicomData<P, T, E, I>

    inline fun <reified T> getOrDefault(tagMap: Map<Int, DicomTagInfo>, tagNo: Int, default: T): T {
        return tagMap[tagNo]?.let { it.stringValue as? T } ?: default
    }

    class Default : DicomDataFactory<DicomPatientMetaInfo, DicomStudyMetaInfo, DicomSeriesMetaInfo, DicomImageMetaInfo>() {
        override val patientMetaClass: KClass<DicomPatientMetaInfo>
            get() = DicomPatientMetaInfo::class
        override val studyMetaClass: Class<DicomStudyMetaInfo>
            get() = DicomStudyMetaInfo::class.java
        override val seriesMetaClass: Class<DicomSeriesMetaInfo>
            get() = DicomSeriesMetaInfo::class.java
        override val imageMetaClass: Class<DicomImageMetaInfo>
            get() = DicomImageMetaInfo::class.java

        override fun from(tagMap: Map<Int, DicomTagInfo>, dcmFile: File): DicomData<DicomPatientMetaInfo, DicomStudyMetaInfo, DicomSeriesMetaInfo, DicomImageMetaInfo> {
            return DicomData(
                    getPatient(tagMap),
                    getStudy(tagMap),
                    getSeries(tagMap),
                    getImage(tagMap, dcmFile)
            )
        }

        private fun getPatient(tagMap: Map<Int, DicomTagInfo>): DicomPatientMetaInfo {
            return DicomPatientMetaInfo(
                    id = getOrDefault(tagMap, Tag.PatientID, ""),
                    name = getOrDefault(tagMap, Tag.PatientName, ""),
                    address = getOrDefault(tagMap, Tag.PatientAddress, ""),
                    birthday = getOrDefault(tagMap, Tag.PatientBirthDate, ""),
                    birthTime = getOrDefault(tagMap, Tag.PatientBirthTime, ""),
                    weight = getOrDefault(tagMap, Tag.PatientWeight, -1f),
                    position = getOrDefault(tagMap, Tag.PatientPosition, ""),
                    sex = getOrDefault(tagMap, Tag.PatientSex, ""),
                    idIssuer = getOrDefault(tagMap, Tag.IssuerOfPatientID, ""),
                    pregnancyStatus = getOrDefault(tagMap, Tag.PregnancyStatus, -1),
                    institutionAddress = getOrDefault(tagMap, Tag.InstitutionName, ""),
                    institutionName = getOrDefault(tagMap, Tag.InstitutionAddress, "")
            )
        }

        private fun getStudy(tagMap: Map<Int, DicomTagInfo>): DicomStudyMetaInfo {
            return DicomStudyMetaInfo(
                    id = getOrDefault(tagMap, Tag.StudyID, ""),
                    instanceUID = getOrDefault(tagMap, Tag.StudyInstanceUID, ""),
                    accessionNumber = getOrDefault(tagMap, Tag.AccessionNumber, ""),
                    modalities = getOrDefault(tagMap, Tag.ModalitiesInStudy, ""),
                    bodyPart = getOrDefault(tagMap, Tag.BodyPartExamined, ""),
                    patientAge = getOrDefault(tagMap, Tag.PatientAge, "-1Y").substringBefore("Y").toInt(),
                    date = getOrDefault(tagMap, Tag.StudyDate, ""),
                    time = getOrDefault(tagMap, Tag.StudyTime, ""),
                    description = getOrDefault(tagMap, Tag.StudyDescription, "")
            )
        }

        private fun getSeries(tagMap: Map<Int, DicomTagInfo>): DicomSeriesMetaInfo {
            return DicomSeriesMetaInfo(
                    number = getOrDefault(tagMap, Tag.SeriesNumber, -1),
                    instanceUID = getOrDefault(tagMap, Tag.SeriesInstanceUID, ""),
                    modality = getOrDefault(tagMap, Tag.Modality, ""),
                    date = getOrDefault(tagMap, Tag.SeriesDate, ""),
                    time = getOrDefault(tagMap, Tag.SeriesTime, ""),
                    description = getOrDefault(tagMap, Tag.SeriesDescription, "")
            )
        }

        private fun getImage(tagMap: Map<Int, DicomTagInfo>, dcmFile: File): DicomImageMetaInfo {
            return DicomImageMetaInfo(
                    name = dcmFile.absolutePath,
                    imageType = getOrDefault(tagMap, Tag.ImageType, ""),
                    sopUID = getOrDefault(tagMap, Tag.SOPInstanceUID, ""),
                    date = getOrDefault(tagMap, Tag.ContentDate, ""),
                    time = getOrDefault(tagMap, Tag.ContentTime, ""),
                    instanceNumber = getOrDefault(tagMap, Tag.InstanceNumber, ""),
                    samplesPerPixel = getOrDefault(tagMap, Tag.SamplesPerPixel, -1),
                    photometricInterpretation = getOrDefault(tagMap, Tag.PhotometricInterpretation, ""),
                    rows = getOrDefault(tagMap, Tag.Rows, -1),
                    columns = getOrDefault(tagMap, Tag.Columns, -1),
                    pixelSpacing = getOrDefault(tagMap, Tag.PixelSpacing, ""),
                    bitsAllocated = getOrDefault(tagMap, Tag.BitsAllocated, -1),
                    bitsStored = getOrDefault(tagMap, Tag.BitsStored, -1),
                    highBit = getOrDefault(tagMap, Tag.HighBit, -1),
                    pixelRepresentation = getOrDefault(tagMap, Tag.PixelRepresentation, -1),
                    windowCenter = getOrDefault(tagMap, Tag.WindowCenter, ""),
                    windowWidth = getOrDefault(tagMap, Tag.WindowWidth, ""),
                    rescaleIntercept = getOrDefault(tagMap, Tag.RescaleIntercept, 0f),
                    rescaleSlope = getOrDefault(tagMap, Tag.RescaleSlope, 0f),
                    sliceThickness = getOrDefault(tagMap, Tag.SliceThickness, 0f),
                    spacingBetweenSlices = getOrDefault(tagMap, Tag.SpacingBetweenSlices, 0f),
                    sliceLocation = getOrDefault(tagMap, Tag.SliceLocation, 0f),
                    rescaleType = getOrDefault(tagMap, Tag.RescaleType, ""),
                    imageOrientation = getOrDefault(tagMap, Tag.ImageOrientation, ""),
                    imagePosition = getOrDefault(tagMap, Tag.ImagePosition, ""),
                    kvp = getOrDefault(tagMap, Tag.KVP, 0f),
                    xRayTubCurrent = getOrDefault(tagMap, Tag.XRayTubeCurrent, -1)
            )
        }
    }
}