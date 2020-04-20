# Understanding how Java packages
You probably already know that Java is a compiled language, but you might not be familiar with how Java actually uses the files once they have been compiled. In this section, I'm going to go into some of the details of how we go from source files to a running application. If you are already familair with what a jar file is, how it is used and what it contains, then you can skip this section.

## `.class` files and the class path

Java is compiled, this means that the Java Runtime Environment (JRE) does not use java source files (ones with names ending in `.java`) directly, instead they must first be into `.class` files using the Java compiler (`javac`) before they can be used.

If I have a file `HelloWorld.java` containing the following:
```java
public final class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}
```
I can compile it using `javac HelloWorld.java`, and then run it using `java HelloWorld`. The `.class` at the end of the file is not included, because technically I am not telling `java` to run the file `HelloWorld.class`. I am telling it that I want to run a class named `HelloWorld` and it will search to find find a definition of a class with that name. Where it searches and what it finds can get complicated, and we will get into that later. 

In order to prevent name clashes, classes are broken up into packages. For example the class `Table` in the package `database` would be different to the class `Table` in `html`. Say we wanted to put `HelloWorld` in the package `com.example`, then first we would need to add a line to the top of the file as follows:
```java
package com.example;
public final class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}
```
and then we need to make sure that when we run it, it is in the folder `com/example`, relative to folder we run `java` from. If you were going to compile and run it, you would use somethign like this:
```bash
> javac com/example/HelloWorld
> java com.example.HelloWorld
Hello world!
```

If you were to change directories then `java com.example.HelloWorld` would stop working - you'd get an `ClassNotFound` error. This is because of something called the **Class Path** which controls where java searches for `.class` files. By default the class path is set to the current directory, but you can change it to any directory you want. In fact, you can set it to be a list of directories, and java will search each of these sequentially using the first class it finds with the correct name.

Clone this repo and try the following:
```
> cd hello_world/
hello_world> javac com/example/HelloWorld.java
hello_world> java com.example.HelloWorld
Hello world!
hello_world$ cd ..
> java com.example.HelloWorld
Error: Could not find or load main class com.example.HelloWorld
Caused by: java.lang.ClassNotFoundException: com.example.HelloWorld
> java -cp hello_world com.example.HelloWorld
Hello world!
```

The class path can be set either by using the `-cp` flag each time `java`/`javac`
is run, or by setting the `CLASSPATH` environment variable and running `java`/`javac` as normal. I think that in general using the `-cp` is a better practice than setting an environment varialbe.

## `.jar` files
It's pretty rare that you will ever find a java class that works all by itself. Most java packages constist of tens, often hundreds of seperate `.class` files, and often they depend on many extra data files such as text and images. In order to make it easy to share packages, Java has a special file format gathering these all up together into somethign more portable. These portable packages are called `.jar` files. Basically they are just zipped up package directories, with some extra meta-data to help organise them. You can add a `.jar` to the class path just like you would a regular directory. Behind the scenes, `java` will unzip the jar and add all the `.class` files inside to its search list. Again, when your code tries to load a class, it will get the first one on the search path with a matching name.

Creating a basic `.jar` file is fairly simple. You can use a utility called `jar`, or if you know what you are doing you could even use zip. The `jar` utility is usually better because it will take care of creating the metadata for you and not just zipping up the directory. Let's make a jar for the `HelloWorld` application.

```
> cd hello_world
hello_world> jar cf hello.jar com/example/HelloWorld.class
```
This just graps the `.class` file and puts it in a `.jar`. You could email this to someone and they could run it as follows:

```
> java -cp hello.jar com.example.HelloWorld
```

Not very impressive when you only have one file in the archive, but if there were 100+ files in there, you can see how it would be handier.

 