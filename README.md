# Introduction

In this document I am going to look at a case-study involving two projects - one which produces a package intended to be used as a library, and a second project which depends on that library, and which produces an *executable* `.jar` file which can be used by an end-user.

I will begin by showing how these projects could be managed using bash scripts which run the command-line tools that come with the JDK. My goal here is to demonstrate clearly each of the steps that are needed to go from a set of source code files to a finished package. In the course of doing this I hope to also demonstrate that - even in these simple projects - there are several tricky issues which need to be addressed.

Once I have finished talking about the use of bash to manage the projects, I will then demonstrate how Apache Maven could be used instead.

This document assumes that you know how to compile Java source code without using and IDE, you know what the Java class path is, and you have at least some familiarity with `.jar` files. If you are unsure of these subjects, please read up on them [here](java_package_basics.md)

# The projects
I am going to use two very simple and very contrived projects to show the steps that are necessary to package something up so that it can be used elsewhere. The first project provides a class `RationalNumber` for working with numbers that consist of an integer numerator and denominator. This class allows arithmetic operations such as addition and multiplication to be performed on rationals, and it ensures that when they are printed they are done in a normalised way - eg that 2/4 will be rendered as 1/2. 

The second project is a simple command line tool that tells the user how far they are through the year. It uses the `RationalNumber` number class form the first project to render the amount in a neat way - for example, the 5th of January is 1/73 of the way through an non-leap year.

# Doing things in bash
Both of these projects are fairly simple, with only one class each so it's feasible to manage them using some simple bash scripts.

## Rational Number Project
The bash version of the Rational Number project is located in `./bash_build/rationals/`. This project contains an implementation of the `RationalNumber` class which allows us to do algebra with fractions. Some of the operations like addition and multiplication are a bit tricky, so I've included tests to make sure that they are implemented correctly. These tests are in the `RationalNumberTests` class which includes a main method so we can run it and check that everything is correct before we package up the `RationalNumber` class in a `.jar` so it can be included in some other project.

In this version of the class both the implementation and the tests are located in the same directory because it makes it easier to compile and run them. Let's do that now from the command line.

```
> cd bash_build/rationals/
> javac com/example/rationals/*.jav
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

This bulilds the `rationals.jar` so that it can be included in another project. There's still one big problem - if we want to use the file we are going to have to find it. It would be a good idea to put it in some central location so that it's easier to get hold of. We could do this with a script that runs the `build.sh` and then just copies the file to some standard location. Picking a "standard location" is tricky, because different people have different stuff on their hard disks and unless we were all in agreement, my install script isn't going to work for someone else.
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
#!/bin/sh
set -e

if [ -n "$MY_LIB_DIR" ]; then
  sh ./build.sh
  cp rationals.jar $MY_LIB_DIR
else
  echo "ERROR: \$MY_LIB_DIR not set"
  exit 1
fi
```

This started out pretty simple, but it's starting to get a bit complicated and it's still pretty fragile. This is a really simple project as well, so you can imagine how bad it could get in a real project.

## Some Issues with this Approach
Using bash worked, but even in this simple project there were a few issues. These include
* **Mixing up the main and test source files:** Both `RationalNumber.java` and `RationalNumberTests.java` are in the same directory. I did this because they are in the same package and having them in the same directory made it easier to compile and run them during development. In a small project like this it's not a big deal, but in a big project this can get very cluttered.
* **Bad management of `.class` files:*** After the build script is finished, the intermediary `.class` files are left in the directory right beside the `.java` files. This is pretty bad - aside from cluttering up the folder, it also leads to the possibility that one of these files might get checked into the source repository or some other mistake like that. Really, humans don't care about `.class` files so they should be kept hidden away somewhere.
* **Bad management of tests:** If you look in `RationalTests.java` you'll see that there are seven tests, but if you look at the output when it is executed, you will see that only six tests are actually run. The multiplication test never runs! This is because - with the way I have implemented the test class - I need to explicitly invoke each test method from `main()` and I forgot to do this for `testMultiply()`.
* **The install script has extra logic** If someone wants to understand how my project works they are going to have to spend time learning how the install scripts work, in addition to understanding the Java code. In a real project the build script would likely get complicated as well.