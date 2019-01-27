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

import Configurations.androidGraldePluginVersion
import Configurations.kotlinLanguageVersion
import Configurations.springBootFrameworkVersion

/**
 * DBShopping Kotlin libraries being used in the project.
 */
object KotlinLibraries {
    /**
     * Kotlin gradle plugin.
     */
    const val gradlePlugin: String = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinLanguageVersion"
    /*************************** Kotlin common libraries **************************************/
    const val stdlibCommon: String = "org.jetbrains.kotlin:kotlin-stdlib-common"
    const val testCommon: String = "org.jetbrains.kotlin:kotlin-test-common"
    const val testAnnotationCommon: String = "org.jetbrains.kotlin:kotlin-test-annotations-common"
    /*************************** Kotlin JVM Libraries *****************************************/
    const val testJvm: String = "'org.jetbrains.kotlin:kotlin-test'"
    const val testJunitJvm: String = "org.jetbrains.kotlin:kotlin-test-junit"
    const val stdlibJdk8: String = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
    const val stdlibJdk7: String = "org.jetbrains.kotlin:kotlin-stdlib-jdk7"
    const val reflection: String = "org.jetbrains.kotlin:kotlin-reflect"
    /*************************** Kotlin Javascript libraries *********************************/
    const val stdlibJs: String = "org.jetbrains.kotlin:kotlin-stdlib-js"
    const val testJs: String = "org.jetbrains.kotlin:kotlin-test-js"
}

/**
 * DBShopping google libraries being used in the project.
 */
object GoogleLibraries {
    /**
     * Android gradle plugin.
     */
    const val androidGradlePlugin: String = "com.android.tools.build:gradle:$androidGraldePluginVersion"
}

/**
 * DBShopping Third parties libraries being used in the project.
 */
object ThirdPartiesLibraries {
    const val rxJava2: String = "io.reactivex.rxjava2:rxjava:2.2.4"
}

/**
 * DBShopping spring dependencies being used in the project.
 */
object SpringDependencies {
    /**
     * Spring boot gradle plugin
     */
    const val springBootGradlePlugin: String =
        "org.springframework.boot:spring-boot-gradle-plugin:$springBootFrameworkVersion"

    /**
     * Spring boot starter library.
     */
    const val springBootStarter: String = "org.springframework.boot:spring-boot-starter"

    /**
     * Spring boot starter test library
     */
    const val springBootStarterTest: String = "org.springframework.boot:spring-boot-starter-test"
}
