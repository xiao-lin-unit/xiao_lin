import org.gradle.api.Project
import kotlin.reflect.full.memberProperties

class ApplicationConventionsPlugin: ConventionsPlugin() {

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
            TypeDependency(DependencyType.PLATFORM, autolibs.Libraries.springBootDependencies)
        )
    }

}
