package de.weltn24.gradle.plugins
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet
import org.gradle.testfixtures.ProjectBuilder
import org.junit.BeforeClass
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.junit.Assert.assertTrue

class JavaConventionsPluginTest {

    static Project project

    @BeforeClass
    public static void setUp(){
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'de.weltn24.java-conventions'
    }

    @Test
    public void javaConventionsPluginAddsIntegrationTestTaskToProject() {
        assertThat project.tasks.findByName('integrationTest') isInstanceOf Task.class
    }

    @Test
    public void ideaTestSourceDirsContainsIntegrationTest() {
        def Set<File> actualTestSourceDirs = project.idea.module.testSourceDirs
        def expectedIntegrationTestDir = project.file('src/integrationTest/java')

        assertThat actualTestSourceDirs contains expectedIntegrationTestDir
    }

    @Test
    public void projectSourceSetsContainIntegrationTestDirs() {
        def SourceSet actualSourceSet = project.sourceSets.find { it.name == 'integrationTest' }
        def expectedJavaDir = project.file('src/integrationTest/java')
        def expectedResourcesDir = project.file('src/integrationTest/resources')

        assertThat actualSourceSet.java.srcDirs contains expectedJavaDir
        assertThat actualSourceSet.resources.srcDirs contains expectedResourcesDir
    }

    @Test
    public void javaConventionsPluginAddsGradlePluginsToProject() {
        println 'javaConventionsPluginAddsGradlePluginsToProject'
        assertTrue(project.plugins.hasPlugin('java'))
        assertTrue(project.plugins.hasPlugin('idea'))
        assertTrue(project.plugins.hasPlugin('eclipse'))
        assertTrue(project.plugins.hasPlugin('jacoco'))
    }

    @Test
    public void taskCheckDependsOnIntegrationTest() {
        assertThat project.tasks.findByName('check').dependsOn contains 'integrationTest'
    }
}
