import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    java
    kotlin("jvm") version "1.4-M1"
}
repositories {
    mavenCentral()
    maven ("https://dl.bintray.com/kotlin/kotlin-eap")
    maven ("https://kotlin.bintray.com/kotlinx")
    ivy {
        setUrl("https://raw.githubusercontent.com/dkandalov/")
        patternLayout {
            artifact("kotlin-common/master/jars/[organisation]-[artifact]-[revision](-[classifier])(.[ext])")
            artifact("kotlin-common-test/master/jars/[organisation]-[artifact]-[revision](-[classifier])(.[ext])")
            metadataSources { artifact() }
        }
    }
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
    testImplementation("dkandalov:kotlin-common-test:0.1.4")
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