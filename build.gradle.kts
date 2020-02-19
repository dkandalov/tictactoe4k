import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.61"
}
repositories {
    mavenCentral()
    jcenter()
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
    implementation("org.http4k:http4k-core:3.235.0")
    implementation("org.http4k:http4k-server-jetty:3.235.0")
    implementation("org.http4k:http4k-client-okhttp:3.235.0")
    implementation("org.http4k:http4k-format-jackson:3.235.0")
    implementation("org.http4k:http4k-testing-approval:3.235.0")
    implementation("org.http4k:http4k-template-handlebars:3.235.0")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.1")
    implementation("dkandalov:kotlin-common-test:0.1.4")
    implementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    implementation(kotlin("stdlib"))
    implementation(kotlin("test"))
    implementation(kotlin("test-junit"))
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