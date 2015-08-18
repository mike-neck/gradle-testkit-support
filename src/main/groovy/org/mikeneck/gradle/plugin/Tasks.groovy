/*
 * Copyright 2015 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mikeneck.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskOutputs
import org.gradle.model.ModelMap
import org.mikeneck.gradle.plugin.data.Either
import org.mikeneck.gradle.plugin.model.TestKitSupport

import java.util.function.Function

import static org.mikeneck.gradle.plugin.util.ValidationHandlingOnTaskDefinition.getSupportFileDestDir
import static org.mikeneck.gradle.plugin.util.ValidationHandlingOnTaskDefinition.serializeOrThrow
import static org.mikeneck.gradle.plugin.util.ValidationHandlingOnTaskDefinition.toClassName
import static org.mikeneck.gradle.plugin.util.ValidationHandlingOnTaskDefinition.toOutputJavaFile

enum Tasks {
    PREPARE_TASK('storeModelForTestKitSupport'){
        @Override
        void createTask(ModelMap<Task> tasks, Either<TestKitSupport> validation) {
            if (validation.success) {
                tasks.create(getTaskName()) {
                    // variable definition
                    // use project, dest dir, output file, runtime dependencies
                    Project pj = project
                    Gradle gr = pj.gradle
                    def destDir = pj.file("${pj.buildDir}/${TestKitSupportPluginImpl.NAME}")
                    def outputFile = pj.file("${destDir}/${TestKitSupportPluginImpl.YAML}")
                    TaskOutputs out = outputs
                    def rt = pj.configurations.runtime.findAll {
                        !"${it}".contains("${gr.gradleHomeDir}")
                    }
                    def tmpJavaFile = destDir.toPath().resolve("${toClassName(validation)}.java").toFile()

                    // task configuration
                    description = 'Stores TestKitSupport model'

                    // task output definition
                    // output file is the final work of this task
                    // UP-TO-DATE condition is listed
                    //   * output file exists
                    //   * validation results in success
                    //   * deserialized object from yaml equals to model object
                    out.files outputFile, tmpJavaFile
                    out.upToDateWhen {
                        if (!outputFile.exists()) {
                            return false
                        } else if (!validation.success) {
                            return false
                        } else {
                            return serializeOrThrow(validation) == outputFile.text
                        }
                    }

                    // task work definition
                    doLast {
                        // if destDir doesn't exist, create it.
                        if (!destDir.exists()) {
                            destDir.mkdirs()
                        }
                        // validation results in fail throws exception
                        // create yaml file from model object
                        outputFile.write(serializeOrThrow(validation), 'UTF-8')

                        // create temporary TestSupport java file
                        def outputJavaFile = toOutputJavaFile(validation, rt)
                        def javaFilePath = destDir.toPath().resolve(outputJavaFile.fileName).toFile()

                        def contents = outputJavaFile.contents
                        println contents
                        javaFilePath.write(contents, 'UTF-8')
                    }
                }
            }
        }
    },
    MAIN_TASK('generateTestKitSupport'){
        @Override
        void createTask(ModelMap<Task> tasks, Either<TestKitSupport> validation) {
            if (validation.success) {
                tasks.create(getTaskName(), Copy) {
                    // valirable definition
                    // use project, use outputs of PREPARE_TASK
                    Project pj = project
                    def pjDir = pj.projectDir.toPath()
                    def prepareFiles = pj.tasks.getAt(PREPARE_TASK.taskName).outputs.files

                    // task configuration
                    group = 'Test prepare'
                    description = 'Generates supporting class for Gradle TestKit'
                    dependsOn PREPARE_TASK.taskName
                    enabled = validation.success

                    // copy definition from tmpJavaFile into user configuring directory
                    from (prepareFiles) {
                        exclude TestKitSupportPluginImpl.YAML
                    }
                    into(pjDir.resolve(getSupportFileDestDir(validation, pj)).toFile())
                }
            }
        }
    }

    final String taskName

    Tasks(String taskName) {
        this.taskName = taskName
    }

    String getTaskName() {
        taskName
    }

    static void throwIfError(Either<TestKitSupport> validation) {
        if (!validation.success) {
            validation.orThrow([apply:{String msg ->
                new IllegalArgumentException(msg)
            }] as Function)
        }
    }

    abstract void createTask(ModelMap<Task> tasks, Either<TestKitSupport> validation)
}