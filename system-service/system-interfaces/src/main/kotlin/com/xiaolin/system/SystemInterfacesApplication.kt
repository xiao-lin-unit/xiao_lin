package com.xiaolin.system

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.xiaolin.system", "com.xiaolin.shared"])
class SystemInterfacesApplication

fun main(args: Array<String>) {
    val runApplication = runApplication<SystemInterfacesApplication>(*args)
}
