plugins {
    kotlin("jvm") version "2.3.10"
    id("domain-conventions")
}

group = "com.xiaolin"

repositories {
    mavenCentral()
}

dependencies {

    implementation(project(":shared:shared-common"))
}

kotlin {
    jvmToolchain(21)
}