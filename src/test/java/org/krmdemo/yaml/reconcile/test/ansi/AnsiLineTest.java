package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.krmdemo.yaml.reconcile.ansi.AnsiLine;
import org.krmdemo.yaml.reconcile.ansi.AnsiSpan;
import org.krmdemo.yaml.reconcile.ansi.AnsiStyle;
import org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.Color;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.fg;
import static org.krmdemo.yaml.reconcile.test.ansi.AnsiTestUtils.escapeJavaWithLS;

/**
 * Unit-test to check the functionality of {@link AnsiLine} class
 */
public class AnsiLineTest {

    @Test
    void testAnsiLine() {
        AnsiLine ansiLine = AnsiLine.create(format("@|fg(#f4) line @|bold #%d:|@", 123));
        System.out.println("-------- ansiLine.content(): ---------------------");
        System.out.println(ansiLine.content());
        System.out.println("-------- ansiLine.dump(): ------------------------");
        System.out.println(ansiLine.dump());
        System.out.println("-------- ansiLine.renderAnsi(): ------------------");
        System.out.println(ansiLine.renderAnsi());
        System.out.println("---- escapeJavaWithLS(ansiLine.renderAnsi()): ----");
        System.out.println(escapeJavaWithLS(ansiLine));
        assertThat(ansiLine.renderAnsi()).isEqualTo("\u001B[38;5;244mline \u001B[1m#123:\u001B[22;39m");
    }

    final static AnsiLine ansiLineRanges = AnsiLine.create(null, Stream.of(
        ansiSpan(Color.RED, 1, 11, 3),
        AnsiSpan.create(";"),
        ansiSpan(Color.CYAN, 123, 123),
        AnsiSpan.create(";"),
        ansiSpan(Color.BLUE, -4, 2),
        AnsiSpan.create(";"),
        ansiSpan(Color.GREEN, -456, -456),
        AnsiSpan.create(";"),
        ansiSpan(Color.MAGENTA, 7, 15, 25)
    ));

    @Test
    void testSubLineEmpty() {
        assertThat(ansiLineRanges.subLine(null, -100, -200)).isEqualTo(AnsiLine.empty());
        assertThat(ansiLineRanges.subLine(null, 200, 300)).isEqualTo(AnsiLine.empty());
        assertThat(ansiLineRanges.subLine(null, 100, -200)).isEqualTo(AnsiLine.empty());

        int width = ansiLineRanges.width();
        assertThat(ansiLineRanges.subLine(null, width-1, width).width()).isEqualTo(1);
        assertThat(ansiLineRanges.subLine(null, width-1, width-1).width()).isEqualTo(0);
        assertThat(ansiLineRanges.subLine(null, width, width).width()).isEqualTo(0);
        assertThat(ansiLineRanges.subLine(null, width, width+1).width()).isEqualTo(0);

        assertThat(ansiLineRanges.subLine(null, -1, 0).width()).isEqualTo(0);
        assertThat(ansiLineRanges.subLine(null, 0, 0).width()).isEqualTo(0);
        assertThat(ansiLineRanges.subLine(null, 1, 1).width()).isEqualTo(0);
        assertThat(ansiLineRanges.subLine(null, -1, 1).width()).isEqualTo(1);
        assertThat(ansiLineRanges.subLine(null, 0, 1).width()).isEqualTo(1);
    }

    @ParameterizedTest(name = "[{index}] from {0} to {1}")
    @CsvSource(delimiterString = " ::: ", value = {
        "0 ::: 10 ::: 3,6,9,12,1 ::: [ansi-style<fg(red)>]",
        "-5 ::: 45 ::: 3,6,9,12,15,18,21,24,27,30,33;123;-4,-3,-2,-1" +
            " ::: [ansi-style<fg(red)>, ansi-style-empty, ansi-style<fg(cyan)>, ansi-style-empty, ansi-style<fg(blue)>]",
        "25 ::: 40 ::: 0,33;123;-4,-3," +
            " ::: [ansi-style<fg(red)>, ansi-style-empty, ansi-style<fg(cyan)>, ansi-style-empty, ansi-style<fg(blue)>]",
        "50 ::: 64 ::: 2;-456;175,200" +
            " ::: [ansi-style<fg(blue)>, ansi-style-empty, ansi-style<fg(green)>, ansi-style-empty, ansi-style<fg(magenta)>]",
        "74 ::: 164 ::: 75,300,325,350,375" +
            " ::: [ansi-style<fg(magenta)>]",
    })
    void testSubLine(Integer beginIndex, Integer endIndex, String content, String strSpanStyles) {
        System.out.println(ansiLineRanges.renderAnsi());
        AnsiLine subLine = ansiLineRanges.subLine(null, beginIndex, endIndex);
        System.out.printf("[ %d ; %d ) --> '%s'%n", beginIndex, endIndex, subLine.renderAnsi());
        System.out.println("open spans-styles: " + subLine.spanStylesOpen().toList());
        assertThat(subLine.content()).isEqualTo(content);
        assertThat("" + subLine.spanStylesOpen().toList()).isEqualTo(strSpanStyles);
    }

    private static AnsiSpan ansiSpan(AnsiStyleAttr.Color color, int rangeFrom, int rangeTo) {
        AnsiStyleAttr styleAttr = fg(color);
        return AnsiSpan.create(AnsiStyle.ansiStyle(styleAttr), intRangeStr(rangeFrom, rangeTo, 1));
    }

    private static AnsiSpan ansiSpan(AnsiStyleAttr.Color color, int rangeFrom, int rangeTo, int multiplier) {
        return AnsiSpan.create(AnsiStyle.ansiStyle(fg(color)), intRangeStr(rangeFrom, rangeTo, multiplier));
    }

    private static String intRangeStr(int rangeFrom, int rangeTo, int multiplier) {
        return multiply(rangeClosed(rangeFrom, rangeTo), multiplier)
            .mapToObj(String::valueOf)
            .collect(joining(","));
    }

    private static IntStream multiply(IntStream intStream, int multiplier) {
        return intStream.map(num -> num * multiplier);
    }
}
