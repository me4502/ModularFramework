ModularFramework [![Build Status](https://travis-ci.org/me4502/ModularFramework.svg?branch=master)](https://travis-ci.org/me4502/ModularFramework) [ ![Download](https://api.bintray.com/packages/me4502/maven/ModularFramework/images/download.svg) ](https://bintray.com/me4502/maven/ModularFramework/_latestVersion)
=============
A modular plugin framework built for Sponge.
## Features
* Lightweight and small module framework.
* Lazy-loading of classes, unused modules are not loaded.

## Prerequisites
* [Java] 8
* Sponge

## Building
__Note:__ If you do not have [Gradle] installed then use ./gradlew for Unix systems or Git Bash and gradlew.bat for Windows systems in place of any 'gradle' command.

In order to build ModularFramework you simply need to run the `gradle build` command. You can find the compiled JAR file in `./build/libs` labeled similarly to 'ModularFramework-x.x.x-SNAPSHOT.jar'.

## Getting ModularFramework
### Using Gradle or Maven
ModularFramework is available on the following maven repository, 
```
https://dl.bintray.com/me4502/maven
```

The following line will add the repository to gradle when placed in the repositories closure, 
```
maven { url 'https://dl.bintray.com/me4502/maven' }
```

The following line will add the dependency when placed in the dependencies closure, 
```
compile 'com.me4502:ModularFramework:VERSION'
```

### Direct Download
ModularFramework can be downloaded [here](https://bintray.com/me4502/maven/ModularFramework/_latestVersion). This version can be used as both a plugin and a library.

## Using ModularFramework
To get started with ModularFramework, call the static method in the ModularFramework class titled registerModuleController. From here, you have access to methods to register modules annotated with an @Module annotation.

### Using ModularFramework as a shaded dependency
When shading ModularFramework, it is important to do a relocate on the shade. Inside the relocate however, the ModularFramework class should be excluded. Failure to do so will remove some crucial functionality.
```   
relocate ("com.me4502.modularframework", "your.plugin.package.modularframework") {
    include(dependency("com.me4502:ModularFramework"))
    exclude "com.me4502.modularframework.ModularFramework"
}
```
    
Outside of the relocate closure, the file that was excluded still needs to be removed from the jar. The following line accomplishes that.
```
exclude "com/me4502/modularframework/ModularFramework.class"
```
Ensure that the exclude statement occurs **after** the relocate statement.

From this point, continue to use the library as you normally would, except with ShadedModularFramework instead of ModularFramework.

## Plugins using ModularFramework
* CraftBook

Have a plugin using ModularFramework? Contact me and I'll add it.