package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.Test;
import org.krmdemo.yaml.reconcile.ansi.AnsiText;

public class AnsiTextTest {

    @Test
    void testStyleOpen() {
        String threeLines = """
              the first line 1 with 2 leading spaces
            this is @|red the red|@ fragment without leading space or semicolon;
            this is @|red;the same red|@ with neither leading space nor semicolon again;
            this is @|red,bold; red and bold|@ fragment with leading space;
            and @|underline,blue ;underline and blue|@ fragment with leading semicolon;
            """;
        System.out.println(threeLines);
        AnsiText ansiText = AnsiText.ansiText(threeLines);
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
