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
        project.repositories {
            jcenter()
        }

        applyPlugins(project)

        pluginVariables = project.extensions.create('weltn24JavaConventions', JavaConventionsPluginExtension)

        project.ext.gradleVersion = project.gradle.gradleVersion
        project.ext.javaVersion = org.gradle.internal.jvm.Jvm.current()

        project.afterEvaluate {
            project.dependencies {
                project.dependencies.add(JavaPlugin.TEST_COMPILE_CONFIGURATION_NAME, 'org.assertj:assertj-core:' + pluginVariables.assertjVersion)
            }
        }

        //Set parallel forks to testThreads or default number of threads
        project.test {
            doFirst {
                if(pluginVariables.runTestsParallel){
                    if(pluginVariables.testThreads > 0){
                        project.test.setMaxParallelForks pluginVariables.testThreads
                    }else{
                        project.test.setMaxParallelForks pluginVariables.defaultThreads
                    }
                }
            }
        }

        configureIdea(project)
        configureIntegrationTest(project)
        configureSmokeTest(project)

        project.tasks.withType(JavaCompile) {
            options.encoding = 'UTF-8'
            options.incremental = true
            options.compilerArgs << '-Xlint:unchecked'
        }

        project.tasks.withType(Test) {
            testLogging {
                events 'skipped', 'passed', 'failed'
                exceptionFormat 'full'
            }
        }
    }

    def applyPlugins(project) {
        project.apply(plugin: 'java')
        project.apply(plugin: 'idea')
        project.apply(plugin: 'eclipse')
        project.apply(plugin: 'jacoco')
    }

    def configureIdea(project) {
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

    def configureIntegrationTest(project) {
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

        // TODO bjuhasz: test this
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

            //Set parallel forks to default threadNumber or integrationTestThreads, if set
            doFirst {
                if(pluginVariables.runTestsParallel){
                    if(pluginVariables.integrationTestThreads > 0){
                        project.integrationTest.setMaxParallelForks pluginVariables.integrationTestThreads
                    }else{
                        project.integrationTest.setMaxParallelForks pluginVariables.defaultThreads
                    }
                }
            }

            // TODO bjuhasz: test this
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
    }

    def configureSmokeTest(project) {
        project.sourceSets {
            smokeTest {
                java.srcDir project.file('src/smokeTest/java')
                resources.srcDir project.file('src/smokeTest/resources')
            }
        }

        // TODO bjuhasz: test this
        project.afterEvaluate {
            project.dependencies {
                smokeTestCompile project.sourceSets.main.output
                smokeTestCompile project.configurations.testCompile
                smokeTestCompile project.sourceSets.test.output
                smokeTestRuntime project.configurations.testRuntime
            }
        }

        project.task('smokeTest',
            type: Test,
            dependsOn: project.jar,
            group: 'verification',
            description: 'Runs the smoke tests.') {

            //Set parallel forks to default threadNumber or smokeTestThreads, if set
            doFirst {
                if(pluginVariables.runTestsParallel){
                    if(pluginVariables.smokeTestThreads > 0){
                        project.smokeTest.setMaxParallelForks pluginVariables.smokeTestThreads
                    }else{
                        project.smokeTest.setMaxParallelForks pluginVariables.defaultThreads
                    }
                }
            }

            // TODO bjuhasz: test this
            testClassesDir = project.sourceSets.smokeTest.output.classesDir
            classpath = project.sourceSets.smokeTest.runtimeClasspath
            systemProperties['jar.path'] = project.jar.archivePath
        }

        project.idea.module {
            testSourceDirs += project.file('src/smokeTest/java')
            scopes.TEST.plus += [project.configurations.smokeTestCompile]
            scopes.TEST.plus += [project.configurations.smokeTestRuntime]
        }
    }

}
