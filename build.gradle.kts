plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.pambrose.stable.versions)
    alias(libs.plugins.pambrose.testing)
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
    application
}

application {
    mainClass.set("MainKt")
}

group = "com.agentmail4k"
version = "0.1.1"

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
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

dokka {
    moduleName.set("agentmail4k")
    pluginsConfiguration.html {
        homepageLink.set("https://github.com/mattbobambrose/agentmail4k")
        footerMessage.set("agentmail4k")
    }
}

mavenPublishing {
    coordinates("com.agentmail4k", "agentmail4k", version.toString())

    pom {
        name.set("agentmail4k")
        description.set("Kotlin DSL for the AgentMail API")
        url.set("https://github.com/mattbobambrose/agentmail4k")
        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("mattbobambrose")
                name.set("Matthew Ambrose")
                email.set("matthew@agentmail4k.com")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/mattbobambrose/agentmail4k.git")
            developerConnection.set("scm:git:ssh://github.com/mattbobambrose/agentmail4k.git")
            url.set("https://github.com/mattbobambrose/agentmail4k")
        }
    }

    publishToMavenCentral()
    signAllPublications()
}
