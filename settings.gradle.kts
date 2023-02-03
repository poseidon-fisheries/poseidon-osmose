rootProject.name = "poseidon-osmose"
include("app")

includeBuild("POSEIDON") {
    dependencySubstitution {
        substitute(module("uk.ac.ox.oxfish:POSEIDON")).using(project(":"))
    }
}
