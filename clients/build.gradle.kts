dependencies {
    implementation(project(":core"))
    implementation(project(":domain:domain-auth"))
    implementation(project(":domain:domain-official"))

    // Add a domain dependency here only when that domain defines a capability port to implement.
    //          implementation(project(":domain:domain-app"))    // when FileUploader is added
    //          implementation(project(":domain:domain-crew"))   // when EventPublisher is added

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.nimbusds:nimbus-jose-jwt:10.4")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("software.amazon.awssdk:s3:2.29.52")
}
