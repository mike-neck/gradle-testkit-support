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
package org.mikeneck.gradle.plugin.test.support;

import org.gradle.testkit.runner.GradleRunner;
import org.jetbrains.annotations.Contract;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.mikeneck.gradle.plugin.test.support.Functions.function;

public class TemporaryProject extends TemporaryFolder {

    private static final String DEPENDENCIES_FILE = "dependencies/dependencies.txt";

    public static final String BUILD_GRADLE = "build.gradle";

    private static final char NEW_LINE = '\n';

    private static final String TAB = "    ";

    private final ClassLoader loader = getClass().getClassLoader();

    private final TemporaryFolder delegate;

    private final Class<?> klass;

    private File buildFile;

    @Contract("null -> fail; !null -> !null")
    public static TemporaryProject testTargetClass(Class<?> klass) throws IOException {
        if (klass == null) throw new IllegalArgumentException("target class is not given.");
        return new TemporaryProject(klass);
    }

    private TemporaryProject(Class<?> klass) throws IOException {
        this.klass = klass;
        this.delegate = new TemporaryFolder();
    }

    public void buildGradle(String contents) throws URISyntaxException, IOException {
        StringBuilder sb = new StringBuilder();
        loadContext(sb);
        sb.append(contents);
        Files.write(buildFile.toPath(), sb.toString().getBytes("UTF-8"));
    }

    private void loadContext(StringBuilder sb) throws URISyntaxException, MalformedURLException {
        URL url = klass.getProtectionDomain().getCodeSource().getLocation();
        URI uri = url.toURI();
        Path path = Paths.get(uri);
        if (url.getProtocol().equals("file") && uri.toString().endsWith(".jar")) {
            sb.append("buildscript {").append(NEW_LINE)
                    .append(TAB).append("dependencies {").append(NEW_LINE)
                    .append(TAB).append(TAB).append("classpath file('").append(path.toString()).append("')").append(NEW_LINE);
            loadDependenciesFile(sb);
            sb.append(TAB).append('}').append(NEW_LINE)
                    .append('}').append(NEW_LINE);
        } else {
            sb.append("project.class.classLoader.addURLs([")
                    .append("new URL('").append(path.toUri().toURL()).append("')");
            Path bld = path.getParent().getParent();
            if (bld.endsWith("build")) {
                sb.append(",")
                        .append("new URL('").append(bld.resolve("resources/main").toUri().toURL()).append("')");
            }
            loadDependenciesUrl(sb);
            sb.append("])").append(NEW_LINE);
        }
    }

    private void loadDependenciesFile(StringBuilder sb) {
        try(Stream<String> str = loadDep()) {
            str.forEach(dep ->
                    sb.append(TAB).append(TAB).append("classpath file('").append(dep).append("')").append(NEW_LINE));
        }
    }

    private void loadDependenciesUrl(StringBuilder sb) {
        try(Stream<String> str = loadDep()) {
            str.map(File::new)
                    .map(File::toURI)
                    .map(function(URI::toURL))
                    .map(URL::toString)
                    .forEach(u ->
                            sb.append(',').append("new URL('").append(u).append("')"));
        }
    }

    private Stream<String> loadDep() {
        return new BufferedReader(new InputStreamReader(loader.getResourceAsStream(DEPENDENCIES_FILE))).lines();
    }

    public void ready() throws Throwable {
        before();
    }

    public void end() {
        after();
    }

    public GradleRunner run(String... args) {
        if (args == null || args.length == 0) throw new IllegalArgumentException("empty arguments");
        return GradleRunner.create()
                .withArguments(args)
                .withProjectDir(getRoot());
    }

    @Override
    protected void before() throws Throwable {
        create();
        buildFile = delegate.newFile(BUILD_GRADLE);
    }

    @Override
    protected void after() {
        delete();
    }

    @Override
    public void create() throws IOException {
        delegate.create();
    }

    @Override
    public File newFile(String fileName) throws IOException {
        return delegate.newFile(fileName);
    }

    @Override
    public File newFile() throws IOException {
        return delegate.newFile();
    }

    @Override
    public File newFolder(String folder) throws IOException {
        return delegate.newFolder(folder);
    }

    @Override
    public File newFolder(String... folderNames) throws IOException {
        return delegate.newFolder(folderNames);
    }

    @Override
    public File newFolder() throws IOException {
        return delegate.newFolder();
    }

    @Override
    public File getRoot() {
        return delegate.getRoot();
    }

    @Override
    public void delete() {
        delegate.delete();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return delegate.apply(base, description);
    }
}
