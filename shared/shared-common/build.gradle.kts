plugins {
    kotlin("jvm") version "2.3.21"
}

group = "com.xiaolin"

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.projectlombok:lombok:1.18.46")
}

kotlin {
    jvmToolchain(21)
}