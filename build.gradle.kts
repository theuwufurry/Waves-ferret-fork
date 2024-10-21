plugins {
    kotlin("jvm") version "2.0.20"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("co.uzzu.dotenv.gradle") version "2.0.0"
}

group = "gg.aquatic.waves"
version = "1.0.7"

val ktor_version: String by project

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
    implementation("gg.aquatic.aquaticseries:aquaticlib:1.0.26:all") {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib")
        exclude("gg.aquatic.aquaticseries.core", "core")
        exclude("gg.aquatic.aquaticseries.paper", "paper")
        exclude("gg.aquatic.aquaticseries.spigot", "spigot")
    }
    implementation("gg.aquatic.wavessync:wavessync-api:1.0.1:all")

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-websockets:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp-jvm:2.3.12")
    implementation("io.ktor:ktor-client-auth:$ktor_version")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
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
        include(dependency("gg.aquatic.aquaticseries:aquaticlib"))
    }
}


val maven_username = if (env.isPresent("MAVEN_USERNAME")) env.fetch("MAVEN_USERNAME") else ""
val maven_password = if (env.isPresent("MAVEN_PASSWORD")) env.fetch("MAVEN_PASSWORD") else ""

publishing {
    repositories {
        maven {
            name = "aquaticRepository"
            url = uri("https://repo.nekroplex.com/releases")

            credentials {
                username = maven_username
                password = maven_password
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "gg.aquatic.waves"
            artifactId = "Waves"
            version = "${project.version}"
            from(components["java"])
        }
    }
}