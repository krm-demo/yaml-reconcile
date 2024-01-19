package org.krmdemo.yaml.reconcile.test.ansi;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.apache.commons.text.StringEscapeUtils.escapeJava;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class AnsiTest {

    @TempDir
    File tmpDir;

    @Test
    @DisplayName("(1) the first test-method")
    void testFirst() {
        System.out.println("inside the first method:");
        System.out.println("- first output (line-1");
        System.out.println("- first output (line-2");
        System.out.println("- first output (line-3");
    }

    @Test
    @DisplayName("(2) the first test-method")
    void testEscape() {
        String blueInTheMiddle = "This is a \u001b[1;34mblue\u001b[0;39m fragment in text";
        System.out.printf("blueInTheMiddle = '%s'%n", blueInTheMiddle);
     }

    @Test
    @DisplayName("(3) the last test-method")
    void testLast() {
        System.out.println("inside the last method:");
        System.out.println("- last output (line-5");
        System.out.println("- last output (line-6");
    }
}
