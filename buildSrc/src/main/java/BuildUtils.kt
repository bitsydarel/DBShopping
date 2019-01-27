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

@file:JvmName("BuildUtils")

import org.gradle.api.Project
import java.util.*

/**
 * Extension function to retrieve the local properties inside of the root project directory.
 */
fun Project.getLocalProperties(): Properties {
    val localProps = Properties()
    localProps.load(rootProject.file("local.properties").inputStream())
    return localProps
}
