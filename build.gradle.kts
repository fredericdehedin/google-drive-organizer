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
	implementation(libs.htmx.webjars)
	//implementation(org.webjars.npm:htmx.org)

	testImplementation(libs.spring.boot.starter.actuator.test)
	testImplementation(libs.spring.boot.starter.webmvc.test)
	testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test> {
	useJUnitPlatform()
}
