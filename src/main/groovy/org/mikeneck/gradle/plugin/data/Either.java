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
package org.mikeneck.gradle.plugin.data;

import org.jetbrains.annotations.Contract;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public interface Either<T> extends Result {

    @Contract("null -> !null; !null -> !null")
    static <T> Either<T> either(T value) {
        if (value == null) {
            return new Left<>("default value is null");
        } else {
            return new Right<>(value);
        }
    }

    <R> Either<R> flatMap(Function<? super T, ? extends Either<R>> func);

    <R> Either<R> map(Function<? super T, ? extends R> func);

    default Either<T> filter(final Predicate<? super T> predicate) {
        if (predicate == null) throw new IllegalArgumentException("function should be non-null value.");
        final UnaryOperator<T> func = t -> {
            if (predicate.test(t)) {
                return t;
            } else {
                return null;
            }
        };
        return map(func);
    }

    default Either<T> filter(final String checkCondition, final Predicate<? super T> predicate) {
        if (predicate == null) throw new IllegalArgumentException("function should be non-null value.");
        final Function<T, Either<T>> func = t -> {
            if (predicate.test(t)) {
                return new Right<>(t);
            } else {
                return new Left<>(checkCondition);
            }
        };
        return flatMap(func);
    }

    T orElse(T defaultValue);

    T orThrow(Function<? super String, ? extends RuntimeException> converter) throws RuntimeException;

    final class Left<T> implements Either<T> {
        private final String message;
        Left(String message) {
            this.message = message;
        }

        @Contract("null -> fail")
        @Override
        public <R> Either<R> flatMap(Function<? super T, ? extends Either<R>> func) {
            if (func == null) throw new IllegalArgumentException("function should be non-null value.");
            return new Left<>(message);
        }

        @Contract("null -> fail")
        @Override
        public <R> Either<R> map(Function<? super T, ? extends R> func) {
            if (func == null) throw new IllegalArgumentException("function should be non-null value.");
            return new Left<>(message);
        }

        @Contract("null -> fail")
        @Override
        public T orElse(T defaultValue) {
            if (defaultValue == null) throw new IllegalArgumentException("default value should be non-null value.");
            return defaultValue;
        }

        @Override
        public T orThrow(Function<? super String, ? extends RuntimeException> converter) {
            if (converter == null) throw new IllegalArgumentException("function should be non-null value.");
            throw converter.apply(message);
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Contract(pure = true)
        @Override
        public String toString() {
            return "Left [cause :[" + message + "]]";
        }
    }

    final class Right<T> implements Either<T> {
        private final T value;
        Right(T value) {
            this.value = value;
        }

        @Contract("null -> fail")
        @Override
        public <R> Either<R> flatMap(Function<? super T, ? extends Either<R>> func) {
            if (func == null) throw new IllegalArgumentException("function should be non-null value.");
            return func.apply(value);
        }

        @Contract("null -> fail")
        @Override
        public <R> Either<R> map(Function<? super T, ? extends R> func) {
            if (func == null) throw new IllegalArgumentException("function should be non-null value.");
            R result = func.apply(value);
            return result == null ? new Left<>(value + " is rejected.") : new Right<>(result);
        }

        @Contract("null -> fail")
        @Override
        public T orElse(T defaultValue) {
            if (defaultValue == null) throw new IllegalArgumentException("default value should be non-null value.");
            return value;
        }

        @Override
        public T orThrow(Function<? super String, ? extends RuntimeException> converter) throws RuntimeException {
            if (converter == null) throw new IllegalArgumentException("function should be non-null value.");
            return value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public String toString() {
            return "Right [" + value.toString() + "]";
        }
    }
}
