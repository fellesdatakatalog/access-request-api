package no.digdir.accessrequestapi

import org.springframework.boot.SpringApplication

fun main(args: Array<String>) {
    System.setProperty("spring.profiles.active", "dev")
    SpringApplication.run(Application::class.java, *args)
}
