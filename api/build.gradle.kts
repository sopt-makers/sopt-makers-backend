plugins {
    id("org.springframework.boot")
}

tasks.jar { enabled = false }

dependencies {
    implementation(project(":core"))
    implementation(project(":domain:domain-user"))
    implementation(project(":domain:domain-auth"))
    implementation(project(":domain:domain-playground"))
    implementation(project(":domain:domain-app"))
    implementation(project(":domain:domain-admin"))
    implementation(project(":domain:domain-official"))
    implementation(project(":storage"))
    implementation(project(":clients"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.data:spring-data-commons")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("com.h2database:h2")
}
