plugins {
    alias(libs.plugins.jvm)
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.gson)

    testImplementation(platform(libs.junit.jupiter.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation(libs.kotest)
    testImplementation(libs.kotest.json.assertion)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.register("create-test-dsl") {
    doFirst {
        val filename = project.property("filename")
        layout.projectDirectory
            .file("src/test/resources/parser/${filename}.dsl")
            .asFile
            .writeText("")
        layout.projectDirectory
            .file("src/test/resources/parser/${filename}.json")
            .asFile
            .writeText(
                """
                |{
                |  "children": [],
                |  "type": "StructurizrDslFile"
                |}
                """.trimMargin("|")
            )
    }
}
