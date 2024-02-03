package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.krmdemo.yaml.reconcile.ansi.AnsiText;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;
import static org.apache.commons.text.StringEscapeUtils.escapeJava;
import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.yaml.reconcile.ansi.AnsiRenderCtx.renderCtx;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.RESET_ALL;
import static org.krmdemo.yaml.reconcile.util.StreamUtils.toSortedMap;

public class AnsiTextTest {

    @ParameterizedTest(name = "(siblingStylesSquash = {0})")
    @ValueSource(booleans = {true, false})
    void testSimpleStyles(boolean siblingStylesSquash) {
        String textAnsiFmt = """
              the first line with two leading spaces
            this is '@|red the red fragment|@' without leading space or semicolon;
            this is '@|red;the same red one |@'with neither leading space nor semicolon again;
            this is '@|red,bold; red and bold fragment|@' with leading space;
            and '@|underline,blue ;underline and blue fragment|@' fragment with leading semicolon;
            
            @|yellow,bold;warning|@ starts the line
            and this line ends with @|yellow,dim another warning|@""";
        System.out.println("-------- text with ansi-text format: ------------");
        System.out.println(textAnsiFmt);

        AnsiText ansiText = AnsiText.ansiText(textAnsiFmt);
        System.out.println("-------- ansiText.content(): ---------------------");
        System.out.println(ansiText.content());
        System.out.println("-------- ansiText.dump(): ------------------------");
        System.out.println(ansiText.dump());
        System.out.println("-------- ansiText.renderAnsi(): ------------------");
        System.out.println(ansiText.renderAnsi());

        System.out.println("---- escapeJavaWithLS(ansiText.renderAnsi()): ----");
        System.out.println("renderCtx() --> " + renderCtx());
        System.out.println(escapeJavaWithLS(ansiText));
        assertThat(ansiText.renderAnsi()).isEqualTo("""
              the first line with two leading spaces
            this is '\u001B[31mthe red fragment\u001B[39m' without leading space or semicolon;
            this is '\u001B[31mthe same red one \u001B[39m'with neither leading space nor semicolon again;
            this is '\u001B[1;31m red and bold fragment\u001B[22;39m' with leading space;
            and '\u001B[4;34m;underline and blue fragment\u001B[24;39m' fragment with leading semicolon;
            
            \u001B[1;33mwarning\u001B[22;39m starts the line
            and this line ends with \u001B[2;33manother warning\u001B[22;39m""");
    }

