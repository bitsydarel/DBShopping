/*
 *
 *  * Copyright (C) 2019 Darel Bitsy
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License
 *
 */

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlin-multiplatform") {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }
            if (requested.id.id == "com.android.library") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
            if (requested.id.id == "com.android.application") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
        }
    }
}

enableFeaturePreview("GRADLE_METADATA")

rootProject.name = "DBShopping"

include(":app")
include(":domain-old")
include(":data")
include(":domain")
include("shoppinglists")

/**
 * List of flutter plugins to include in the main project.
 */
val flutterPlugins: List<String> = listOf(
        "shoppinglists"
)

for (project in rootProject.children) {
    val projectName = project.name
    if (projectName in flutterPlugins) {
        project.projectDir = file(projectName.plus("/android"))
        project.buildFileName = "build.gradle"
    }
    if (!project.projectDir.isDirectory) {
        throw IllegalArgumentException("Project directory ${project.projectDir} for project ${project.name} does not exist.")
    }
    if (!project.buildFile.isFile) {
        throw IllegalArgumentException("Build file ${project.buildFile} for project ${project.name} does not exist.")
    }
}

