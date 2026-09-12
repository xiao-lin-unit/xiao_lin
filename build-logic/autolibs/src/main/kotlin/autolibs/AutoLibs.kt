package autolibs

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.PluginDependenciesSpecScope
import org.gradle.plugin.use.PluginDependencySpec

object Versions {

  const val springBootStarter = "4.1.0"
  const val projectVersion = "0.0.1"
  const val springBootDependencies = "4.1.0"
    
}

object Libraries {

  const val springBootStarter = "org.springframework.boot:spring-boot-starter:4.1.0"
  const val springBootDependencies = "org.springframework.boot:spring-boot-dependencies:4.1.0"
  const val springContext = "org.springframework:spring-context:undefined"
  const val springBootAutoconfigure = "org.springframework.boot:spring-boot-autoconfigure:undefined"
  const val postgresql = "org.postgresql:postgresql:undefined"
  const val shared = ":shared:shared-domain:0.0.1"
    
}
object Libs {

  const val springBootStarter = "org.springframework.boot:spring-boot-starter"
  const val springBootDependencies = "org.springframework.boot:spring-boot-dependencies"
  const val springContext = "org.springframework:spring-context"
  const val springBootAutoconfigure = "org.springframework.boot:spring-boot-autoconfigure"
  const val postgresql = "org.postgresql:postgresql"
  const val shared = ":shared:shared-domain"
    
}

object Plugins {


    
}

object FullPlugins {


    
}

object Bundles {


    
}

object Extensions {


    
}

object Mavens {

  const val aliyun = "https://maven.aliyun.com/repository/public"
    
}
