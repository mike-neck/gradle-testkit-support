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
package org.mikeneck.gradle.plugin;

import org.junit.Before;
import org.junit.Test;
import org.mikeneck.gradle.plugin.data.TestKitSupportPojo;
import org.mikeneck.gradle.plugin.model.TestKitSupport;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.IOException;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class YamlTest {

    final String testSrcDir = "src/test/groovy";

    final String packageName = "org.mikeneck";

    final String className = "TestProject";

    TestKitSupport support;

    @Before
    public void setup() {
        support = new TestKitSupport() {
            @Override
            public String getTestSrcDir() {
                return testSrcDir;
            }
            @Override
            public void setTestSrcDir(String testSrcDir) {}
            @Override
            public String getPackageName() {
                return packageName;
            }
            @Override
            public void setPackageName(String packageName) {}
            @Override
            public String getClassName() {
                return className;
            }
            @Override
            public void setClassName(String className) {}
        };
    }

    @Test
    public void testSerialize() {
        DumperOptions dumpOption = new DumperOptions();
        dumpOption.setDefaultFlowStyle(FlowStyle.BLOCK);
        dumpOption.setExplicitRoot("!!" + TestKitSupportPojo.class.getCanonicalName());
        Yaml yaml = new Yaml(dumpOption);
        String dump = yaml.dump(support);
        System.out.println(dump);

        Yaml loader = new Yaml();
        TestKitSupport pojo = (TestKitSupport) loader.load(dump);
        assertThat(pojo, is(support));
    }

    @Test
    public void testDeserialize() throws IOException {
        StringWriter sw = new StringWriter();
        String text = sw.append("!!org.mikeneck.gradle.plugin.data.TestKitSupportPojo")
                .append('\n')
                .append("testSrcDir: ").append(testSrcDir)
                .append('\n')
                .append("className: ").append(className)
                .append('\n')
                .append("packageName: ").append(packageName)
                .append('\n')
                .toString();
        Yaml yaml = new Yaml();
        TestKitSupport loaded = (TestKitSupport) yaml.load(text);

        assertThat(loaded, is(support));
    }
}
