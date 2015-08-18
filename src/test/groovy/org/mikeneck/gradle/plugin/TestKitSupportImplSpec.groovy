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

import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.mikeneck.gradle.plugin.test.support.TemporaryProject
import spock.lang.Specification

class TestKitSupportImplSpec extends Specification {

    @Rule
    TemporaryProject project = TemporaryProject.testTargetClass(TestKitSupportPluginImpl)

    final ClassLoader loader = getClass().classLoader

    def setup() {
        project.ready()
    }

    def cleanup() {
        project.end()
    }

    def 'task[model] shows model object correctly' () {
        given:
        def script = loader.getResource('build-scripts/only-model.gradle').text
        project.buildGradle(script)

        when:
        def result = project.run('model').build()

        then:
        result.task(':model').outcome == TaskOutcome.SUCCESS
        result.standardOutput.contains('+ className')
        result.standardOutput.contains('+ packageName')
        result.standardOutput.contains('+ testSrcDir')
    }

    def 'When model is valid, task[tasks] shows task[generateTestKitSupport]'() {
        given:
        def script = loader.getResource('build-scripts/test-valid.gradle').text
        project.buildGradle(script)

        when:
        def result = project.run('tasks').build()

        then:
        result.standardOutput.contains(Tasks.MAIN_TASK.taskName)
        println result.standardOutput
    }

    def 'When model is invalid, task[tasks] does not show task[generateTestKitSupport]'() {
        given:
        def script = loader.getResource('build-scripts/test-invalid.gradle').text
        project.buildGradle(script)

        when:
        def result = project
                .run('tasks')
                .build()

        then:
        result.task(':tasks').outcome == TaskOutcome.SUCCESS
        !result.standardOutput.contains(Tasks.MAIN_TASK.taskName)
    }

    def 'When model is valid, task[compileTestJava] will success'() {
        given:
        def script = loader.getResource('build-scripts/test-valid.gradle').text
        project.buildGradle(script)

        when:
        def result = project.run(Tasks.MAIN_TASK.taskName, 'compileTestJava').build()

        then:
        result.task(':compileTestJava').outcome == TaskOutcome.SUCCESS
    }
}
