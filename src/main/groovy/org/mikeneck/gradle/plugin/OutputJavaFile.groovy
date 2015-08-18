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
package org.mikeneck.gradle.plugin

import org.mikeneck.gradle.plugin.model.TestKitSupport

final class OutputJavaFile {

    static final List<String> imports = [
            'org.gradle.testkit.runner.GradleRunner',
            'org.junit.rules.TemporaryFolder',
            'org.junit.rules.ExternalResource',
            'org.junit.runner.Description',
            'org.junit.runners.model.Statement',
            'java.io.File',
            'java.io.IOException',
            'java.net.MalformedURLException',
            'java.net.URISyntaxException',
            'java.net.URL',
            'java.nio.file.Files',
            'java.nio.file.Path',
            'java.nio.file.Paths'
    ]

    static final String DQ = '"'

    private final TestKitSupport conf

    private final Collection<File> files

    OutputJavaFile(TestKitSupport conf, Collection<File> files) {
        this.conf = conf
        this.files = files
    }

    String getFileName() {
        "${conf.className}.java"
    }

    String getPackageName() {
        conf.packageName
    }

    String getTestSrcDir() {
        conf.testSrcDir
    }

    String getPackageDir() {
        conf.packageName.replace('.', '/')
    }

    String getContents() {
        """|package ${conf.packageName};
           |
           |${imports.collect{"import ${it};"}.join('\n')}
           |
           |public class ${conf.className} extends ExternalResource {
           |
           |    public static final String BUILD_GRADLE = "build.gradle";
           |
           |    private static final char NEW_LINE = '\\n';
           |
           |    private static final String TAB = "    ";
           |
           |    private final TemporaryFolder delegate;
           |
           |    private final URL url;
           |
           |    private File buildFile;
           |
           |    public static ${conf.className} target(Class<?> klass) throws IOException, URISyntaxException {
           |        if (klass == null) throw new IllegalArgumentException("Target class is required.");
           |        return new ${conf.className}(klass);
           |    }
           |
           |    private ${conf.className}(Class<?> klass) throws IOException, URISyntaxException {
           |        this.url = klass.getProtectionDomain().getCodeSource().getLocation();
           |        this.delegate = new TemporaryFolder();
           |    }
           |
           |    public void buildGradle(String script) throws URISyntaxException, IOException {
           |        StringBuilder sb = new StringBuilder();
           |        boolean loadedByClassLoader = loadContext(sb);
           |        loadDependency(sb, loadedByClassLoader);
           |        sb.append(script);
           |        Files.write(buildFile.toPath(), sb.toString().getBytes("UTF-8"));
           |    }
           |
           |    private boolean loadContext(StringBuilder sb) throws URISyntaxException, MalformedURLException {
           |        if (url.getProtocol().equals("file") && url.toExternalForm().endsWith(".jar")) {
           |            return false;
           |        } else {
           |            sb.append("project.class.classLoader.addURLs([")
           |                    .append("new URL('").append(url).append("')");
           |            Path path = Paths.get(url.toURI());
           |            Path bld = path.getParent().getParent();
           |            if(bld.endsWith("build")) {
           |                sb.append(",")
           |                        .append("new URL('").append(bld.resolve("resources/main").toUri().toURL()).append("')");
           |            }
           |            return true;
           |        }
           |    }
           |
           |    private void loadDependency(StringBuilder sb, boolean loadedByClassLoader) throws URISyntaxException {
           |        sb.append("buildscript{").append(NEW_LINE)
           |                .append(TAB).append("dependencies {").append(NEW_LINE)
           |${files.collect {"                .append(TAB).append(TAB).append(${DQ}classpath file('${it}')${DQ}).append(NEW_LINE)"}.join('\n')};
           |        if (!loadedByClassLoader) {
           |            sb.append(TAB).append(TAB).append("classpath file('").append(Paths.get(url.toURI())).append("')");
           |        }
           |        sb.append(TAB).append('}').append(NEW_LINE)
           |                .append('}').append(NEW_LINE);
           |    }
           |
           |    public void ready() throws Throwable {
           |        before();
           |    }
           |
           |    public void end() {
           |        after();
           |    }
           |
           |    @Override
           |    protected void before() throws Throwable {
           |        delegate.create();
           |        buildFile = delegate.newFile(BUILD_GRADLE);
           |    }
           |
           |    @Override
           |    protected void after() {
           |        delegate.delete();
           |    }
           |
           |    public GradleRunner run(String... args) {
           |        if (args == null || args.length == 0) throw new IllegalArgumentException("empty arguments");
           |        return GradleRunner.create()
           |                .withArguments(args)
           |                .withProjectDir(getRoot());
           |    }
           |
           |    public File getRoot() {
           |        return delegate.getRoot();
           |    }
           |
           |    public File newFile(String fileName) throws IOException {
           |        return delegate.newFile(fileName);
           |    }
           |
           |    public File newFile() throws IOException {
           |        return delegate.newFile();
           |    }
           |
           |    public File newFolder(String folder) throws IOException {
           |        return delegate.newFolder(folder);
           |    }
           |
           |    public File newFolder(String... folderNames) throws IOException {
           |        return delegate.newFolder(folderNames);
           |    }
           |
           |    public File newFolder() throws IOException {
           |        return delegate.newFolder();
           |    }
           |
           |    @Override
           |    public Statement apply(Statement base, Description desc) {
           |        return delegate.apply(base, desc);
           |    }
           |}
           |""".stripMargin()
    }
}
