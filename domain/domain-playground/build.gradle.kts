dependencies {
    implementation(project(":core"))
    implementation(project(":domain:domain-user"))
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")
    implementation("tools.jackson.core:jackson-databind")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito:mockito-core")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
