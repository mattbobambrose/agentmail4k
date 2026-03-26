plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.pambrose.stable.versions)
    alias(libs.plugins.pambrose.testing)
    `maven-publish`
    application
}

application {
    mainClass.set("MainKt")
}

group = "com.mattbobambrose.agentmail4k"
version = "0.1.0"

repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.auth)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.ktor.client.mock)
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "com.mattbobambrose.agentmail4k"
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
