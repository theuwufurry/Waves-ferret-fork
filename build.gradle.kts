plugins {
    kotlin("jvm") version "2.0.21"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("co.uzzu.dotenv.gradle") version "2.0.0"
}

group = "gg.aquatic.waves"
version = "1.0.25"

val ktor_version: String by project

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://repo.oraxen.com/releases")
    maven("https://jitpack.io")
    maven {
        url = uri("https://repo.nekroplex.com/releases")
    }
    maven {
        url = uri("https://repo.codemc.io/repository/maven-releases/")
    }
    maven {
        url = uri("https://repo.codemc.io/repository/maven-snapshots/")
    }
}

val nmsVersion = "1.0.39"

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    implementation("gg.aquatic.aquaticseries:aquaticlib:1.0.42:all")
    implementation("gg.aquatic.aquaticseries.spigot:spigot:1.0.42")
    implementation("gg.aquatic.aquaticseries.paper:paper:1.0.42")
    implementation("gg.aquatic.aquaticseries:aquaticlib-core:1.0.42:all") {
    }
    implementation("gg.aquatic.aquaticseries.nms:NMS_v1_17_1:$nmsVersion") {
        exclude("gg.aquatic.aquaticseries.nms", "AquaticNMS")
    }
    implementation("gg.aquatic.aquaticseries.nms:NMS_v1_18_2:$nmsVersion") {
        exclude("gg.aquatic.aquaticseries.nms", "AquaticNMS")
    }
    implementation("gg.aquatic.aquaticseries.nms:NMS_v1_19_4:$nmsVersion") {
        exclude("gg.aquatic.aquaticseries.nms", "AquaticNMS")
    }
    implementation("gg.aquatic.aquaticseries.nms:NMS_v1_20_1:$nmsVersion") {
        exclude("gg.aquatic.aquaticseries.nms", "AquaticNMS")
    }
    implementation("gg.aquatic.aquaticseries.nms:NMS_v1_20_4:$nmsVersion") {
        exclude("gg.aquatic.aquaticseries.nms", "AquaticNMS")
    }
    implementation("gg.aquatic.aquaticseries.nms:NMS_v1_20_6:$nmsVersion") {
        exclude("gg.aquatic.aquaticseries.nms", "AquaticNMS")
    }
    implementation("gg.aquatic.aquaticseries.nms:NMS_v1_21:$nmsVersion") {
        exclude("gg.aquatic.aquaticseries.nms", "AquaticNMS")
    }
    implementation("gg.aquatic.aquaticseries.nms:NMS_v1_21_1:$nmsVersion") {
        exclude("gg.aquatic.aquaticseries.nms", "AquaticNMS")
    }
    implementation("gg.aquatic.wavessync:wavessync-api:1.0.1:all")

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-websockets:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp-jvm:2.3.12")
    implementation("io.ktor:ktor-client-auth:$ktor_version")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("com.github.retrooper:packetevents-spigot:2.5.0")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("gg.aquatic:AEAPI:1.0")
    compileOnly("io.th0rgal:oraxen:1.171.0")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.2-beta-r3-b")
    compileOnly ("com.ticxo.modelengine:ModelEngine:R4.0.4")
    compileOnly("io.lumine:Mythic-Dist:5.6.1")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.2")
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

    //configurations = listOf(project.configurations.compileClasspath.get())
    /*
    dependencies {
        include(dependency("gg.aquatic.aquaticseries:aquaticlib"))
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
        include(dependency("com.github.retrooper:packetevents-spigot"))
    }
     */
    //configurations = listOf(project.configurations.implementation.get(), project.configurations.runtimeOnly.get())
    /*
    from({
        project.configurations.runtimeClasspath.get().filter { it.name.contains("kotlinx-coroutines-core") }
            .map { project.zipTree(it) }
    })
     */
    // Relocate packages
    relocate("kotlinx.coroutines", "gg.aquatic.waves.shadow.kotlinx.coroutines")
    relocate("com.github.retrooper", "gg.aquatic.waves.shadow.com.retrooper")
    relocate("io.github.retrooper", "gg.aquatic.waves.shadow.io.retrooper")

    // Exclude the original (unrelocated) kotlinx-coroutines-core package
    exclude("META-INF/versions/9/module-info.class")
    exclude("kotlin/**")
    exclude("com/google/**","com/typesafe/**", "io/netty/**", "org/slf4j/**")
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