Java-Conventions Gradle Plugin
========================================

### This Plugin adds the following features to your Project:

- gradle plugins
    - java
    - idea
    
- Dependencies
    - adds [AssertJ](http://joel-costigliola.github.io/assertj/assertj-core-quick-start.html) in test compile scope 
    
- Configuration
    All configurations are optional
    
    Example:
    
        project.weltn24JavaConventions {
            runTestsParallel = true
        }
    
    | Type | Name | Default Value | Description |
    | ---- | ---- | ------------- | ----------- |
    |bool| runTestsParallel| false| On/Off switch for parallel testing |
    |int| defaultThreads | Runtime.runtime.availableProcessors()/2 | Default number of workers for test runs |
    |int| testThreads | current defaultThreads | Overrides the default number of workers for unit tests |
    |int| integrationTestThreads| current defaultThreads| Overrides the default number of workers for integration tests |
    |int| smokeTestThreads| current defaultThreads| Overrides the default number of workers for smoke tests |
    |String| assertjVersion| "3.0.0"| sets a specified version of assertj in your project |
    
- Tasks
    - integrationTest
    - smokeTest
    
- Others
    - UTF-8 encoding for JavaCompile Tasks
    - Intellij JavaVersion = 1.8
    
    
###Copyright (c) 2015 WeltN24 GmbH

Released under the [MIT license](https://tldrlegal.com/license/mit-license).
