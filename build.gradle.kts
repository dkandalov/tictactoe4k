import org.gradle.api.JavaVersion.*
import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    java
    kotlin("jvm") version "1.8.20"
}
repositories {
    mavenCentral()
    ivy {
        artifactPattern("https://raw.githubusercontent.com/dkandalov/[module]/master/jars/[artifact]-[revision](.[ext])")
    }
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(platform("org.http4k:http4k-bom:4.42.1.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-apache")
    implementation("org.http4k:http4k-client-okhttp")
    implementation("org.http4k:http4k-format-moshi")
    implementation("org.http4k:http4k-testing-approval")
    implementation("org.http4k:http4k-template-handlebars")
    implementation("org.slf4j:slf4j-nop:1.7.36") // Suppress log warnings from handlebars
    testImplementation("datsok:datsok:0.5")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
}

kotlin.sourceSets["main"].apply {
    kotlin.srcDir("src/main")
}
kotlin.sourceSets["test"].apply {
    kotlin.srcDir("src/test")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
}

java.sourceCompatibility = VERSION_11
java.targetCompatibility = VERSION_11

tasks.test {
    useJUnitPlatform()
}