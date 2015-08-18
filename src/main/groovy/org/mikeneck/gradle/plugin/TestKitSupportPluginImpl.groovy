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

import org.gradle.api.Task
import org.gradle.model.Model
import org.gradle.model.ModelMap
import org.gradle.model.Mutate
import org.gradle.model.RuleSource
import org.mikeneck.gradle.plugin.model.TestKitSupport
import org.mikeneck.gradle.plugin.util.ModelValidation

class TestKitSupportPluginImpl extends RuleSource {

    public static final String NAME = 'gradle-testkit-support'

    public static final String YAML = 'model.yaml'

    public static final String DEFAULT_TEST_SRC_DIR = 'src/test/java'

    public static final String DEFAULT_CLASS_NAME = 'TemporaryProject'

    @Model
    static void testKitSupport(TestKitSupport support) {
        support.testSrcDir = DEFAULT_TEST_SRC_DIR
        support.className = DEFAULT_CLASS_NAME
    }

    @Mutate
    static void createTask(ModelMap<Task> tasks, TestKitSupport conf) {
        def validated = ModelValidation.validate(conf)
        Tasks.values().each {
            it.createTask(tasks, validated)
        }
    }
}
