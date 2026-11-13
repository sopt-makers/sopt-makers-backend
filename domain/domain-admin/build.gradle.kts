dependencies {
    implementation(project(":core"))
    implementation(project(":domain:domain-user"))
    implementation("org.springframework:spring-tx")
    implementation("org.springframework:spring-context")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito:mockito-core")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
