package com.github.charleslzq.dicom

import org.dcm4che3.io.DicomInputHandler
import org.dcm4che3.io.DicomInputStream
import java.util.function.Supplier

interface DicomInputParser<T> : DicomInputHandler, Supplier<T> {

    fun parse(input: DicomInputStream): T {
        input.setDicomInputHandler(this)
        input.readDataset(-1, -1)
        return get()
    }

}