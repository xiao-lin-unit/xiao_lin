plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenLocal()
    gradlePluginPortal()
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("interfaceConventions") {
            id = "interface-conventions"
            implementationClass = "InterfaceConventionsPlugin"
        }
        create("applicationConventions") {
            id = "application-conventions"
            implementationClass = "ApplicationConventionsPlugin"
        }
        create("domainConventions") {
            id = "domain-conventions"
            implementationClass = "DomainConventionsPlugin"
        }
        create("infraConventions") {
            id = "infra-conventions"
            implementationClass = "InfraConventionsPlugin"
        }
    }
}

