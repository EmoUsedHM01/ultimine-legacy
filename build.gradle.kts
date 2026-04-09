plugins {
    id("com.gtnewhorizons.retrofuturagradle") version "1.4.3"
}

group = "com.ftbultimine"
version = "1.1.0"

minecraft {
    mcVersion.set("1.7.10")
    username.set("Developer")
    extraRunJvmArguments.addAll("-ea:${project.group}")
}

tasks.processResources {
    val props = mapOf("version" to project.version, "mcversion" to "1.7.10")
    inputs.properties(props)
    filesMatching("mcmod.info") {
        expand(props)
    }
}

tasks.injectTags {
    outputClassName.set("com.ftbultimine.Tags")
    tags.put("VERSION", project.version)
    tags.put("MOD_ID", "ftbultimine")
    tags.put("MOD_NAME", "FTB Ultimine")
}

tasks.compileJava {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}
