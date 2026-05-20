dependencies {
    implementation(project(":core"))

    // Add a domain dependency here only when that domain defines a capability port to implement.
    // Example: implementation(project(":domain:domain-auth"))   // when SmsSender is added
    //          implementation(project(":domain:domain-app"))    // when FileUploader is added
    //          implementation(project(":domain:domain-crew"))   // when EventPublisher is added
}
