gradle-testkit-support
===
Gradle plugin, supporting gradle testKit.

This plugin generates support class for gradle test kit, and saves your time from writing boiler plate codes.

Usage
===

Add the code snippet to your plugin build script.

```groovy
plugins {
    id 'org.mikeneck.gradle-testkit-support-plugin' version '0.1'
}
```

Give these items via `model{}` block.

* test source code directory
* package name

```groovy
model {
    testKitSupport {
        testSrcDir = 'src/test/groovy' // default is src/test/java
        packageName = 'com.sample.plugin' // mandatory
        className = 'TestProject' // default is TemporaryProject
    }
}
```

Run `generateTestKitSupport` task to generate support class.

```
$ gradle --daemon gTKS
```

Then the java code `TestProject` will be generated under the directory `src/test/groovy` and package `com.sample.plugin`.

The generated class will automatically load runtime dependencies for your plugin, and provide temporary directory for test.

Use generated class with your plugin test.

```groovy
class YourPluginSpec extends Specification {

    @Rule
    def TestProject testProject = TestProject.target(YourPluginImpl)

    def setup() {
        testProject.ready()
    }

    def cleanup() {
        testProject.end()
    }

    def 'given build script will success'() {
        given:
        def script = """|apply plugin: 'your-plugin-id'
                |model {
                |    yourPlugin {
                |        definition = 'test'
                |    }
                |}
                |""".stripMargin()
        testProject.buildGradle(script)

        when:
        def result = testProject.run('yourTaskName', '--stacktrace')
                .build()

        then:
        result.task(":yourTaskName").outcome = TaskOutcome.SUCCESS
    }
}
```

Requirement
===

* Gradle 2.6 above
* Java SE8

LICENSE
===

Apache2

For Contributors
===

### Project setup

#### run these tasks

1. `./gradlew --daemon idea`
1. `./gradlew --daemon generatePojos`
1. `./gradlew --daemon prepareTest`

#### modify generated POJO file

Because of `org.mikeneck.rule-based-model-generation` plugin's less functionality, a generated POJO `TestKitSupportPojo` has no constructor and no standard methods(`toString`, `hashCode`, `equals`), so please add them by hand.

```java
public class TestKitSupportPojo implements TestKitSupport {

    // add from
    public TestKitSupportPojo() {}

    public TestKitSupportPojo(TestKitSupport parent) {
        this.testSrcDir = parent.getTestSrcDir();
        this.packageName = parent.getPackageName();
        this.className = parent.getClassName();
    }

    public TestKitSupportPojo(String testSrcDir, String packageName, String className) {
        this.testSrcDir = testSrcDir;
        this.packageName = packageName;
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
    // add end

    // setter/getter/fields...
}
```
