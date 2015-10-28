package de.weltn24.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.gradle.plugins.ide.idea.model.IdeaProject

class JavaConventionsPlugin implements Plugin<Project> {

    JavaConventionsPluginExtension pluginVariables;

    void apply(Project project) {
        pluginVariables = project.extensions.create('weltn24JavaConventions', JavaConventionsPluginExtension)

        projectConfiguration(project)
        jacocoConfiguration(project)
        javaConfiguration(project)
        testConfiguration(project)
        ideaConfiguration(project)
        integrationTestConfiguration(project)
        componentTestConfiguration(project)
        smokeTestConfiguration(project)
    }


    def projectConfiguration(project) {
        project.apply(plugin: 'java')
        project.apply(plugin: 'idea')
        project.apply(plugin: 'eclipse')
        project.apply(plugin: 'jacoco')

        project.repositories {
            jcenter()
        }

        project.ext.gradleVersion = project.gradle.gradleVersion
        project.ext.javaVersion = org.gradle.internal.jvm.Jvm.current()

    }

    def jacocoConfiguration(project) {
        project.jacoco {
            toolVersion = pluginVariables.jacocoVersion
        }
    }

    def javaConfiguration(project) {
        project.tasks.withType(JavaCompile) {
            options.encoding = 'UTF-8'
            options.incremental = true
            options.compilerArgs << '-Xlint:unchecked'
        }
    }

    def testConfiguration(project) {
        project.afterEvaluate {
            project.dependencies {
                project.dependencies.add(JavaPlugin.TEST_COMPILE_CONFIGURATION_NAME, 'org.assertj:assertj-core:' + pluginVariables.assertjVersion)
            }
        }

        project.test {
            doFirst {
                if (pluginVariables.runTestsParallel) {
                    project.test.setMaxParallelForks pluginVariables.testThreads
                }
            }
        }

        project.tasks.withType(Test) {
            testLogging {
                events 'skipped', 'passed', 'failed'
                exceptionFormat 'full'
            }
        }
    }


    def ideaConfiguration(project) {
        IdeaModel idea = project.idea
        IdeaProject ideaProject = idea.getProject()

        if (ideaProject != null) {
            ideaProject.setJdkName('1.8')
            ideaProject.setLanguageLevel('1.8')
            //for gradle version <2.3 (vcs undefined)
            if(ideaProject.hasProperty("vcs")){
                ideaProject.setVcs('Git')
            }
        }

        project.idea.module {
            downloadJavadoc = true
            downloadSources = true
        }
    }

    def integrationTestConfiguration(project) {
        project.sourceSets {
            integrationTest {
                java.srcDir project.file('src/integrationTest/java')
                resources.srcDir project.file('src/integrationTest/resources')
            }

            // IntelliJ doesn't copy the resources from src/intTest into build/intTest,
            // thus those resources are not on the classpath and are inaccessible:
            // https://youtrack.jetbrains.com/issue/IDEA-128966
            // http://zerodivisible.io/blog/2014/01/gradle-adding-new-sourcesets-while-using-intellij-idea/
            // http://stackoverflow.com/questions/27755843/how-to-add-test-resources-root-in-gradle-intellij
            // TODO: remove this hotfix when the bug-ticket is closed:
            if (System.properties.'idea.active') {
                test {
                    resources.srcDir project.file('src/integrationTest/resources')
                }
            }
        }

        project.afterEvaluate {
            project.dependencies {
                integrationTestCompile project.sourceSets.main.output
                integrationTestCompile project.configurations.testCompile
                integrationTestCompile project.sourceSets.test.output
                integrationTestRuntime project.configurations.testRuntime
            }
        }

        project.task('integrationTest',
            type: Test,
            dependsOn: project.jar,
            group: 'verification',
            description: 'Runs the integration tests.') {

            testClassesDir = project.sourceSets.integrationTest.output.classesDir
            classpath = project.sourceSets.integrationTest.runtimeClasspath
            systemProperties['jar.path'] = project.jar.archivePath
        }

        project.idea.module {
            testSourceDirs += project.file('src/integrationTest/java')
            scopes.TEST.plus += [project.configurations.integrationTestCompile]
            scopes.TEST.plus += [project.configurations.integrationTestRuntime]
        }

        project.tasks.findByName('check').dependsOn('integrationTest')
        project.tasks.findByName('integrationTest').mustRunAfter('test')
    }

    def componentTestConfiguration(project) {
        project.sourceSets {
            componentTest {
                java.srcDir project.file('src/componentTest/java')
                resources.srcDir project.file('src/componentTest/resources')
            }

            // IntelliJ doesn't copy the resources from src/intTest into build/intTest,
            // thus those resources are not on the classpath and are inaccessible:
            // https://youtrack.jetbrains.com/issue/IDEA-128966
            // http://zerodivisible.io/blog/2014/01/gradle-adding-new-sourcesets-while-using-intellij-idea/
            // http://stackoverflow.com/questions/27755843/how-to-add-test-resources-root-in-gradle-intellij
            // TODO: remove this hotfix when the bug-ticket is closed:
            if (System.properties.'idea.active') {
                test {
                    resources.srcDir project.file('src/componentTest/resources')
                }
            }
        }


        project.afterEvaluate {
            project.dependencies {
                componentTestCompile project.sourceSets.main.output
                componentTestCompile project.configurations.testCompile
                componentTestCompile project.sourceSets.test.output
                componentTestRuntime project.configurations.testRuntime
            }
        }

        project.task('componentTest',
            type: Test,
            dependsOn: project.jar,
            group: 'verification',
            description: 'Runs the component tests.') {

            testClassesDir = project.sourceSets.componentTest.output.classesDir
            classpath = project.sourceSets.componentTest.runtimeClasspath
            systemProperties['jar.path'] = project.jar.archivePath
        }

        project.idea.module {
            testSourceDirs += project.file('src/componentTest/java')
            scopes.TEST.plus += [project.configurations.componentTestCompile]
            scopes.TEST.plus += [project.configurations.componentTestRuntime]
        }

        project.tasks.findByName('check').dependsOn('componentTest')
    }

    def smokeTestConfiguration(project) {
        project.sourceSets {
            smokeTest {
                java.srcDir project.file('src/smokeTest/java')
                resources.srcDir project.file('src/smokeTest/resources')
            }
        }

        project.task('smokeTest',
            type: Test,
            group: 'verification',
            description: 'Runs the smoke tests against $SMOKETEST_SUT (must be set)') {

            testClassesDir = project.sourceSets.smokeTest.output.classesDir
            classpath = project.sourceSets.smokeTest.runtimeClasspath
        }

        project.smokeTest.onlyIf { System.getenv('SMOKETEST_SUT') }

        project.idea.module {
            testSourceDirs += project.file('src/smokeTest/java')
            scopes.TEST.plus += [project.configurations.smokeTestCompile]
            scopes.TEST.plus += [project.configurations.smokeTestRuntime]
        }
    }

}
