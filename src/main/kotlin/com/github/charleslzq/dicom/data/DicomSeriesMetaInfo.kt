package com.github.charleslzq.dicom.data

import org.joda.time.LocalDateTime

data class DicomSeriesMetaInfo(
        val number: Int = -1,
        val instanceUID: String,
        val modality: String = "",
        val date: String = "",
        val time: String = "",
        val description: String = "",
        override val uid: String = instanceUID,
        override val createTime: LocalDateTime = LocalDateTime.now(),
        override val updateTime: LocalDateTime = LocalDateTime.now()
) : Meta