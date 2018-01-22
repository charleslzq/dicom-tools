package com.github.charleslzq.dicom.data

import org.joda.time.LocalDateTime

data class DicomSeriesMetaInfo(
        val number: Int = -1,
        val instanceUID: String = "",
        val modality: String = "",
        val date: String = "",
        val time: String = "",
        val description: String = ""
) : Meta {
    override val uid: String = instanceUID
    override var createTime: LocalDateTime = LocalDateTime.now()
    override var updateTime: LocalDateTime = LocalDateTime.now()
}