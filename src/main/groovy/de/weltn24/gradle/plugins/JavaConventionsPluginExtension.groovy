package de.weltn24.gradle.plugins

class JavaConventionsPluginExtension {
    boolean runTestsParallel = false;
    int defaultThreads = (int) (Runtime.runtime.availableProcessors()/2);
    int testThreads = 0;
    int integrationTestThreads = 0;
    int smokeTestThreads = 0;

    String assertjVersion = "3.0.0";
}
