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

import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.mikeneck.gradle.plugin.util.ProhibitedPattern.*;

public enum ModelValidationRule {

    DIRECTORY_NAME("^/?[\\p{Lower}][a-z0-9]*(/[\\p{Lower}][a-z0-9]*)*/?$"),
    PACKAGE_NAME("^[\\p{Lower}][a-z0-9]*(\\.[\\p{Lower}][a-z0-9]*)*$",
            startsWith("java.")),
    CLASS_NAME("^[\\p{Upper}][\\p{Alnum}]*([\\p{Upper}][\\p{Alnum}])*",
            equalName(String.class), equalName(Object.class),
            equalName(Integer.class), equalName(Class.class),
            equalName(Long.class), equalName(Byte.class),
            equalName(Character.class), equalName(Boolean.class),
            equalName(CharSequence.class), equalName(Number.class),
            equalName(Appendable.class), equalName(AutoCloseable.class),
            equalName(AbstractMethodError.class), equalName(ArithmeticException.class),
            equalName(ArrayIndexOutOfBoundsException.class), equalName(ArrayStoreException.class),
            equalName(AssertionError.class), equalName(BootstrapMethodError.class),
            equalName(ClassCastException.class), equalName(ClassCircularityError.class),
            equalName(ClassFormatError.class), equalName(ClassLoader.class),
            equalName(ClassNotFoundException.class), equalName(ClassValue.class),
            equalName(Cloneable.class), equalName(CloneNotSupportedException.class),
            equalName(Comparable.class), equalName(Compiler.class),
            equalName(Deprecated.class), equalName(Enum.class),
            equalName(EnumConstantNotPresentException.class), equalName(Error.class),
            equalName(Exception.class), equalName(ExceptionInInitializerError.class),
            equalName(Float.class), equalName(FunctionalInterface.class),
            equalName(IllegalAccessError.class), equalName(IllegalAccessException.class),
            equalName(IllegalArgumentException.class), equalName(IllegalMonitorStateException.class),
            equalName(IllegalStateException.class), equalName(IllegalThreadStateException.class),
            equalName(IncompatibleClassChangeError.class), equalName(IndexOutOfBoundsException.class),
            equalName(InheritableThreadLocal.class), equalName(InstantiationError.class),
            equalName(InstantiationException.class), equalName(InternalError.class),
            equalName(Iterable.class), equalName(LinkageError.class),
            equalName(Math.class), equalName(NegativeArraySizeException.class),
            equalName(NoClassDefFoundError.class), equalName(NoSuchFieldError.class),
            equalName(NoSuchFieldException.class), equalName(NoSuchMethodError.class),
            equalName(NoSuchMethodException.class), equalName(NullPointerException.class),
            equalName(NumberFormatException.class), equalName(OutOfMemoryError.class),
            equalName(Override.class), equalName(Package.class),
            equalName(Process.class), equalName(ProcessBuilder.class),
            equalName(Readable.class), equalName(ReflectiveOperationException.class),
            equalName(Runnable.class), equalName(Runtime.class),
            equalName(RuntimeException.class), equalName(RuntimePermission.class),
            equalName(SafeVarargs.class), equalName(SecurityException.class),
            equalName(SecurityManager.class), equalName(Short.class),
            equalName(StackOverflowError.class), equalName(StackTraceElement.class),
            equalName(StrictMath.class), equalName(StringBuffer.class),
            equalName(StringBuilder.class), equalName(StringIndexOutOfBoundsException.class),
            equalName(SuppressWarnings.class), equalName(System.class),
            equalName(Thread.class), equalName(ThreadDeath.class),
            equalName(ThreadGroup.class), equalName(ThreadLocal.class),
            equalName(Throwable.class), equalName(TypeNotPresentException.class),
            equalName(UnknownError.class), equalName(UnsatisfiedLinkError.class),
            equalName(UnsupportedClassVersionError.class), equalName(UnsupportedOperationException.class),
            equalName(VerifyError.class), equalName(VirtualMachineError.class),
            equalName(Void.class));

    private final String pattern;

    private final Set<ProhibitedPattern> prohibited;

    ModelValidationRule(String pattern, ProhibitedPattern... prohibited) {
        this.pattern = pattern;
        this.prohibited = Stream.of(prohibited).collect(toSet());
    }

    @Contract(pure = true)
    public boolean validate(@NotNull String object) {
        return object.matches(pattern) && prohibited.stream().filter(p -> p.matches(object)).count() == 0;
    }
}
