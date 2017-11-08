package com.github.charleslzq.dicom

import com.google.common.collect.Lists
import org.dcm4che3.data.*
import org.dcm4che3.io.DicomInputStream
import org.dcm4che3.util.TagUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DicomTagInfoReader : DicomInputParser<List<DicomTagInfo>> {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val tagList: MutableList<DicomTagInfo> = Lists.newArrayList()
    private lateinit var bulkDataUri: String

    override fun get(): List<DicomTagInfo> {
        return tagList.toList()
    }

    override fun readValue(dicomInputStream: DicomInputStream, attributes: Attributes) {
        val tag = dicomInputStream.tag()
        if (TagUtils.isGroupLength(tag) || dicomInputStream.isExcludeBulkData) {
            dicomInputStream.readValue(dicomInputStream, attributes)
        } else {
            val vr = dicomInputStream.vr()
            val length = dicomInputStream.length()
            if (vr == VR.SQ || length == -1) {
                dicomInputStream.readValue(dicomInputStream, attributes)
            } else if (length > 0) {
                if (dicomInputStream.isIncludeBulkDataURI) {
                    bulkDataUri = dicomInputStream.createBulkData(dicomInputStream).uri
                } else {
                    val tagId = TagUtils.toString(tag)
                    val tagName = ElementDictionary.keywordOf(tag, null)
                    log.debug("VR:{}, length: {}, id:{}, name:{}", vr, length, tagId, tagName)
                    val bytes = dicomInputStream.readValue()
                    if (tag == Tag.TransferSyntaxUID || tag == Tag.SpecificCharacterSet || TagUtils.isPrivateCreator(tag)) {
                        attributes.setBytes(tag, vr, bytes)
                    }
                    val stringValueBuilder = StringBuilder()
                    if (vr.prompt(bytes, dicomInputStream.bigEndian(), attributes.specificCharacterSet, Int.MAX_VALUE, stringValueBuilder)) {
                        tagList.add(DicomTagInfo(vr, tag, tagId, tagName, stringValueBuilder.toString()))
                    }
                }
            }
        }
    }

    override fun readValue(dicomInputStream: DicomInputStream, sequence: Sequence) {
        dicomInputStream.readValue(dicomInputStream, sequence)
    }

    override fun readValue(p0: DicomInputStream?, p1: Fragments?) {

    }

    override fun startDataset(p0: DicomInputStream?) {
        log.info("Start to read metadata")
    }

    override fun endDataset(p0: DicomInputStream?) {
        log.info("End reading metadata")
    }
}