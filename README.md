# Introduction

In this document I am going to look at a case-study involving two projects - one which produces a package intended to be used as a library, and a second project which depends on that library, and which produces an *executable* `.jar` file which can be used by an end-user.

I will begin by showing how these projects could be managed using bash scripts which run the command-line tools that come with the JDK. My goal here is to demonstrate clearly each of the steps that are needed to go from a set of source code files to a finished package. In the course of doing this I hope to also demonstrate that - even in these simple projects - there are several tricky issues which need to be addressed.

Once I have finished talking about the use of bash to manage the projects, I will then demonstrate how Apache Maven could be used instead.

This document assumes that you know how to compile Java source code without using and IDE, you know what the Java class path is, and you have at least some familiarity with `.jar` files. If you are unsure of these subjects, please read up on them [here](java_package_basics.md)

# The projects
I am going to use two very simple and very contrived projects to show the steps that are necessary to package something up so that it can be used elsewhere. The first project provides a class `RationalNumber` for working with numbers that consist of an integer numerator and denominator. This class allows arithmetic operations such as addition and multiplication to be performed on rationals, and it ensures that when they are printed they are done in a normalised way - eg that 2/4 will be rendered as 1/2. 

(TODO: Describe the `RationalNumber` class and how it could be used.)

The second project is a simple command line tool that tells the user how far they are through the year. It uses the `RationalNumber` number class form the first project to render the amount in a neat way - for example, the 5th of January is 1/73 of the way through an non-leap year. (TODO: Make it clear that this project just exists so we can demonstrate including another project.)

# Doing things in bash
Both of these projects are fairly simple, with only one class each so it's feasible to manage them using some simple bash scripts.

## Rational Number Project
The bash version of the Rational Number project is located in `./bash_build/rationals/`. This project contains an implementation of the `RationalNumber` class which allows us to do algebra with fractions. Some of the operations like addition and multiplication are a bit tricky, so I've included tests to make sure that they are implemented correctly. These tests are in the `RationalNumberTests` class which includes a main method so we can run it and check that everything is correct before we package up the `RationalNumber` class in a `.jar` so it can be included in some other project.

In this version of the class both the implementation and the tests are located in the same directory because it makes it easier to compile and run them. Let's do that now from the command line.

```
> cd bash_build/rationals/
> javac com/example/rationals/*.java
> java com.example.rationals.RationalNumberTests
PASS: Constructor
PASS: Constructor with default value
PASS: Constructor GCD
PASS: Addition
PASS: Equality
```

Packaging up the jar is pretty easy:
```
> jar cf rationals.jar com/example/rationals/*.class
```
This grabs all the files located in `com/example/rationals/*.class` and puts them in the `rationals.jar` file. If you are not familiar with compiling and packaging from the command line, check [this](java_package_basics.md) out.

## Building and Installing the package
The steps above are more or less the whole packaging process. If we want to automate it we will need somthing that will:
1. Compile the code.
1. Run the tests
1. Copy the relevant files into a jar

If any of the steps fail the whole process should stop. If the compile fails, then there will be no tests to run, and if the tests fail then there's no point in packaging up the incorrectly implemented classes.

This is pretty simple to implement. The `RationalNumberTests` main method has been set up to exit with a non-zero value if any of the test methods fail. In the *nix world a non-zero exit means the process has failed. If we combine this with the `set -e` command in bash, the script will stop if any of the individual lines fail. You can see this in `build.sh`


```bash
#!/bin/sh

set -e
javac  com/example/rationals/*.java
java com.example.rationals.RationalNumberTests
jar cf rationals.jar com/example/rationals/*.class
```

This builds the `rationals.jar` so that it can be included in another project. There's still one big problem - if we want to use the file we are going to have to find it. It would be a good idea to put it in some central location so that it's easier to get hold of. We could do this with a script that runs the `build.sh` and then just copies the file to some standard location. Picking a "standard location" is tricky, because different people have different stuff on their hard disks and unless we were all in agreement, my install script isn't going to work for someone else.
for example, suppose my script was as follows:

```bash
#!/bin/sh
set -e
sh ./build.sh
cp rationals.jar ~/my_java_libs
```

If you were to run this, and there was no `~/my_java_libs` directory on your system, you'd end up creating a new file named `~/my_java_libs` with the contents of your jar file. Probably not what you expect.

Instead you could rely on an environment variable like `$MY_LIB_LOCATION` to set the location - then everyone using the script could set it to whatever they want. It would probably be a good idea to check the viable is set before trying to run the build script and copy the files over. The end result would be something like this.

