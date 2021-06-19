import com.squareup.sqldelight.gradle.SqlDelightExtension


plugins {
    kotlin("jvm") version "1.5.10"
    id("com.squareup.sqldelight") version "1.5.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.squareup.sqldelight:sqlite-driver:1.5.0") {
//        exclude(group = "org.xerial", module = "sqlite-jdbc")
    }
//    implementation("org.xerial:sqlite-jdbc:3.34.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
}

configure<SqlDelightExtension> {
    database("TestDb") {
        packageName = "leaktest"
        schemaOutputDirectory = file("src/main/sqldelight/databases")
    }
}
