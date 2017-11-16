package com.github.charleslzq.dicom.spring

import com.github.charleslzq.dicom.data.DicomImageMetaInfo
import com.github.charleslzq.dicom.data.DicomPatient
import com.github.charleslzq.dicom.data.DicomSeries
import com.github.charleslzq.dicom.data.DicomStudy
import com.github.charleslzq.dicom.store.DicomDataListener
import org.springframework.core.task.AsyncTaskExecutor

class AsyncDicomDataListener(
        private val asyncTaskExecutor: AsyncTaskExecutor,
        private val listener: DicomDataListener
): DicomDataListener {

    override fun onPatientCreate(dicomPatient: DicomPatient) {
        asyncTaskExecutor.submit {
            listener.onPatientCreate(dicomPatient)
        }
    }

    override fun onPatientUpdate(oldPatient: DicomPatient, newPatient: DicomPatient) {
        asyncTaskExecutor.submit {
            listener.onPatientUpdate(oldPatient, newPatient)
        }
    }

    override fun onPatientDelete(patientId: String) {
        asyncTaskExecutor.submit {
            listener.onPatientDelete(patientId)
        }
    }

    override fun onStudyCreate(patientId: String, dicomStudy: DicomStudy) {
        asyncTaskExecutor.submit {
            listener.onStudyCreate(patientId, dicomStudy)
        }
    }

    override fun onStudyUpdate(patientId: String, oldStudy: DicomStudy, newStudy: DicomStudy) {
        asyncTaskExecutor.submit {
            listener.onStudyUpdate(patientId, oldStudy, newStudy)
        }
    }

    override fun onStudyDelete(patientId: String, studyId: String) {
        asyncTaskExecutor.submit {
            listener.onStudyDelete(patientId, studyId)
        }
    }

    override fun onSeriesCreate(patientId: String, studyId: String, series: DicomSeries) {
        asyncTaskExecutor.submit {
            listener.onSeriesCreate(patientId, studyId, series)
        }
    }

    override fun onSeriesUpdate(patientId: String, studyId: String, oldSeries: DicomSeries, newSeries: DicomSeries) {
        asyncTaskExecutor.submit {
            listener.onSeriesUpdate(patientId, studyId, oldSeries, newSeries)
        }
    }

    override fun onSeriesDelete(patientId: String, studyId: String, seriesId: String) {
        asyncTaskExecutor.submit {
            listener.onSeriesDelete(patientId, studyId, seriesId)
        }
    }

    override fun onImageCreate(patientId: String, studyId: String, seriesId: String, dicomImageMetaInfo: DicomImageMetaInfo) {
        asyncTaskExecutor.submit {
            listener.onImageCreate(patientId, studyId, seriesId, dicomImageMetaInfo)
        }
    }

    override fun onImageUpdate(patientId: String, studyId: String, seriesId: String, oldImageMetaInfo: DicomImageMetaInfo, newImageMetaInfo: DicomImageMetaInfo) {
        asyncTaskExecutor.submit {
            listener.onImageUpdate(patientId, studyId, seriesId, oldImageMetaInfo, newImageMetaInfo)
        }
    }

    override fun onImageDelete(patientId: String, studyId: String, seriesId: String, imageNum: String) {
        asyncTaskExecutor.submit {
            listener.onImageDelete(patientId, studyId, seriesId, imageNum)
        }
    }

}