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
package org.mikeneck.gradle.plugin.model;

import static org.mikeneck.gradle.plugin.model.TestKitSupportDataSupplier.*;

public enum TestKitSupportFields {

    TEST_SRC_DIR {
        @Override
        public String dataOf(TestKitSupportDataSupplier supplier) {
            if (supplier.equals(NULL) || supplier.equals(EMPTY)) {
                return supplier.data();
            } else if (supplier.equals(INVALID)) {
                return "Src/0test/groovy";
            } else {
                return "src/main/groovy";
            }
        }
    },
    PACKAGE_NAME {
        @Override
        public String dataOf(TestKitSupportDataSupplier supplier) {
            if (supplier.equals(NULL) || supplier.equals(EMPTY)) {
                return supplier.data();
            } else if (supplier.equals(INVALID)) {
                return "1sf.gef";
            } else {
                return "org.mikeneck";
            }
        }
    },
    CLASS_NAME {
        @Override
        public String dataOf(TestKitSupportDataSupplier supplier) {
            if (supplier.equals(NULL) || supplier.equals(EMPTY)) {
                return supplier.data();
            } else if (supplier.equals(INVALID)) {
                return "myType";
            } else {
                return "MyType";
            }
        }
    };

    abstract public String dataOf(TestKitSupportDataSupplier supplier);
}