```bash
#!/bin/bash
set -e

if [[ ! -n "$MY_LIB_DIR" ]]; then
echo "ERROR: \$MY_LIB_DIR not set"
  exit 1
fi

sh ./build.sh
cp rationals.jar $MY_LIB_DIR
```

This started out pretty simple, but it's starting to get a bit complicated and it's still pretty fragile. This is a really simple project as well, so you can imagine how bad it could get in a real project.

## Using the `RationalNumber` class in another project.
The `progress` project is fairly contrived, it only really exists as an example of a project that depends on work developed in another project. Another key difference is that this project doesn't produce a library, it produces an executable which users will run directly. When we package this up we want it to be as self contained as possible, but that is going to be tricky because of its dependency on the `rationals` package.

Let's try just building a jar.

```bash
#!/bin/bash
set -e

if [[ ! -n "$MY_LIB_DIR" ]]; then
    echo "ERROR: \$MY_LIB_DIR not set"
    exit 1    
fi

SOURCE=com/example/progress
javac -cp $MY_LIB_DIR/rationals.jar $SOURCE/*.java
jar cf progress.jar $SOURCE/*.class
```

This is really similar to the previous build script - compile the classes and put them in a jar file. The only big difference was that we had to add `rationals.jar` to the class path when we did the compile. The big problem here is that if a user want's to run this they are also going to have to include `rationals.jar` on their class path by invoking it as follows

```bash
java -cp $MY_LIB_DIR/rationals.jar:progress.jar com.example.progress.YearProgress
```

We could wrap this up in a shell script, which would save the users some typing, but we are still going to have to find a way of making sure that anyone who tries to run the `YearProgress` class in the `progress.jar` file also has a copy of the `rationals.jar` file. Otherwise they are going to get an error like

```
Error: Unable to initialize main class com.example.progress.YearProgress
Caused by: java.lang.NoClassDefFoundError: com/example/rationals/RationalNumber
```

Java has the option of creating *executable* jars - file that we could execute using `java -jar progress.jar`. That would be a lot neater then the above solution, but it comes with a catch - you can't add to the class path when using executable jars. So if we want to produce an executable jar that works in our situation, we will have to find a way to include **all** the necessary classes in the `.jar` file.

Luckily, `.jar` files are actually just zip files with a funny name. If we want we can unzip them and manipulate their contents just like we would a zip. In our case we could unzip the contents of `rationals.zip` into a temporary directory and then use those files when we construct the `progress.jar` file. That would look something like this:

```bash
#!/bin/bash
set -e


if [[ ! -n "$MY_LIB_DIR" ]]; then
    echo "ERROR: \$MY_LIB_DIR not set"
    exit 1
fi

PACKAGE=com.example.progress
LIB=$MY_LIB_DIR/rationals.jar
SOURCE=`echo $PACKAGE | tr . /` # Convert the package name into a file path 
javac -cp $LIB $SOURCE/*.java
TMP_DIR=`mktemp -d`
unzip -uo $LIB -d $TMP_DIR
jar cfe progress-executable.jar $PACKAGE.YearProgress -C $TMP_DIR . $SOURCE/*.class
rm -r $TMP_DIR
```

There's actually a few things going on in the above script. First of all it uses `set -e` so that it will stop at the first error. Then it checks that `$MY_LIB_DIR` is set. There is a bit of string manipulation to make sure the package name and class locations match up. It then unzips `rationals.jar` into a temp directory as discussed above, before constructing an executable jar using the combined contents of the `com.example.rationals` and `com.example.progress` packages, with `com.example.progress.YearProgress` as the main class. 

This is starting to get pretty complicated. It works in our case, but I'm not actually sure that you can always just unzip the jars and add the class files like this. There's probably a bunch of other things you need to do if you want it to work properly, but TBH I haven't really worked with `javac`/`jar` directly from the command-line in almost 20 years. I always use something like Maven to do this stuff.

