package com.github.charleslzq.dicom.spring

import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(
        ParseConfigConfiguration::class,
        DicomStoreConfiguration::class,
        DicomDataReaderConfiguration::class,
        DicomFileWatchConfiguration::class)
annotation class EnableDicomParser