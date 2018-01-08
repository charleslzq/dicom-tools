package com.github.charleslzq.dicom.data

import org.joda.time.LocalDateTime

data class DicomStudyMetaInfo(
        val id: String = "",
        val accessionNumber: String = "",
        val instanceUID: String,
        val modalities: String = "",
        val bodyPart: String = "",
        val patientAge: Int = -1,
        val date: String = "",
        val time: String = "",
        val description: String = "",
        override val uid: String = instanceUID,
        override val createTime: LocalDateTime = LocalDateTime.now(),
        override val updateTime: LocalDateTime = LocalDateTime.now()
) : Meta