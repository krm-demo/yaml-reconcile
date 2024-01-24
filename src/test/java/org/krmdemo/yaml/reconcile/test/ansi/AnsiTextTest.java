package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.Test;
import org.krmdemo.yaml.reconcile.ansi.AnsiText;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.text.StringEscapeUtils.escapeJava;
import static org.assertj.core.api.Assertions.assertThat;

public class AnsiTextTest {

    @Test
    void testMultiLines() {
        String threeLines = """
              This is the line 1 @|blue; and
            this |@; is @|red; the second |@cyan; one
            but @|bold;it'|@ the last
            """;
        AnsiText ansiText = AnsiText.ansiText(threeLines);
        System.out.println("========== dump lines: ======================");
        System.out.println(ansiText.dumpLines());
        assertThat(ansiText.renderLinesAnsi().collect(joining(lineSeparator()))).isEqualTo(threeLines);
        System.out.println("========== dump spans: ======================");
        System.out.println(ansiText.dumpSpans());
        String expectedSpans = escapeJava(threeLines).replaceAll("[|@]", "");
        String actualSpans = ansiText.renderSpansAnsi().collect(joining()).replaceAll("\\|@", "");
        assertThat(actualSpans).isEqualTo(expectedSpans);
    }
}
