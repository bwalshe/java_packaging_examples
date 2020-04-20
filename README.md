# Introduction

In this document I am going to look at a case-study involving two projects - one which produces a package intended to be used as a library, and a second project which depends on the first, and produces an *executable* `.jar` file which can be used by and end-user.

I will begin by showing how these projects could be managed using bash scripts which run the command-line tools that come with the JDK. My goal here is to demonstrate clearly each of the steps that are needed to go from a set of source code files to a finished package. In the course of doing this I hope to also demonstrate that - even in these simple projects - there are several tricky issues which need to be addressed.

Once I have finished talking about the use of bash to manage the projects, I will then demonstrate how Apache Maven could be used instead.

This document assumes that you know how to compile Java source code without using and IDE, you know what the Java class path is, and you have at least some familiarity with `.jar` files. If you are unsure of these then please read up on them [here](java_package_basics.md)

# The projects
I am going to use two very simple and very contrived projects to show the steps that are necessary to package something up so that it can be used elsewhere. The first project provides a class `RationalNumber` for working with numbers that consist of an integer numerator and denominator. This class allows arithmetic operations such as addition and multiplication to be performed on rationals, and it ensures that when they are printed they are done in a normalised way - eg that 2/4 will be rendered as 1/2. 

The second project is a simple command line tool that tells the user how far they are through the year. It uses the `RationalNumber` number class form the first project to render the amount in a neat way - for example, the 5th of January is 1/73 of the way through an non-leap year.