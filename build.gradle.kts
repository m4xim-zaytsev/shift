plugins {
    id("java")
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

application {
    mainClass.set("org.example.Main")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.example.Main"
    }
    destinationDirectory.set(file("c:\\Users\\zayts\\Desktop"))
    archiveFileName.set("test.jar")
}
