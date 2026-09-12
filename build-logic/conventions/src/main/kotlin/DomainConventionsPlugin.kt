import org.gradle.api.Project

class DomainConventionsPlugin: ConventionsPlugin() {

    override fun projectRepositories(target: Project): List<String> {
        return listOf(
            autolibs.Mavens.aliyun
        )
    }

    override fun projectVersion(): String {
        return autolibs.Versions.projectVersion
    }

//    override fun projectDependencies(target: Project): List<TypeDependency> {
//        val dependencies = mutableListOf<TypeDependency>()
//        if (target.name != "shared-domain") {
//            dependencies.add(TypeDependency(DependencyType.PROJECT, autolibs.Libs.shared))
//        }
//        return dependencies
//    }

}
