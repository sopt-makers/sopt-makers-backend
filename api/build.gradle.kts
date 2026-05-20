plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain:domain-user"))
    implementation(project(":domain:domain-auth"))
    implementation(project(":domain:domain-playground"))
    implementation(project(":domain:domain-crew"))
    implementation(project(":domain:domain-app"))
    implementation(project(":domain:domain-admin"))
    implementation(project(":storage"))
    implementation(project(":clients"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
