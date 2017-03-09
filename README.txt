README for Asymmetrik Coding Challenge: Mobile Device Keyboard
Author: Jordan Fike

Build Instructions:

1. If you have not already done so, download the Java JDK: https://java.com/en/download/
2. Download the Java source files from the repository here:

	https://github.com/jrdnfike/CodingChallenge.git

	Keep the source files in the keyboard folder once copied to your desired directory.

3. Modify the file KeyboardTest.java as needed to include desired test cases.

NOTE: for a command line build, ensure the Java executables are available via your PATH.
i.e. PATH contains INSTALL_PATH/jdk_version#/bin

4. Compile the Java code using the command:
    
    UNIX: javac keyboard/*.java
    WINDOWS: javac keyboard\*.java

5. Create the executable jar file using the following command:

    UNIX: jar -cvfe KeyboardTest.jar keyboard.KeyboardTest keyboard/KeyboardTest.class keyboard/AutocompleteProvider.class keyboard/Candidate.class
    WINDOWS: jar -cvfe KeyboardTest.jar keyboard.KeyboardTest keyboard\KeyboardTest.class keyboard\AutocompleteProvider.class keyboard\Candidate.class

Run Instructions:

    Execute the command:

    java -jar KeyboardTest.jar


Implementation Details:

I separated the implementation into three classes:
Candidate, AutocompleteProvider and a test driver called KeyboardTest.

Motivation for AutocompleteProvider Data Structure:

I decided to use a list data structure as the stored dictionary
I did a quick glance around at other potential storage solutions,
the optimal of which for space would be some variation of a deterministic 
finite automaton (DFA).

As it would extend my work past 4 hours to implement such a structure,
as well as increase the likelihood of having to copy a correct implementation,
I decided to simply use lists to keep with the spirit of the challenge.

My sorting approach was to sort as list insertions are performed.
This was done using binary search algorithms, which achieve a performance
rate of O(n log n) worst case, like most post-insertion sorting methods.