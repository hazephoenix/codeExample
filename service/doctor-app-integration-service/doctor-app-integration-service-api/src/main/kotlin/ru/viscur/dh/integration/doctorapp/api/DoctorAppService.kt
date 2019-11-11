package ru.viscur.dh.integration.doctorapp.api

import ru.viscur.dh.integration.doctorapp.api.cmd.AcceptDoctorCallCmd
import ru.viscur.dh.integration.doctorapp.api.cmd.CallableDoctorStatusChangedCmd
import ru.viscur.dh.integration.doctorapp.api.cmd.DeclineDoctorCallCmd
import ru.viscur.dh.integration.doctorapp.api.cmd.NewDoctorCallCmd
import ru.viscur.dh.integration.doctorapp.api.model.CallableDoctor
import ru.viscur.dh.integration.doctorapp.api.model.DoctorCall
import ru.viscur.dh.integration.doctorapp.api.request.DoctorCallsRequest

interface DoctorAppService {

    /**
     * Новый вызов доктора
     * @throws ru.viscur.dh.integration.doctorapp.api.exception.DoctorCallNotAllowedException в случае если
     *      вызов доктора был заблокирован (в промежутке между вызовом)
     */
    fun newCall(cmd: NewDoctorCallCmd): DoctorCall

    /**
     * Доктор принял вызов
     */
    fun acceptCall(cmd: AcceptDoctorCallCmd)

    /**
     * Доктор отклонил вызов
     */
    fun declineCall(cmd: DeclineDoctorCallCmd)

    /**
     * Получить список докторов, которых можно вызвать (всех).
     */
    fun findCallableDoctors(): List<CallableDoctor>

    /**
     * Статус доктора изменился (он перешел в интенсивную терапию или вышел из нее)
     */
    fun callableDoctorStatusChanged(doctor: CallableDoctorStatusChangedCmd)

    /**
     * Получить список вызовов врача
     */
    fun getDoctorCalls(request: DoctorCallsRequest)

}