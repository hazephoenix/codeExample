package ru.viscur.dh.apps.rfidlocationdevice

import ru.viscur.dh.location.api.ReaderEvent
import java.time.Instant
import java.util.regex.Pattern


fun parseLine(line: String, delimiter: Pattern = Pattern.compile("\\s+")) = line.split(delimiter).let { tokens ->
    ReaderEvent(
            stamp = Instant.ofEpochMilli(tokens[0].toLong()),
            reader = tokens[1],
            channel = tokens[2],
            zone = tokens[3],
            tags = tokens.subList(4, tokens.size)
    )
}

