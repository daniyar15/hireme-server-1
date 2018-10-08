package kz.scope.hiremeserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HiremeServerApplication

fun main(args: Array<String>) {
    runApplication<HiremeServerApplication>(*args)
}
