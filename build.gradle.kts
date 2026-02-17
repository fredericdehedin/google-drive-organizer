plugins {
	java
	alias(libs.plugins.spring.boot)
}

group = "com.fde"
version = "0.0.1-SNAPSHOT"
description = "Google Drive Organizer"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

sourceSets {
	main {
		java.srcDir("src/main/java")
	}
	test {
		java.srcDir("src/test/java")
	}
}

repositories {
	mavenCentral()
}

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2.client)
    implementation(libs.htmx.webjars)
    implementation(libs.google.drive)
    implementation(libs.tika.core)
    implementation(libs.tika.parsers.standard)
    implementation(libs.tika.parser.ocr)

    testImplementation(platform(libs.spring.boot.bom))
    testImplementation(libs.spring.boot.starter.test)
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test> {
	useJUnitPlatform()
}
