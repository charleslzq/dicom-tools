package com.github.charleslzq.dicom.spring

import com.github.charleslzq.dicom.data.DicomImageMetaInfo
import com.github.charleslzq.dicom.data.DicomPatientMetaInfo
import com.github.charleslzq.dicom.data.DicomSeriesMetaInfo
import com.github.charleslzq.dicom.data.DicomStudyMetaInfo
import com.github.charleslzq.dicom.store.DicomDataListener
import org.springframework.core.task.AsyncTaskExecutor

class AsyncDicomDataListener(
        private val asyncTaskExecutor: AsyncTaskExecutor,
        private val listener: DicomDataListener
) : DicomDataListener {

    override fun onPatientMetaSaved(dicomPatientMetaInfo: DicomPatientMetaInfo) {
        asyncTaskExecutor.submit {
            listener.onPatientMetaSaved(dicomPatientMetaInfo)
        }
    }

    override fun onPatientDelete(patientId: String) {
        asyncTaskExecutor.submit {
            listener.onPatientDelete(patientId)
        }
    }

    override fun onStudyMetaSaved(patientId: String, dicomStudyMetaInfo: DicomStudyMetaInfo) {
        asyncTaskExecutor.submit {
            listener.onStudyMetaSaved(patientId, dicomStudyMetaInfo)
        }
    }

    override fun onStudyDelete(patientId: String, studyId: String) {
        asyncTaskExecutor.submit {
            listener.onStudyDelete(patientId, studyId)
        }
    }

    override fun onSeriesMetaSaved(patientId: String, studyId: String, dicomSeriesMetaInfo: DicomSeriesMetaInfo) {
        asyncTaskExecutor.submit {
            listener.onSeriesMetaSaved(patientId, studyId, dicomSeriesMetaInfo)
        }
    }

    override fun onSeriesDelete(patientId: String, studyId: String, seriesId: String) {
        asyncTaskExecutor.submit {
            listener.onSeriesDelete(patientId, studyId, seriesId)
        }
    }

    override fun onImageSaved(patientId: String, studyId: String, seriesId: String, dicomImageMetaInfo: DicomImageMetaInfo) {
        asyncTaskExecutor.submit {
            listener.onImageSaved(patientId, studyId, seriesId, dicomImageMetaInfo)
        }
    }

    override fun onImageDelete(patientId: String, studyId: String, seriesId: String, imageNum: String) {
        asyncTaskExecutor.submit {
            listener.onImageDelete(patientId, studyId, seriesId, imageNum)
        }
    }

}