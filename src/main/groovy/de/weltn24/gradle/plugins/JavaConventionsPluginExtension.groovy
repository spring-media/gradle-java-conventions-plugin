package de.weltn24.gradle.plugins

class JavaConventionsPluginExtension {
    boolean runTestsParallel = false;
    int testThreads = (int) (Runtime.runtime.availableProcessors()/2);
    String assertjVersion = "3.1.0";
}
