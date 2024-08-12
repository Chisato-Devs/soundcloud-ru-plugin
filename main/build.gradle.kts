plugins {
    `java-library`
    kotlin("jvm")
    kotlin("plugin.serialization")
}

base {
    archivesName = "soundcloud-ru"
}

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_11
}

dependencies {
    compileOnly("commons-codec:commons-codec:1.13")
    compileOnly("dev.arbjerg:lavaplayer:2.1.2")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("commons-io:commons-io:2.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("org.jetbrains.kotlin:kotlin-annotations-jvm:1.9.0")
    implementation("com.auth0:java-jwt:4.4.0")
    compileOnly("org.slf4j:slf4j-api:2.0.7")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom {
                artifactId = base.archivesName.get()
                from(components["java"])
            }
        }
    }
}

kotlin {
    jvmToolchain(11)
}