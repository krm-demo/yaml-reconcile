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
              This is the line 1
            This is |the second| one
            And it' || the last
            """;
        AnsiText ansiText = AnsiText.ansiText(threeLines);
        System.out.println(ansiText.dumpLines());
        assertThat(ansiText.renderLinesAnsi().collect(joining(lineSeparator()))).isEqualTo(threeLines);
        System.out.println("=====================================");
        System.out.println(ansiText.dumpSpans());
        assertThat(ansiText.renderSpansAnsi().collect(joining("|"))).isEqualTo(escapeJava(threeLines));
    }
}
