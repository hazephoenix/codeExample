import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
    implementation("org.slf4j:slf4j-api:1.7.28")
    implementation("org.apache.xmlgraphics:batik-dom:1.12")
    implementation("org.apache.xmlgraphics:batik-svggen:1.12")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-Xuse-experimental=kotlin.ExperimentalUnsignedTypes")
}