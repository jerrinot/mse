package jerrinot.info.mse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SilentEventSpyFailureHintTest {

    @Test
    void extractFailureHintFromBuildLogFindsParseError(@TempDir Path tempDir) throws Exception {
        Path log = tempDir.resolve("mse-build.log");
        Files.writeString(log,
                "noise line\n"
                        + "  at com.example.Foo.bar(Foo.java:1)\n"
                        + "(line 32,col 73) Parse error. Found \"RowGroup\" <IDENTIFIER>\n"
                        + "Problem stacktrace :\n");

        String hint = SilentEventSpy.extractFailureHintFromBuildLog(log.toFile());
        assertEquals("(line 32,col 73) Parse error. Found \"RowGroup\" <IDENTIFIER>", hint);
    }

    @Test
    void looksLikeDiagnosticLineFiltersStackFramesAndKeepsErrors() {
        assertFalse(SilentEventSpy.looksLikeDiagnosticLine("  at com.example.Foo.bar(Foo.java:1)"));
        assertFalse(SilentEventSpy.looksLikeDiagnosticLine("... 23 more"));
        assertTrue(SilentEventSpy.looksLikeDiagnosticLine("cannot find symbol"));
        assertTrue(SilentEventSpy.looksLikeDiagnosticLine("error: ';' expected"));
    }
}
