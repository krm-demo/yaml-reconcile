package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.Test;
import org.krmdemo.yaml.reconcile.ansi.AnsiStyle;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class AnsiStyleTest {

    @Test
    void testStyleSequence() {
        List<AnsiStyle> styles = asList(
            AnsiStyle.empty().fg("red").build(),
            AnsiStyle.empty().fg("blue").build(),
            AnsiStyle.empty().fg("green").build()
        );
        List<String> spans = new LinkedList<>(asList(
            "span-1", "span-2", "span-3"
        ));
        String stylesDump = styles.stream()
            .map(st -> st.beginAnsi() + spans.removeFirst() + st.endAnsi())
            .collect(Collectors.joining(lineSeparator()));
        System.out.println(stylesDump);
        assertThat(stylesDump).isEqualTo("""
            start[1]<on>span-1end[1]<off>
            start[1]<on>span-2end[1]<off>
            start[1]<on>span-3end[1]<off>""");
    }
}
