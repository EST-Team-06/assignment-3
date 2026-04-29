# Effective Software Testing Lab (Assignment 3)

To correctly complete this assignment, you **must**:

1. **Work in a team of three students.** The assignment must be submitted by groups of *three* students. You are required to keep the same group composition as in Assignments 01 and 02.

2. **Work with your team only** (unless otherwise stated). While you may discuss ideas with other teams, each team is expected to develop its own unique solution. A strict plagiarism policy will be enforced for all submitted work.

3. **Use Generative AI appropriately.** You must ensure that the final solution is your own original work. Generative AI should only be used as a tutor or assistant (e.g., to help troubleshooting or learning a new API), not for directly producing solutions. If you make use of generative AI, **you have to document all the prompts you have used and an overall explanation of how you used Generative AI**. At the end, you are responsible for the answers you submit. To avoid misuse of Generative AI, we will do the following:

   3a. **_Random Audits_**: After all three assignments have been submitted and graded, a subset of groups will be **semi-randomly selected** to present and explain their work. This presentation will cover the rationale behind specific decisions, the designed test cases, and any other submitted artifact. Each group member is expected to demonstrate **a thorough understanding of the entire submitted solution**. Inability to do so may result in individual penalties, disqualification of the group’s submission, and, in severe cases, the incident may be reported as **academic misconduct**. It is essential that **every group member fully understands and can explain all parts of the submitted work** — there is no option to claim unfamiliarity with any part of the solution. **Failure to demonstrate this understanding will result in failed assignment(s) for the student.**

   3b. **_Plagiarism detection_**: Automated tools will be used to detect code similarities and identify AI-generated content. Any instances of code reuse or reliance on AI beyond the limits described above will be flagged for further review.

4. **Follow the instructions and submission guidelines precisely**. Ensure that your solutions adhere to the structure outlined in the submission section.

5. **Deadline.** The deadline for this assignment is **May 26, 2026, at 18:00** (Zurich, CH, time).


## Submission

Before the deadline, finalize your work and prepare **one single zip file**. This zip file must include all the folders provided with this assignment, each one augmented with the codes and tests you write for the corresponding problem (as described below).

For exercices 1-5, in the **root folder**, create **one markdown file per problem***, named using the format:

`solution_[problem name].md` (e.g., `solution_IntervalMerger.md`)

In each of these files, document your decisions and provide the information requested below.

For assignments 6 and 7, include your submission as a single PDF file for each task. Along with that PDF file, provide any required code snippets you maybe used to solve each task.

## Exercises
This assignment consists of 5 + 2 exercises. 

Exercises 1–5 each have their own folder and contain a problem definition and the corresponding solution in Java. Your task is to design, implement, and document tests for these solutions, following the instructions provided in each exercise's `README.md` file. We suggest the use of the IntelliJ IDEA integrated development environment.

Assignments 6 and 7 will be released weekly this week and next week, after the lectures of Dr. Alexandra Bugariu.

### Structure of an Exercise

In exercises 1–5, each exercise contains a `src` folder, which contains at least two files,
`ExerciseName.java` and `ExerciseNameTest.java`. The `ExerciseName.java` file contains the solution which you have to test, and the `ExerciseNameTest.java` file is the file where you have to write your tests.  In addition to the `src` folder, each project contains a `README.md` file that describes the problem and instructions for testing and a `pom.xml` file that contains the Maven configuration. 

### Requirements
Java 11 has been used for all the exercises 1-5, we cannot guarantee things will work with other versions. 

### Chapters covered
This assignment covers **Chapter 6: Test doubles and mocks**, **Chapter 7: Designing for testability**, **Chapter 8: Test-driven development**, and **Chapter 10: Test code quality**. You will apply the concepts learned to design and implement tests for the given exercises.

Moreover, the two guests lectures from Dr. Bugariu will be covered in separately released tasks.

## Grading Criteria
Each assignment will be graded with either 0 (fail) or 1 (pass). For an assignment to receive a 1, it must:

1. Provide clear evidence of effort.

2. Follow the principles discussed in Chapter 10 regarding test code quality. Adherence to these principles will be considered during the evaluation.

3. Provide a clear `solution_[problem name].md` file for exercices1-5.

4. Provide code that fulfills the requirements of the assignment in each exercise or explains why this could not be achieved and what has been tried, and how.

5. For the two tasks from the guest lectures, there must be signs of enough effort for each of the two tasks.

