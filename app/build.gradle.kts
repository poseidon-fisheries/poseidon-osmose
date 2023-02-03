plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.3.1")

    implementation("com.google.guava:guava:31.1-jre")
    implementation("com.esotericsoftware:minlog:1.3.0")
    implementation("org.yaml:snakeyaml:1.29")
    implementation("si.uom:si-quantity:2.1")

    //a jury-rigged version of OSMOSE, hopefully linkable with this simulation
    implementation(fileTree("libs/osmose") { include("*.jar") })

    implementation("uk.ac.ox.oxfish:POSEIDON")

}

tasks.withType<Test> {
    maxHeapSize = "4G"
}