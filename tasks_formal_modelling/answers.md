# Formal Modeling
## Task 1a
I used [JetUML](https://www.jetuml.org/) for the class diagram. 

![UML_Class](UML_Class.png)

* `isClassmate` cannot be modelled nicely in class diagrams. It's more of a predicate and hence I decided to use a method.
    * It returns true if student is from same unviersity and has same major, are same class (Undergrad/Grad) and not the same student.
* Instead of having a boolean attribute, it made more sense to make a method `isLegal()`, returns true if university is present. Otherwise false. 
    * The concept of legality implies that university is nullable; a student can be a student without being registered to a university, which makes them non-legal.

## Task 1b
* Alloy does not have in-built booleans, so we make our own, see [docs](https://alloy.readthedocs.io/en/latest/modules/boolean.html)

### Model
```
abstract sig Bool {}
one sig True, False extends Bool {}

sig University {}
sig Major {}
sig StudentID {}

abstract sig Student {
    studentID : one StudentID,
    legal     : one Bool,
    major     : one Major,
    university: lone University
}

sig GraduateStudent extends Student {}
sig UndergraduateStudent extends Student {}

fact uniqueStduentIDs {
    all disj s1, s2 : Student |
        s1.studentID != s2.studentID
}

fact legal {
    all s : Student |
        (some s.university) iff (s.legal = True)
}

pred classmates[s1, s2 : Student] {
    s1 != s2
    s1.major = s2.major
    s1.university = s2.university

    (
        (s1 in GraduateStudent and s2 in GraduateStudent)
        or
        (s1 in UndergraduateStudent and s2 in UndergraduateStudent)
    )
}

pred show {}
run show for exactly 2 University,
           exactly 3 Major,
           exactly 3 Student,
           exactly 3 StudentID
```
### Visalization
![AlloyViz](AlloyViz.png)