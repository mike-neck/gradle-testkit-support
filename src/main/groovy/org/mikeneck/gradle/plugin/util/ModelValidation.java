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
import org.mikeneck.gradle.plugin.data.Either;
import org.mikeneck.gradle.plugin.model.TestKitSupport;

import java.util.function.Predicate;

import static org.mikeneck.gradle.plugin.data.Either.either;
import static org.mikeneck.gradle.plugin.util.ModelValidationRule.CLASS_NAME;
import static org.mikeneck.gradle.plugin.util.ModelValidationRule.DIRECTORY_NAME;
import static org.mikeneck.gradle.plugin.util.ModelValidationRule.PACKAGE_NAME;

public final class ModelValidation {

    private ModelValidation() {}

    @Contract("null -> !null; !null -> !null")
    public static Either<TestKitSupport> validate(TestKitSupport conf) {
        return either(conf)
                .filter("Check testSrcDir not null.", SRC_DIR_NOT_NULL)
                .filter("Check testSrcDir not empty.", SRC_DIR_NOT_EMPTY)
                .filter("Check testSrcDir matching valid pattern.", SRC_DIR_MATCHES_PATTERN)
                .filter("Check packageName not null.", PACKAGE_NAME_NOT_NULL)
                .filter("Check packageName not empty.", PACKAGE_NAME_NOT_EMPTY)
                .filter("Check packageName matching valid pattern.", PACKAGE_NAME_MATCHES_PATTERN)
                .filter("Check className not null.", CLASS_NAME_NOT_NULL)
                .filter("Check className not empty.", CLASS_NAME_NOT_EMPTY)
                .filter("Check className matching valid pattern.", CLASS_NAME_MATCHES_PATTERN);
    }

    private static final Predicate<TestKitSupport> SRC_DIR_NOT_NULL = c -> c.getTestSrcDir() != null;

    private static final Predicate<TestKitSupport> SRC_DIR_NOT_EMPTY = c -> !c.getTestSrcDir().isEmpty();

    private static final Predicate<TestKitSupport> SRC_DIR_MATCHES_PATTERN = c -> DIRECTORY_NAME.validate(c.getTestSrcDir());

    private static final Predicate<TestKitSupport> PACKAGE_NAME_NOT_NULL = c -> c.getPackageName() != null;

    private static final Predicate<TestKitSupport> PACKAGE_NAME_NOT_EMPTY = c -> !c.getPackageName().isEmpty();

    private static final Predicate<TestKitSupport> PACKAGE_NAME_MATCHES_PATTERN = c -> PACKAGE_NAME.validate(c.getPackageName());

    private static final Predicate<TestKitSupport> CLASS_NAME_NOT_NULL = c -> c.getClassName() != null;

    private static final Predicate<TestKitSupport> CLASS_NAME_NOT_EMPTY = c -> !c.getClassName().isEmpty();

    private static final Predicate<TestKitSupport> CLASS_NAME_MATCHES_PATTERN = c -> CLASS_NAME.validate(c.getClassName());

}
