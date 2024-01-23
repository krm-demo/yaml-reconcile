package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.Test;
import org.krmdemo.yaml.reconcile.ansi.AnsiStyle;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class AnsiStyleTest {

    @Test
    void testStyleSequence() {
        List<AnsiStyle> styles = asList(
            AnsiStyle.empty().formatString("red").build(),
            AnsiStyle.empty().formatString("blue").build(),
            AnsiStyle.empty().formatString("green").build()
        );
        List<String> spans = new LinkedList<>(asList(
            "span-1", "span-2", "span-3"
        ));
        String stylesDump = styles.stream()
            .map(st -> format("style<%s>{%s}", st.dump(), spans.removeFirst()))
            .collect(Collectors.joining(lineSeparator()));
        System.out.println(stylesDump);
        assertThat(stylesDump).isEqualTo("""
            style<:red>{span-1}
            style<:blue>{span-2}
            style<:green>{span-3}""");
    }
}
