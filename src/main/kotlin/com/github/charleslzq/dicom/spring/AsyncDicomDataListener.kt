package com.github.charleslzq.dicom.spring

import com.github.charleslzq.dicom.data.ImageMeta
import com.github.charleslzq.dicom.data.Meta
import com.github.charleslzq.dicom.store.DicomDataListener
import org.springframework.core.task.AsyncTaskExecutor

class AsyncDicomDataListener<in P : Meta, in T : Meta, in E : Meta, in I : ImageMeta>(
        private val asyncTaskExecutor: AsyncTaskExecutor,
        private val listener: DicomDataListener<P, T, E, I>
) : DicomDataListener<P, T, E, I> {

    override fun onPatientMetaSaved(dicomPatientMetaInfo: P) {
        asyncTaskExecutor.submit {
            listener.onPatientMetaSaved(dicomPatientMetaInfo)
        }
    }

    override fun onPatientDelete(patientId: String) {
        asyncTaskExecutor.submit {
            listener.onPatientDelete(patientId)
        }
    }

    override fun onStudyMetaSaved(patientId: String, dicomStudyMetaInfo: T) {
        asyncTaskExecutor.submit {
            listener.onStudyMetaSaved(patientId, dicomStudyMetaInfo)
        }
    }

    override fun onStudyDelete(patientId: String, studyId: String) {
        asyncTaskExecutor.submit {
            listener.onStudyDelete(patientId, studyId)
        }
    }

    override fun onSeriesMetaSaved(patientId: String, studyId: String, dicomSeriesMetaInfo: E) {
        asyncTaskExecutor.submit {
            listener.onSeriesMetaSaved(patientId, studyId, dicomSeriesMetaInfo)
        }
    }

    override fun onSeriesDelete(patientId: String, studyId: String, seriesId: String) {
        asyncTaskExecutor.submit {
            listener.onSeriesDelete(patientId, studyId, seriesId)
        }
    }

    override fun onImageSaved(patientId: String, studyId: String, seriesId: String, dicomImageMetaInfo: I) {
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