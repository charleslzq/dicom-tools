package com.github.charleslzq.dicom.data

import com.google.common.collect.Maps
import java.time.LocalDateTime

data class DicomStoreMetaInfo(
        val updateTime: MutableMap<String, LocalDateTime> = Maps.newHashMap()
)