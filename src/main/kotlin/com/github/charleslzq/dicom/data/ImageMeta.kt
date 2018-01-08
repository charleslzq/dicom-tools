package com.github.charleslzq.dicom.data

import java.net.URI

interface ImageMeta : Meta {
    val files: MutableMap<String, URI>
}