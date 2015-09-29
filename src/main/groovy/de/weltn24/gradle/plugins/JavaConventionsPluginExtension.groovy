package de.weltn24.gradle.plugins

class JavaConventionsPluginExtension {
    boolean runTestsParallel = false;
    int testThreads = (int) (Runtime.runtime.availableProcessors()/2);
    String assertjVersion = "3.2.0";
    String jacocoVersion = "0.7.5.201505241946";
}
