import org.gradle.api.Plugin
import org.gradle.api.Project


abstract class ConventionsPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            repositories.apply {
                for (rep in projectRepositories(target)) {
                    maven { url = uri(rep) }
                }
            }
            if (projectVersion().isNotBlank()) {
                version.apply {
                    projectVersion()
                }
            }
            dependencies.apply {
                for (dep in projectDependencies(target)) {
                    when(dep.type) {
                        DependencyType.PLATFORM -> add(dep.mode, platform(dep.dependency))
                        DependencyType.PROJECT -> add(dep.mode, project(dep.dependency))
                        DependencyType.DEPENDENCY -> add(dep.mode, dep.dependency)
                    }

                }
            }
        }
    }

    protected open fun projectRepositories(target: Project): List<String> {
        return listOf();
    }

    protected open fun projectVersion(): String {
        return ""
    }

    protected open fun projectDependencies(target: Project): List<TypeDependency> {
        return listOf()
    }

    protected class TypeDependency(val type: DependencyType, val dependency: String, val mode: String = "implementation")

    protected enum class DependencyType {
        PLATFORM, PROJECT, DEPENDENCY
    }

}