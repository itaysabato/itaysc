Undermind - README.txt

Contact:    Itay Sabato <itaysabato@gmail.com>
Submission Type:    JNI proxy bot
File I/O:   No files are read/write during play
Libraries:  BWAPI 3.6.1 (included in submission)
                            JNI-BWAPI 0.2 (included in submission)
                            jdk-6u26-windows-i586 (not included in submission)
                            http://www.oracle.com/technetwork/java/javase/downloads/jdk-6u26-download-400750.html

Compilation Instructions (Preferably in this order):

ExampleAIClient.dll:
- Open BWAPI_3.6.1\ExampleProjects.sln in MSVC++ 2008 Express Edition
- Edit Project Properties > C/C++ > Additional Include Directories
    - Change "C:\Program Files\Java\jdk1.6.0_26\include" to your local jdk1.6.0_26\include directory.
    - Change "C:\Program Files\Java\jdk1.6.0_26\include\win32" to your local jdk1.6.0_26\include\win32 directory.
- Build solution in Release mode (should already be set to output a dll).

Java class files:
- make sure your local jdk1.6.0_26\bin directory is included in the PATH environment variable.
- open a command prompt terminal and change directory to the location of this file.
- execute the following commands in the terminal:
    javac -d BWAPI_3.6.1\Release eisbot\proxy\*.java eisbot\proxy\model\*.java eisbot\proxy\types\*.java eisbot\proxy\util\*.java
    javac -d BWAPI_3.6.1\Release undermind\*.java undermind\utilities\*.java undermind\strategy\*.java undermind\strategy\decision\*.java undermind\strategy\execution\*.java undermind\strategy\representation\*.java

The compiled ExampleAIClient.dll and java class files should be found in BWAPI_3.6.1\Release
