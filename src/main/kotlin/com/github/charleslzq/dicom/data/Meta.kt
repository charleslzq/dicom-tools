package com.github.charleslzq.dicom.data

import org.joda.time.LocalDateTime

interface Meta {
    val uid: String
    val createTime: LocalDateTime
    val updateTime: LocalDateTime
}