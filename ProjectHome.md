## Quick Links ##
[Getting Started](Readme.md) | [How Testability is determined](HowItWorks.md) | [How to Contribute](HowToContribute.md) | [Scores for Open-Source projects](http://testabilityexplorer.org)

## Sample Reports ##
[Source-annotated Report](http://testability-explorer.googlecode.com/svn/te-report/index.html)

Testability-explorer is a tool which analyzes Java bytecode and computes how difficult it will be to write unit tests for the code. It attempts to help you quantitatively determine how hard your code is to test, and where to focus to make it more testable.

Testability Explorer can be used:
  1. As a learning tool which flags causes of hard to test code with detailed breakdown of reasons.
  1. To identify hard to test hair-balls in legacy code.
  1. As part of your code analysis-toolset.
  1. As a tool which can be added into continuous integration that can enforce testable code.

Currently the tool computes:
  1. Non-Mockable Total Recursive Cyclomatic Complexity. _Cyclomatic Complexity_ is a measure of how many different paths of execution are there in the code. It is computed, by counting the `if`, `while`, and `case` as branching primitives. It is _recursive_ because cost of the method as well as any methods it calls are counted. It is _total_ because cost of object construction as well as any static initializations are counted. And finally, it is _non-mockable_ because any code which can be mocked out in test is not counted as part of the cost. This means that the score is based on the amount of complex code that cannot be mocked out in a unit test.
  1. Global Mutable State. Counts the number of fields which are globally reachable by the class under test and which are mutable. Mutable global state makes testing difficult as tests are not isolatable, the global state needs to be set up and cleared between tests.
  1. Law of Demeter. This is the principle that calling methods on objects you get from other collaborators is trouble, instead, the collaborator should call that method itself. It makes testing harder because your mocks must expose some internal state through these methods.

Check out the [Readme](Readme.md), or post a message to the [mailing list](http://groups.google.com/group/testability-explorer).

For a demo of testability-explorer in action see http://testabilityexplorer.org/report

[![](http://www.testabilityexplorer.org/img/jetty-6.x-progress.png)](http://www.testabilityexplorer.org)

