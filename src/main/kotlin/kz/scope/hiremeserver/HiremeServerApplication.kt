package kz.scope.hiremeserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters
import java.util.*
import javax.annotation.PostConstruct

@SpringBootApplication
@EntityScan(basePackageClasses = [
    HiremeServerApplication::class,
    Jsr310JpaConverters::class
])
class HiremeServerApplication {
    @PostConstruct
    fun init(): Unit = TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
}

fun main(args: Array<String>) {
    runApplication<HiremeServerApplication>(*args)
}
