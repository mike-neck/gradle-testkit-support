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

import org.mikeneck.gradle.plugin.model.TestKitSupport;

import java.util.Objects;

public class TestKitSupportPojo implements TestKitSupport {

    private String testSrcDir;

    private String packageName;

    private String className;

    @Override
    public String getTestSrcDir() {
        return testSrcDir;
    }

    @Override
    public void setTestSrcDir(String testSrcDir) {
        this.testSrcDir = testSrcDir;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "TestKitSupport["
                + "testSrcDir:[" + testSrcDir + "],"
                + "packageName:[" + packageName + "],"
                + "className:[" + className + "]]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (!(o instanceof TestKitSupport)) return false;
        TestKitSupport that = (TestKitSupport) o;
        return Objects.equals(testSrcDir, that.getTestSrcDir()) &&
                Objects.equals(packageName, that.getPackageName()) &&
                Objects.equals(className, that.getClassName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(testSrcDir, packageName, className);
    }
}
