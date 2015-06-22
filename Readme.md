Gradle Java Conventions Plugin
========================================

![Build Status](https://snap-ci.com/WeltN24/gradle-java-conventions-plugin/branch/master/build_image)

## Usage

Build script snippet for use in all Gradle versions:

    buildscript {
      repositories {
        maven {
          url "https://plugins.gradle.org/m2/"
        }
      }
      dependencies {
        classpath "gradle.plugin.de.weltn24:java-conventions:1.0.15"
      }
    }
    
    apply plugin: "de.weltn24.java-conventions"
    
Build script snippet for new, incubating, plugin mechanism introduced in Gradle 2.1:

    plugins {
      id "de.weltn24.java-conventions" version "1.0.15"
    }


## This Plugin adds the following features to your Project:

### gradle plugins
- java
- idea
    
### Dependencies
- adds [AssertJ](http://joel-costigliola.github.io/assertj/assertj-core-quick-start.html) in test compile scope 
    
### Configuration
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

### Tasks
- integrationTest
- smokeTest
    
### Others
- UTF-8 encoding for JavaCompile Tasks
- Intellij JavaVersion = 1.8

## Publishing

Publishing is automatically done by snap-ci after a commit with increased version.
    
## Copyright (c) 2015 WeltN24 GmbH

Released under the [MIT license](https://tldrlegal.com/license/mit-license).
