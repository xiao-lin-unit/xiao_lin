import org.gradle.api.Project

class InterfaceConventionsPlugin: ConventionsPlugin() {

    override fun projectRepositories(target: Project): List<String> {
        return listOf(
            autolibs.Mavens.aliyun
        )
    }

    override fun projectVersion(): String {
        return autolibs.Versions.projectVersion
    }

}
