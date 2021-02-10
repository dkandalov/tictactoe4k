import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    java
    kotlin("jvm") version "1.4.30"
}
repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://kotlin.bintray.com/kotlinx")
    maven("https://dl.bintray.com/dkandalov/maven")
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(platform("org.http4k:http4k-bom:4.3.0.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-apache")
    implementation("org.http4k:http4k-client-okhttp")
    implementation("org.http4k:http4k-format-jackson")
    implementation("org.http4k:http4k-testing-approval")
    implementation("org.http4k:http4k-template-handlebars")
    implementation("org.slf4j:slf4j-nop:1.7.30") // Suppress log warnings from handlebars
    testImplementation("datsok:datsok:0.4")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
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

java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

tasks.test {
    useJUnitPlatform()
}