# Maven Silent Extension (MSE)

A Maven core extension that replaces Maven's verbose output with a dense,
machine-readable format. Designed for AI coding agents that operate within
finite context windows, where thousands of lines of lifecycle ceremony waste
tokens and bury actionable errors.

## How it works

MSE implements Maven's `EventSpy` API. When activated, it:

1. Suppresses Maven's default SLF4J logging (lifecycle banners, download
   progress, plugin headers).
2. Parses Surefire/Failsafe XML reports after test execution to extract
   structured failure details -- regardless of JVM forking.
3. Emits all output as `MSE:`-prefixed lines for unambiguous machine parsing.

## Requirements

- Java 11+
- Maven 3.6.x through 3.9.x

## Installation

Run the installer plugin in your project root:

```
mvn info.jerrinot:mse-maven-plugin:install
```

This creates `.mvn/extensions.xml` with the MSE extension entry. Maven downloads it automatically on the next build.

Alternatively, create `.mvn/extensions.xml` manually:

```xml
<extensions>
    <extension>
        <groupId>info.jerrinot</groupId>
        <artifactId>maven-silent-extension</artifactId>
        <version>0.1.0</version>
    </extension>
</extensions>
```

To uninstall:

```
mvn info.jerrinot:mse-maven-plugin:uninstall
```

## Usage

Activate with a system property or environment variable:

```
mvn -Dmse clean verify
```

or

```
MSE_ACTIVE=true mvn clean verify
```

Without activation the extension is inert: Maven behaves as if it were not installed.

## Output format

All lines are prefixed with `MSE:`.

Successful build:

```
MSE:SESSION_START modules=12 goals=clean,verify
MSE:OK modules=12 passed=342 failed=0 errors=0 skipped=0 time=47s
```

Test failure:

```
MSE:FAIL maven-surefire-plugin:test @ my-module
MSE:TESTS total=342 passed=339 failed=2 errors=1 skipped=0
MSE:TEST_FAIL com.example.AppTest#testParseInput
  expected:<42> but was:<0>
  at com.example.AppTest.testParseInput(AppTest.java:27)
MSE:TEST_ERROR com.example.ServiceTest#testConnection
  java.net.ConnectException: Connection refused
  at com.example.ServiceTest.testConnection(ServiceTest.java:18)
MSE:BUILD_FAILED failed=1 modules=12 passed=339 failed=2 errors=1 skipped=0 time=52s
```

Compiler error:

```
MSE:FAIL maven-compiler-plugin:compile @ my-module
MSE:ERR /src/main/java/com/example/App.java:10:15 cannot find symbol
MSE:ERR /src/main/java/com/example/App.java:20:1 ';' expected
MSE:BUILD_FAILED failed=1 modules=12 passed=0 failed=0 errors=0 skipped=0 compiler_errors=2 time=8s
```

Internal error (falls back to passthrough):

```
MSE:PASSTHROUGH <reason>
```

## Limitations

- Output from non-compiler, non-test plugins (exec-maven-plugin,
  frontend-maven-plugin) is not yet captured.
- Maven 4.x compatibility is untested.
- Plugins that fork arbitrary OS processes are out of scope.

## License

[Apache License, Version 2.0](LICENSE)