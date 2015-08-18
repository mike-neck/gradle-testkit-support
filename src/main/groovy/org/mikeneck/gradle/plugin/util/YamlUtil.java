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
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Objects;

public final class YamlUtil {

    private YamlUtil() {}

    @Contract("null -> fail")
    public static DeserializedObject load(File file) throws NullPointerException, IOException {
        File y = Objects.requireNonNull(file);
        Yaml yaml = new Yaml();
        try (Reader r = new FileReader(y)) {
            Object object = yaml.load(r);
            return new DeserializedObject(object);
        }
    }

    @Contract("null -> fail")
    public static SerializedObject convert(Object object) throws NullPointerException {
        Object o = Objects.requireNonNull(object);
        DumperOptions dumpOption = new DumperOptions();
        dumpOption.setDefaultFlowStyle(FlowStyle.BLOCK);

        Yaml yaml = new Yaml(dumpOption);
        return new SerializedObject(yaml.dump(o));
    }

    public static final class DeserializedObject {

        private final Object object;

        private DeserializedObject(Object object) {
            this.object = object;
        }

        @Contract("null -> fail")
        @SuppressWarnings("unchecked")
        public <T> T as(Class<T> klass) throws ClassCastException, NullPointerException {
            Object o = Objects.requireNonNull(object);
            Class<T> c = Objects.requireNonNull(klass);
            return c.cast(o);
        }
    }

    public static final class SerializedObject {

        private final String dump;

        private SerializedObject(String dump) {
            this.dump = dump;
        }

        @Contract(pure = true)
        public String toText() {
            return dump;
        }

        @Contract("null -> fail")
        public void storeTo(File file) throws IOException, NullPointerException {
            try (Writer w = new FileWriter(Objects.requireNonNull(file))) {
                w.append(dump)
                        .flush();
            }
        }
    }
}
