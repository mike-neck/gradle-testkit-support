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

import spock.lang.Specification
import spock.lang.Unroll

import static org.mikeneck.gradle.plugin.util.ModelValidationRule.*
import static org.mikeneck.gradle.plugin.test.support.Validity.*

class ModelValidationRuleSpec extends Specification {

    @Unroll
    def '#validator accept #object as #validity'() {
        expect:
        validator.validate(object) == validity.asBoolean

        where:
        validator | object | validity
        DIRECTORY_NAME  | 'src'             | VALID
        DIRECTORY_NAME  | '1a'              | INVALID
        DIRECTORY_NAME  | 'src/'            | VALID
        DIRECTORY_NAME  | '/src'            | VALID
        DIRECTORY_NAME  | 'src/main/java'   | VALID
        DIRECTORY_NAME  | '/src/main/java/' | VALID
        DIRECTORY_NAME  | 'src/main/java2'  | VALID
        DIRECTORY_NAME  | '/'               | INVALID

        PACKAGE_NAME    | 'org'             | VALID
        PACKAGE_NAME    | '1s'              | INVALID
        PACKAGE_NAME    | 'Abs'             | INVALID
        PACKAGE_NAME    | 'com.'            | INVALID
        PACKAGE_NAME    | 'com.github'      | VALID
        PACKAGE_NAME    | 'com.gitHub'      | INVALID
        PACKAGE_NAME    | 'com.3s'          | INVALID
        PACKAGE_NAME    | 'org.mike.test'   | VALID
        PACKAGE_NAME    | 'java.lang'       | INVALID

        CLASS_NAME      | 'S'               | VALID
        CLASS_NAME      | 'Test'            | VALID
        CLASS_NAME      | 's'               | INVALID
        CLASS_NAME      | 'camelClass'      | INVALID
        CLASS_NAME      | 'CamelClass'      | VALID
        CLASS_NAME      | 'Integer'         | INVALID
        CLASS_NAME      | 'String'          | INVALID
        CLASS_NAME      | 'Object'          | INVALID
    }
}
