package com.github.charleslzq.dicom.data

import org.joda.time.LocalDateTime

data class DicomStoreMetaInfo(
        val updateTime: MutableMap<String, LocalDateTime> = emptyMap<String, LocalDateTime>().toMutableMap()
)