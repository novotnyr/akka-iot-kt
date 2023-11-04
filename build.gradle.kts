plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "com.github.novotnyr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.typesafe.akka:akka-actor-typed_2.13:2.6.21")
    implementation("com.typesafe.akka:akka-cluster-typed_2.13:2.6.21")
    implementation("org.scala-lang:scala-library:2.13.12")
    runtimeOnly("ch.qos.logback:logback-classic:1.4.11")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass = "com.github.novotnyr.akka.Runner"
    val akkaHost: String? by project
    val akkaPort: String? by project
    val akkaSeedNode: String? by project
    applicationDefaultJvmArgs = listOf(
        "-Dakka.remote.artery.canonical.hostname=$akkaHost",
        "-Dakka.remote.artery.canonical.port=$akkaPort",
        "-Dakka.cluster.seed-nodes.0=akka://smarthome@$akkaSeedNode"
    )
}