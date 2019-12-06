package ru.viscur.dh.datastorage.impl.entity

import ru.viscur.dh.practitioner.call.model.AwaitingCallStatusStage
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "practitioner_call_awaiting_ref")
class PractitionerCallAwaitingRefEntity {
    @Id
    @Column(name = "call_id")
    var callId: String = ""
    @Column(name = "stage_date_time")
    var lastStageDateTime: Date = Date()
    @Column(name = "stage")
    var awaitingCallStatusStage: AwaitingCallStatusStage = AwaitingCallStatusStage.PractitionerAppCall
    @Column(name = "voice_call_count")
    var voiceCallCount: Int? = null
}