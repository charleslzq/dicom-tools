package com.github.charleslzq.dicom.data

import java.time.LocalDateTime

data class DicomStoreMetaInfo(
        val updateTime: MutableMap<String, LocalDateTime> = emptyMap<String, LocalDateTime>().toMutableMap()
)