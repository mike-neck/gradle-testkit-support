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
package org.mikeneck.gradle.plugin.util

import org.mikeneck.gradle.plugin.model.TestKitSupport
import org.mikeneck.gradle.plugin.model.TestKitSupportImpl
import org.mikeneck.gradle.plugin.test.support.Validity
import spock.lang.Specification
import spock.lang.Unroll

import static org.mikeneck.gradle.plugin.model.TestKitSupportDataSupplier.*
import static org.mikeneck.gradle.plugin.model.TestKitSupportFields.*
import static org.mikeneck.gradle.plugin.util.ModelValidation.validate

class ModelValidationSpec extends Specification {

    @Unroll
    def 'testSrcDir[#testSrcDir], packageName[#packageName], className[#className] -> #validity'() {
        when:
        TestKitSupport conf = new TestKitSupportImpl(
                testSrcDir: TEST_SRC_DIR.dataOf(testSrcDir),
                packageName: PACKAGE_NAME.dataOf(packageName),
                className: CLASS_NAME.dataOf(className))

        then:
        validate(conf).success == validity.asBoolean

        where:
        testSrcDir  | packageName   | className | validity
        NULL        | VALID         | VALID     | Validity.INVALID
        EMPTY       | VALID         | VALID     | Validity.INVALID
        INVALID     | VALID         | VALID     | Validity.INVALID
        VALID       | NULL          | VALID     | Validity.INVALID
        VALID       | EMPTY         | VALID     | Validity.INVALID
        VALID       | INVALID       | VALID     | Validity.INVALID
        VALID       | VALID         | NULL      | Validity.INVALID
        VALID       | VALID         | EMPTY     | Validity.INVALID
        VALID       | VALID         | INVALID   | Validity.INVALID
        VALID       | VALID         | VALID     | Validity.VALID
    }
}
