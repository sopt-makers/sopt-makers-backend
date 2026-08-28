dependencies {
    implementation(project(":core"))
    implementation(project(":domain:domain-auth"))
    implementation(project(":domain:domain-admin"))
    implementation(project(":domain:domain-official"))
    implementation(project(":domain:domain-playground"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.nimbusds:nimbus-jose-jwt:10.4")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("software.amazon.awssdk:s3:2.29.52")
    implementation("software.amazon.awssdk:scheduler:2.29.52")
    implementation("org.jsoup:jsoup:1.18.3")
}
