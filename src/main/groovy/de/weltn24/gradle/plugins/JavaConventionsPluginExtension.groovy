package de.weltn24.gradle.plugins

class JavaConventionsPluginExtension {
    boolean runTestsParallel = false;
    int testThreads = (int) (Runtime.runtime.availableProcessors()/2);
    String jacocoVersion = "0.7.6.201602180812";
}
