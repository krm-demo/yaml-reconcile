package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.Test;
import org.krmdemo.yaml.reconcile.ansi.AnsiText;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

public class AnsiTextTest {

    @Test
    void testMultiLines() {
        String threeLines = """
              This is the line 1
            This is the second one
            And this is the last
            """;
        AnsiText ansiText = AnsiText.ansiText(threeLines);
        System.out.println(ansiText.dump());
        assertThat(ansiText.renderAnsi().collect(joining(lineSeparator()))).isEqualTo(threeLines);
    }
}
