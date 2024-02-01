package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.Test;
import org.krmdemo.yaml.reconcile.ansi.AnsiText;

public class AnsiTextTest {

//    public static void main(String[] args) {
//        System.out.println("This is \u001B[31mred fragment\u001B[39m of text");
//    }
//
    @Test void testHelloAnsi() {
        String prefixAnsi = "\u001b[90m testHelloAnsi(): \u001b[39m";
        System.out.println(prefixAnsi + "this is \u001b[31mthe red fragment\u001b[0m of text");
        System.out.println(prefixAnsi + "this is \u001B[1;34mthe blue and bold fragment\u001B[0m of text");
    }

    @Test
    void testStyleOpen() {
        String threeLines = """
              the first line 1 with 2 leading spaces
            this is @|red the red fragment|@ without leading space or semicolon;
            this is @|red;the same red one |@with neither leading space nor semicolon again;
            this is @|red,bold; red and bold fragment|@ with leading space;
            and @|underline,blue ;underline and blue fragment|@ fragment with leading semicolon;
            """;
        System.out.println(threeLines);
        AnsiText ansiText = AnsiText.ansiText(threeLines);
        System.out.println("========== renderAnsi: ======================");
        System.out.println(ansiText.renderAnsi());
        System.out.println("========== dump lines: ======================");
        System.out.println(ansiText.dump());
//        assertThat(ansiText.renderLinesAnsi().collect(joining(lineSeparator()))).isEqualTo(threeLines);
//        System.out.println("========== dump spans: ======================");
//        System.out.println(ansiText.dumpSpans());
//        String expectedSpans = escapeJava(threeLines).replaceAll("[|@]", "");
//        String actualSpans = ansiText.renderSpansAnsi().collect(joining()).replaceAll("\\|@", "");
//        assertThat(actualSpans).isEqualTo(expectedSpans);
    }
}
