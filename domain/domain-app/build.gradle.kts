dependencies {
    implementation(project(":core"))
    implementation(project(":domain:domain-user"))
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
