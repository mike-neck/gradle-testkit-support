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
package org.mikeneck.gradle.plugin.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ProhibitedPattern {

    private final Predicate<String> validator;

    private ProhibitedPattern(String pattern, PatternConstraints constraints) {
        validator = constraints.validator(pattern);
    }

    @Contract("!null -> !null")
    public static ProhibitedPattern startsWith(@NotNull String pattern) {
        return new ProhibitedPattern(pattern, PatternConstraints.START_WITH);
    }

    @Contract("!null -> !null")
    public static ProhibitedPattern equalName(@NotNull Class<?> klass) {
        return new ProhibitedPattern(klass.getSimpleName(), PatternConstraints.EQUALS);
    }

    @Contract(pure = true)
    public boolean matches(@NotNull String object) {
        return validator.test(object);
    }

    private enum PatternConstraints {
        START_WITH {
            @Override
            @Contract("!null -> !null")
            Predicate<String> validator(@NotNull String p) {
                return o -> o.startsWith(p);
            }
        },
        EQUALS {
            @Override
            @Contract("!null -> !null")
            Predicate<String> validator(@NotNull String p) {
                return o -> o.equals(p);
            }
        };

        @Contract("!null -> !null")
        abstract Predicate<String> validator(@NotNull String p);
    }
}
