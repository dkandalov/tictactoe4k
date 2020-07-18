import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    java
    kotlin("jvm") version "1.4-M3"
}
repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://kotlin.bintray.com/kotlinx")
    maven("https://dl.bintray.com/dkandalov/maven")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.http4k:http4k-core:3.245.0")
    implementation("org.http4k:http4k-server-apache:3.245.0")
    implementation("org.http4k:http4k-client-okhttp:3.245.0")
    implementation("org.http4k:http4k-format-jackson:3.245.0")
    implementation("org.http4k:http4k-testing-approval:3.245.0")
    implementation("org.http4k:http4k-template-handlebars:3.245.0")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.1")
    implementation("org.slf4j:slf4j-nop:1.7.30") // this is to suppress log warnings from handlebars
    testImplementation("datsok:datsok:0.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

sourceSets["main"].withConvention(KotlinSourceSet::class) {
    kotlin.srcDir("src/main")
}
sourceSets["test"].withConvention(KotlinSourceSet::class) {
    kotlin.srcDir("src/test")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
}