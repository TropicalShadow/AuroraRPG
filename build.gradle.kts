import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    java
    kotlin("jvm") version "1.4.31"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "me.tropicalshadow"
version = "0.0.2"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-platform-bukkit:4.0.0-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("mysql:mysql-connector-java:5.1.46")
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.7.2")

}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}
tasks {
    withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        archiveFileName.set(rootProject.name + "-V" + rootProject.version + ".jar")
        relocate("kotlin", "com.github.tropicalshadow.dependencies.kotlin")
        relocate("kotlinx", "com.github.tropicalshadow.dependencies.kotlinx")
        relocate("org.jetbrains", "com.github.tropicalshadow.dependencies.jetbrains")
        relocate("org.intellij", "com.github.tropicalshadow.dependencies.jetbrains.intellij")
        relocate("com.zaxxer.hikari", "com.github.tropicalshadow.lib.hikari")
        exclude("DebugProbesKt.bin")
        exclude("META-INF/**")

    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
    processResources {
        filter<ReplaceTokens>("tokens" to mapOf("version" to project.version))
    }

}