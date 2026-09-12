pluginManagement {
    includeBuild("build-logic")
}

rootProject.name = "xiao_lin"

include("gateway")

include("shared",
    "shared:shared-domain",
    "shared:shared-application",
    "shared:shared-infra",
    "shared:shared-interfaces",
    "shared:shared-common"
    )

include(
    "system-service",
    "system-service:system-domain",
    "system-service:system-interfaces",
    "system-service:system-application",
    "system-service:system-infra"
)


