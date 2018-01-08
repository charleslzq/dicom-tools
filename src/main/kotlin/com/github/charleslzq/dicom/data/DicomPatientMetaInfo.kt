package com.github.charleslzq.dicom.data

import org.joda.time.LocalDateTime

data class DicomPatientMetaInfo(
        val id: String,
        val name: String = "",
        val address: String = "",
        val sex: String = "",
        val birthday: String = "",
        val birthTime: String = "",
        val weight: Float = -1f,
        val idIssuer: String = "",
        val position: String = "",
        val pregnancyStatus: Int = -1,
        val institutionName: String = "",
        val institutionAddress: String = "",
        override val uid: String = id,
        override val createTime: LocalDateTime = LocalDateTime.now(),
        override val updateTime: LocalDateTime = LocalDateTime.now()
) : Meta