## Some Issues with using the command line tools directly 
Using bash worked, but even in this simple project there were a few issues. These include
* **Mixing up the main and test source files:** Both `RationalNumber.java` and `RationalNumberTests.java` are in the same directory. I did this because they are in the same package and having them in the same directory made it easier to compile and run them during development. In a small project like this it's not a big deal, but in a big project this can get very cluttered.
* **Bad management of `.class` files:** After the build script is finished, the intermediary `.class` files are left in the directory right beside the `.java` files. This is pretty bad - aside from cluttering up the folder, it also leads to the possibility that one of these files might get checked into the source repository or some other mistake like that. Really, humans don't care about `.class` files so they should be kept hidden away somewhere.
* **Bad management of tests:** If you look in `RationalTests.java` you'll see that there are seven tests, but if you look at the output when it is executed, you will see that only six tests are actually run. The multiplication test never runs! This is because - with the way I have implemented the test class - I need to explicitly invoke each test method from `main()` and I forgot to do this for `testMultiply()`.
* **The install script has extra logic** If someone wants to understand how my project works they are going to have to spend time learning how the install scripts work, in addition to understanding the Java code. In a real project the build script would likely get complicated as well.
* **Manipulating .jar files is a nightmare** That last build script that unzipped a jar file so that its contents could be included in another jar was pretty nasty. A complex operation like that needs an extensive set of tests to ensure it is working correctly, and if I had to rewrite something like that for every project, it would incur a significant overhead.
*  **Unnecessary classes were included in the .jars** If you take a look inside the jar files created by these build scripts, you'll see that I was a bit careless and the classes for the unit tests were included in the jar. These tests are completely unnecessary once the package is built, so they shouldn't be included in the jar artefacts that get shipped.
* **No versioning:** This is a bit more subtle, but right now the build has no concept of a version. Suppose I had several projects that depended on `RationalNumber`, and in particular they used the `RationalNumber::numericValue` method. Now suppose that for some reason I decided that `RationalNumber` should implement the `java.lang.Number` interface. This would mean that instead of `numericValue()` I would have to implement `doubleValue()`, `floatValue()` etc. All the projects that depended on `RationalNumber` would break as soon as I ran its `install.sh` script.

# Doing it in Maven
Now that we know the steps that are required to manually build/install/run these projects, let's do it again using Maven. If you've cloned the repo you can find the maven versions of these projects in the `maven_build` directory. Maven is an opinionated piece of software, so there are a few changes we have to make to get our projects to work with it. Let's look at each of them.

## The Rationals Project
The `pom.xml` config for this project is verbose, but that's just the nature of XML - the details are actually quite simple. If you want to check it out it is available [here](./maven_build/rationals/pom.xml). The important details are:
1. The `groupId`, `artifactId` and `version` are set to `com.example`, `rationals` and `1.0-SNAPSHOT` respectively.
1. I've included `junit` as a dependency. This will let me write slightly nicer unit tests. If you are unsure what to put in a dependency entry, you can look it up in [maven central](https://search.maven.org). Take note that this dependency has the `scope` value of "test". I'll talk about that later.
1. I've set the compiler and surefire test plugin to use newer versions than standard. This is done in the `pluginManagement` section. The entries look complicated, but if you go to the plugin's home-page they usually show you what values you need to enter here to get the newest version running.
1. In the `properties` section, I've set the source encoding to UTF-8. This isn't necessary, but it will get rid of a few annoying warning messages. In general you should aim to get rid of all warnings even if you don't think they are important, as they make it less obvious when you get a warning about something important.

If you inspect the config, you'll notice that I never specify *where* the source files are, or where Maven should put the results when it is finished. This is because Maven follows the principal of *Convention Before Configuration*. Maven expects all projects to follow the same structure, with the files in predefined locations. You can override these locations, but it's generally not considered a good idea unless you have a very good reason. Aside from cutting down on the amount of configuration options you have to supply, it means that when a new developer starts working on your project, all the files are going to be in the location they expect.

By default, Maven expects that all your source files will be in the `src` directory. More specifically, it expects that your main Java source files will be in `src/main/java` and your Java test files will be in `src/test/java`. If you had some Scala in your project the main files would go in `src/main/scala`, and I bet you can guess where the test files would go. As I noted above, one of the thigs I didn't like about my original project was that the tests were mixed in with the main code, but that it made it easier to compile. When maven runs it's going to take care of making sure that `javac` can find both sets of files - so here's something that Maven has solved already.

Running a compile in Maven is pretty simple: just run `mvn compile` on the command line at the project root. Maven will only compile files that have changed since the last time you compiled everything, which is usually a good idea. If for some reason you need to get it to start over and rebuild everything you can run `mvn clean` and then compile it, or you could even do `mvn clean compile`.

If you want to create the `.jar` file for the project you use `mvn package`. This will run the compilation step, then the tests and then produce a jar in the `target` folder. Using the `target` folder is another one of those conventions that you could override if you really want to, but it's best to just leave it as is. If you want to run the tests on their own, you can run `mvn test`, but I'll skip over the details of the test phase for now. 

