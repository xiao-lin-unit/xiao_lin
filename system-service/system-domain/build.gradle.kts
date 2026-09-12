plugins {
    kotlin("jvm") version "2.3.21"
    id("domain-conventions")
}

group = "com.xiaolin"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":shared:shared-domain"))
    implementation(project(":shared:shared-common"))


    implementation("org.springframework.data:spring-data-commons:4.1.0")
}

kotlin {
    jvmToolchain(21)
}
