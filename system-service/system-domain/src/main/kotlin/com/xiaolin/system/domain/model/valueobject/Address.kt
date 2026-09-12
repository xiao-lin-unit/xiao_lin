package com.xiaolin.system.domain.model.valueobject

data class Address(
    val province: String?,
    val provinceCode: String?,
    val city: String?,
    val cityCode: String?,
    val address: String?) {
    val fullAddress: String
        get() = "$province $city $address"
}