Finally you can "install" the package using `mvn install`. The exact details of what this one does a bit more mysterious, but basically, after you run install, you will be able to use the package in other projects by listing it as a dependency. Under the hood, it copies the jar into a special, hidden folder in your home directory called `~/.m2/repository` so that other projects will be able to find it. It's good to know that this folder exists, but it's hidden for a reason so don't try making any changes to it manually. You will regret it.

In addition to the above phases, two other important ones are `mvn deploy` and `mvn release` which provide the functionality you need to make your package available to other users without them having to get your source-code and compile it themselves. These are outside the scope of this article, and these days they are usually run as part of an automated continuous integration pipeline, so you are unlikely to need to use them yourself.

### Running tests and the "test" scope
In this version of the rationals project the main code is the same but the tests have changed quite a bit, so let's take a look at those changes. Aside from being moved to a new directory, `RationalNumberTest` no longer has a main method. Instead of running the class directly, the Surefire plugin runs it by using introspection to find any methods that have been marked as tests. This prevents the problem we saw in the original project where I forgot to include one of the test methods in the main method. When using maven, every test method in any class in the test folder that has a name ending with "Test" will be evaluated. As we have separated out the main and test code into completely different folders, we don't need to worry about accidentally running a non-test class that just happens to contain the word "Test". One thing to watch out for is that the test methods are no longer `static`. Even though they look like they could be implemented as `static` methods, this would cause problems for surefire and it would not run them.

The `junit` dependency allows us to write nicer tests. First of all it provides the `@Test` annotation which allows us to explicitly specify which methods should be considered "test methods", instead of relying on surefire to pick them up based on their names. The second thing `junit` gives us is a set of assertion methods that we can use to check values are what we expect them to be.

When adding `junit` as a dependency, I specified that it was in the "test" scope. This means that only classes in the test folder can reference `junit`. e.g I couldn't use `assertEquals` in my main code. In addition, `junit` will not be included in any `.jar` files that the project produces. 

## The Progress project
The structure of the progress project is pretty similar to the rationals, so I'm just going to cover the interesting bits here. The full `pom.xml` is available [here](./maven_build/progress/pom.xml)

The first thing worth noting is that this project has the rationals as a dependency. We do this by referencing the full `groupId`, `artifactId` **and** `version`. If a new version of the rationals project is ever released, it's not going to affect this one. NB. because the rationals package hasn't been released publicly, you will have to run `mvn install` for the project before you can add it as a dependency here. 

```xml
<dependencies>
  <dependency>
    <groupId>com.example</groupId>
    <artifactId>rationals</artifactId>
    <version>1.0-SNAPSHOT</version>
  </dependency>
  ...
</dependencies>
```

The other interesting thing about this project is that it uses a plugin to take care of creating an executable jar which includes all the relevant dependencies. There are actually several plugins available which can do this, with the main distinction between them being how much control you have over their behaviour. The more simple plugins have less to configure, so it *kind of* makes sense that there are multiple ones that do basically the same thing. I went with `maven-assembly-plugin`.
This one is a bit tricky so let's look at it in detail.

```xml
<plugins>
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <executions>
      <execution>
        <phase>package</phase>
        <goals>
          <goal>single</goal>
        </goals>
        <configuration>
          <archive>
            <manifest>
              <mainClass>
                com.example.progress.YearProgress
              </mainClass>
            </manifest>
          </archive>
          <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
          </descriptorRefs>
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```

This plugin has one *execution* - one action that it's going to do. It'll execute during the `package` phase, which is basically the phase after everything gets tested, and it will run a goal called "single". The phase name is a maven thing, but the goal names are particular to each plugin. In the case of the maven assembly plugin, it only understands one goal, but some plugins have many different goals. If you are wondering the goal is called "single" because it wraps everything up in a single file.

In this project set two arguments when calling the `single` goal - we set `archive.manifest.mainClass` to "com.example.YearProgress", which majes the jar executable - and we tell it to use the "jar-with-dependencies" descriptor-ref. The descriptor-ref are pre configured templates for constructing files for a project. Jar-with-dependencies tells the assembly plugin to construct a jar with all the relevant classes included. Another descriptor-ref you might see is "src" which will create a source archive for your project. 

One tricky thing you might notice is that this plugin went in the `build.plugins` section of the POM, but the compiler and surefire plugins were included in the `build.pluginManagement.plugins` section. This is because `build.pluginManagement.plugins` is used for setting the parameters on plugins that are already included in the project, but `build.plugins` is for **adding** a new plugin to the project. The compiler and surefire plugins are included in every project by default, but the assembly plugin had to be added. This distinction doesn't make much sense in the case of a small project like this, but in larger projects which include sub-projects this can be helpful for keeping all the plugins under control. 