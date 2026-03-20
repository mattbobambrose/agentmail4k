plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    `maven-publish`
}

group = "to.agentmail"
version = "0.1.0"

repositories {
    mavenCentral()
}

val ktorVersion = "3.1.1"

dependencies {
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    testImplementation("io.kotest:kotest-runner-junit5:6.0.0.M1")
    testImplementation("io.kotest:kotest-assertions-core:6.0.0.M1")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "to.agentmail"
            artifactId = "agentmail-sdk"
            version = project.version.toString()

            pom {
                name.set("AgentMail SDK")
                description.set("Kotlin SDK for the AgentMail API")
                url.set("https://github.com/agentmail-to/agentmail-kotlin")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("agentmail")
                        name.set("AgentMail")
                        email.set("support@agentmail.to")
                    }
                }
                scm {
                    url.set("https://github.com/agentmail-to/agentmail-kotlin")
                }
            }
        }
    }
}
