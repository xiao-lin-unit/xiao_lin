import org.gradle.api.Project

class InfraConventionsPlugin: ConventionsPlugin() {

    override fun projectRepositories(target: Project): List<String> {
        return listOf(
            autolibs.Mavens.aliyun
        )
    }

    override fun projectVersion(): String {
        return autolibs.Versions.projectVersion
    }

    override fun projectDependencies(target: Project): List<TypeDependency> {
        return listOf(
            TypeDependency(DependencyType.PLATFORM, autolibs.Libraries.springBootDependencies),
            TypeDependency(DependencyType.DEPENDENCY, autolibs.Libs.springContext),
            TypeDependency(DependencyType.DEPENDENCY, autolibs.Libs.springBootAutoconfigure),
            TypeDependency(DependencyType.DEPENDENCY, autolibs.Libs.postgresql, "runtimeOnly")
        )
    }
}
