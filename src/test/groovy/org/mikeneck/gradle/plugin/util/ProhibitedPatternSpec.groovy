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

import static org.mikeneck.gradle.plugin.util.ProhibitedPattern.equalName
import static org.mikeneck.gradle.plugin.util.ProhibitedPattern.startsWith

class ProhibitedPatternSpec extends Specification {

    @Unroll
    def 'startsWith(#pattern) takes #object as prohibited -> #expected'() {
        when:
        def prohibited = startsWith(pattern)

        then:
        prohibited.matches(object) == expected

        where:
        pattern     | object        | expected
        'java.'     | 'java.util'   | true
        'java.'     | 'javax.inject'| false
    }

    @Unroll
    def 'equalName(#klass) takes #object as prohibited -> #expected'() {
        when:
        def prohibited = equalName(klass)

        then:
        prohibited.matches(object) == expected

        where:
        klass       | object    | expected
        Object      | 'Test'    | false
        Object      | 'Object'  | true
    }
}
