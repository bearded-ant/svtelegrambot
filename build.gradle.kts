import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.7"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
}

group = "com.ant"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-quartz")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	// firebase
	implementation("com.google.firebase:firebase-admin:9.1.1")

	//jsoup
	implementation("org.jsoup:jsoup:1.15.3")

	//telegram bot
//	implementation("dev.inmo:tgbotapi:5.0.0")
//	implementation("dev.inmo:tgbotapi.core:5.0.0")
//	implementation("dev.inmo:tgbotapi.api:5.0.0")
//	implementation("dev.inmo:tgbotapi.utils:5.0.0")

	implementation("org.telegram:telegrambots:6.4.0")

//
//	runtimeOnly("com.h2database:h2")
//
//
//	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
