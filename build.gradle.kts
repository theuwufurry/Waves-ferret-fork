plugins {
    kotlin("jvm") version "2.0.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "gg.aquatic.waves"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven {
        url = uri("https://repo.nekroplex.com/releases")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    implementation("gg.aquatic.aquaticseries:aquaticlib-17:1.0.6:all") {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib")
        exclude("gg.aquatic.aquaticseries", "aquaticlib")
        exclude("gg.aquatic.aquaticseries.core", "core")
    }
}


kotlin {
    jvmToolchain(17)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("Waves-${project.version}.jar")
    archiveClassifier.set("plugin")
    dependencies {
        include(dependency("gg.aquatic.aquaticseries:aquaticlib-17"))
    }
}