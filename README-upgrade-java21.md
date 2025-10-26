Upgrade to Java 21 - instructions for Windows (PowerShell)

This project was updated to target Java 21 in `pom.xml` (property `java.version` -> 21).

What I changed:
- Set `<java.version>` to 21
- Added `maven-compiler-plugin` configured with `release` = 21
- Added `maven-toolchains-plugin` and `maven-enforcer-plugin` to require Java 21

Steps to install JDK 21 and build (PowerShell):

1. Download and install a JDK 21 distribution. Example: Temurin (Adoptium) or Azul Zulu.
   - Temurin: https://adoptium.net/
   - Azul Zulu: https://www.azul.com/downloads/

2. After installing, set JAVA_HOME and update PATH in PowerShell for the current session:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
$env:Path = "$env:JAVA_HOME\\bin;" + $env:Path
mvn -v
```

3. Build the project using Maven (from project root):

```powershell
cd C:\Users\revan\ngo
mvn -DskipTests clean package
```

4. If you prefer to use Maven Toolchains, create a `~/.m2/toolchains.xml` pointing to your JDK 21 installation. Example:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<toolchains>
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>21</version>
      <vendor>any</vendor>
    </provides>
    <configuration>
      <jdkHome>C:\Program Files\Java\jdk-21</jdkHome>
    </configuration>
  </toolchain>
</toolchains>
```

Notes and next steps:
- Run the full test suite after installing JDK 21: `mvn test`.
- If compilation errors appear, update affected source code or dependencies. Common issues: removed/changed internal APIs, third-party libs requiring updates.
- I couldn't run the Copilot Pro upgrade tools (requires a higher plan), so I implemented a manual POM update. If you'd like, I can attempt automated refactors using OpenRewrite locally (requires adding plugins) or help update dependencies to Spring Boot 3.2.x+ if needed.

If you'd like, I can now:
- Try to install JDK 21 automatically (requires elevated/copilot pro tooling); or
- Run the build using a specified installed JDK path if you provide it; or
- Upgrade Spring Boot and other dependencies to latest compatible versions for Java 21.
