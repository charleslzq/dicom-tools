package com.github.charleslzq.dicom.data

import org.joda.time.LocalDateTime

interface Meta {
    val uid: String
    var createTime: LocalDateTime
    var updateTime: LocalDateTime
}