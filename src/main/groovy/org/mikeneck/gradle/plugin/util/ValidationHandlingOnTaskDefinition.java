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

import org.gradle.api.Project;
import org.mikeneck.gradle.plugin.InvalidConfigurationException;
import org.mikeneck.gradle.plugin.OutputJavaFile;
import org.mikeneck.gradle.plugin.TestKitSupportPluginImpl;
import org.mikeneck.gradle.plugin.data.Either;
import org.mikeneck.gradle.plugin.model.TestKitSupport;
import org.mikeneck.gradle.plugin.model.TestKitSupportPojo;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class ValidationHandlingOnTaskDefinition {

    private ValidationHandlingOnTaskDefinition() {}

    public static final UnaryOperator<TestKitSupport> TO_POJO = TestKitSupportPojo::new;

    public static final Function<TestKitSupport, String> TO_YAML = function(m -> YamlUtil.convert(m).toText());

    public static final Function<String, InvalidConfigurationException> WHEN_FAIL = InvalidConfigurationException::new;

    public static final Function<TestKitSupport, String> TO_SRC_DIR = TestKitSupport::getTestSrcDir;

    public static final Function<TestKitSupport, String> TO_CLASS_NAME = TestKitSupport::getClassName;

    public static final Function<TestKitSupport, String> TO_PACKAGE_NAME = TestKitSupport::getPackageName;

    public static String getSupportFileDestDir(Either<TestKitSupport> validation, Project pj) {
        return Paths.get(toTestSrcDir(validation)).resolve(toPackageName(validation, pj).replace('.', '/')).toString();
    }

    public static String toPackageName(Either<TestKitSupport> validation, Project pj) {
        return validation.map(TO_PACKAGE_NAME)
                .orElse(pj.getGroup().toString());
    }

    public static String toClassName(Either<TestKitSupport> validation) {
        return validation.map(TO_CLASS_NAME)
                .orElse(TestKitSupportPluginImpl.DEFAULT_CLASS_NAME);
    }

    public static String toTestSrcDir(Either<TestKitSupport> validation) {
        return validation.map(TO_SRC_DIR)
                .orElse(TestKitSupportPluginImpl.DEFAULT_TEST_SRC_DIR);
    }

    public static String serializeOrThrow(Either<TestKitSupport> validation) {
        return validation.map(TO_POJO)
                .map(TO_YAML)
                .orThrow(WHEN_FAIL);
    }

    public static OutputJavaFile toOutputJavaFile(Either<TestKitSupport> validation, final Collection<File> files) {
        return validation.map(m -> new OutputJavaFile(m, files))
                .orThrow(WHEN_FAIL);
    }

    @FunctionalInterface
    public interface FunctionWithException<I, O> {
        O apply(I input) throws Exception;
    }

    public static <I, O> Function<I, O> function(FunctionWithException<? super I, ? extends O> func) {
        return i -> {
            try {
                return func.apply(i);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
