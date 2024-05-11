package com.munir_atef.pha_viewer.service_groups

import com.munir_atef.pha_viewer.service.ServiceResult

interface ServiceInterface {
    fun invoke(service: String, body: String): ServiceResult
}

