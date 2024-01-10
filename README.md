### File converter in Base32 (Java)

#### Overview
This project implements
a simple Java application for encoding/decoding files using the Base32 algorithm.
*More about algorithm:*
 - [Official standard of algorithm](https://www.ietf.org/rfc/rfc3548.txt)
 - [Wikipedia article](https://en.wikipedia.org/wiki/Base32)

The core functionality is implemented in the `CustomBase32` class, which includes methods for encoding and decoding byte arrays. 

The `FileConverterBase32` class depends on this functionality to allow the works with files.

Project has simple GUI for user _(Implements with Swing)_:
![Example of GUI](./example1.gif)
#### Table of Contents
- [Project Structure](#project-structure)
- [Dependencies](#dependencies)
- [How to Run](#how-to-run)

#### Project Structure

- `CustomBase32`: Contains the core functionality for encoding/decoding byte arrays using the Base32 algorithm.
- `FileTransformBase32`: Extends the functionality to encode and decode file contents.
- `Base32ConverterApp`: Simple Swing application, which implements GUI.

#### Requirements
 - Swing 
 - Maven 3.9.5
 - Java 19

#### How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/timofeevAS/Base32-converter
   cd Base32-converter
   ```
2. Build project in executable-jar:
   ```bash
   mvn package
   mvn clean compile assembly:single
   ```
   
3. Get *.jar executable application in `./target` folder

#### Usage
1. Choose file and push `Encode` or `Decode`
2. Encoded file creates next to chosen file with pseudo-extension `*.b32`

