dependencies {
    implementation(project(":core"))
    implementation(project(":domain:domain-user"))
    implementation(project(":domain:domain-auth"))
    implementation(project(":domain:domain-admin"))
    implementation(project(":domain:domain-official"))
    implementation(project(":domain:domain-crew"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("tools.jackson.core:jackson-databind")
    runtimeOnly("org.postgresql:postgresql")

    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")
}