    @Test
    void testNestedStyles() {
        String textAnsiFmt = """
            @|bold;the first line with leading bold fragment|@ and trailing no-style
              the next style starts @|green here and
            continues @|italic at this line @|!fg and ends without color|@
            """;
        System.out.println("-------- text with ansi-text format: -------------");
        System.out.println(textAnsiFmt);

        AnsiText ansiText = AnsiText.ansiText(textAnsiFmt);
        System.out.println("-------- ansiText.content(): ---------------------");
        System.out.println(ansiText.content());
        System.out.println("-------- ansiText.dump(): ------------------------");
        System.out.println(ansiText.dump());
        System.out.println("-------- ansiText.renderAnsi(): ------------------");
        System.out.println(ansiText.renderAnsi());

        System.out.println("---- escapeJavaWithLS(ansiText.renderAnsi()): ----");
        System.out.println("renderCtx() --> " + renderCtx());
        System.out.println(escapeJavaWithLS(ansiText));
        assertThat(ansiText.renderAnsi()).isEqualTo("""
            \u001B[1mthe first line with leading bold fragment\u001B[22m and trailing no-style
              the next style starts \u001B[32mhere and\u001B[39m
            \u001B[32mcontinues \u001B[3;32mat this line \u001B[3;39mand ends without color\u001B[23m
            """);
        assertThat(escSeqByPos(ansiText.renderAnsi()).entrySet()).containsExactly(
            kv(2, "1"), kv(47, "22"),
            kv(99, "32"), kv(112, "39"),
            kv(118, "32"), kv(133, "3;32"), kv(153, "3;39"), kv(182, "23"));

        System.out.println("========= switching off sibling-styles-squash =======");
        renderCtx().setSiblingStylesSquash(false);
        System.out.println();

        System.out.println("-------- ansiText.renderAnsi(): ------------------");
        System.out.println(ansiText.renderAnsi());
        System.out.println("---- escapeJavaWithLS(ansiText.renderAnsi()): ----");
        System.out.println("renderCtx() --> " + renderCtx());
        System.out.println(escapeJavaWithLS(ansiText));
        assertThat(escSeqByPos(ansiText.renderAnsi()).entrySet()).containsExactly(
            kv(2, "1"), kv(47, "22"),
            kv(99, "32"), kv(112, "39"),
            kv(118, "32"), kv(133, "39"), kv(138, "3;32"), kv(158, "23;39"), kv(166, "3"), kv(192, "23"));

        System.out.println("========= switching on line-prefix-reset-all =======");
        renderCtx().setSiblingStylesSquash(true);
        renderCtx().setLinePrefixResetAll(true);
        System.out.println();

        System.out.println("-------- ansiText.renderAnsi(): ------------------");
        System.out.println(ansiText.renderAnsi());
        System.out.println("---- escapeJavaWithLS(ansiText.renderAnsi()): ----");
        System.out.println("renderCtx() --> " + renderCtx());
        System.out.println(escapeJavaWithLS(ansiText));
        assertThat(escSeqByPos(ansiText.renderAnsi()).entrySet()).containsExactly(
            kv(  2, RESET_ALL.ansiCodeSeq()), kv(6, "1"), kv(51, "22"),
            kv( 79, RESET_ALL.ansiCodeSeq()), kv(107, "32"), kv(120, "39"),
            kv(126, RESET_ALL.ansiCodeSeq()), kv(130, "32"), kv(145, "3;32"), kv(165, "3;39"), kv(194, "23"),
            kv(200, RESET_ALL.ansiCodeSeq()));

        System.out.println("========= switching on line-suffix-reset-all =======");
        renderCtx().setLineSuffixResetAll(true);
        System.out.println();

        System.out.println("-------- ansiText.renderAnsi(): ------------------");
        System.out.println(ansiText.renderAnsi());
        System.out.println("---- escapeJavaWithLS(ansiText.renderAnsi()): ----");
        System.out.println("renderCtx() --> " + renderCtx());
        System.out.println(escapeJavaWithLS(ansiText));
        assertThat(escSeqByPos(ansiText.renderAnsi()).entrySet()).containsExactly(
            kv(  2, RESET_ALL.ansiCodeSeq()), kv(6, "1"), kv(51, "22"),
            kv( 78, RESET_ALL.ansiCodeSeq()),
            kv( 83, RESET_ALL.ansiCodeSeq()), kv(111, "32"), kv(124, "39"),
            kv(129, RESET_ALL.ansiCodeSeq()),
            kv(134, RESET_ALL.ansiCodeSeq()), kv(138, "32"), kv(153, "3;32"), kv(173, "3;39"), kv(202, "23"),
            kv(207, RESET_ALL.ansiCodeSeq()),
            kv(212, RESET_ALL.ansiCodeSeq()),
            kv(216, RESET_ALL.ansiCodeSeq()));
    }

    @Test void testColor256() {
        asList(
            "cyan", "fg(cyan)", "fg(#AA)",
            "bg(^yellow)", "bg(#FE)", "fg(#AA),bg(#FE)"
        ). forEach(fmtSt -> {
            String ansiTextFmt = format("@|%s %s|@", fmtSt, fmtSt);
            AnsiText ansiText = AnsiText.ansiText(ansiTextFmt);
            System.out.printf("%s ---> '%s'%n", "'" + ansiTextFmt + "'", ansiText.renderAnsi());
            System.out.println(ansiText.dump());
        });
    }

    private static String escapeJavaWithLS(AnsiText text) {
        return escapeJava(text.renderAnsi()).replaceAll("\\\\n", lineSeparator());
    }

    private static Map<Integer, String> escSeqByPos(String str) {
        Matcher m = Pattern.compile("\u001B\\[(.*?)m").matcher(str);
        return m.results().collect(toSortedMap(mr -> mr.start(1), mr -> mr.group(1)));
    }

    private static <K,V> Map.Entry<K,V> kv(K key, V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

//    public static void main(String[] args) {
//        System.out.println("This is \u001B[31mred fragment\u001B[39m of text");
//    }
//
    @Test void testHelloAnsi() {
        String prefixAnsi = "\u001b[90m testHelloAnsi(): \u001b[39m";
        System.out.println(prefixAnsi + "this is \u001b[31mthe red fragment\u001b[0m of text");
        System.out.println(prefixAnsi + "this is \u001B[1;34mthe blue and bold fragment\u001B[0m of text");
    }
}
