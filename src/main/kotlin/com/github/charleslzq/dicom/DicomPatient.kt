package com.github.charleslzq.dicom

data class DicomPatient(
        var id: String = "",
        var name: String = "",
        var address: String = "",
        var sex: String = "",
        var age: Int = -1,
        var birthday: String = "",
        var idIssuer: String = "",
        var position: String = ""
)