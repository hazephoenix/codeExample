package ru.viscur.dh.location.impl

import ru.viscur.dh.datastorage.api.model.ReaderEventLog
import ru.viscur.dh.location.api.ReaderEvent

fun ReaderEvent.readerEventLog() = ReaderEventLog(0L, stamp, reader, channel, zone, tags)
