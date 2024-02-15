package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.krmdemo.yaml.reconcile.ansi.AnsiLine;
import org.krmdemo.yaml.reconcile.ansi.AnsiStyle;
import org.krmdemo.yaml.reconcile.ansi.AnsiText;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.yaml.reconcile.ansi.AnsiRenderCtx.renderCtx;
import static org.krmdemo.yaml.reconcile.test.ansi.AnsiTestUtils.escSeqByPos;
import static org.krmdemo.yaml.reconcile.test.ansi.AnsiTestUtils.escapeJavaWithLS;
import static org.krmdemo.yaml.reconcile.test.ansi.AnsiTestUtils.kv;

public class AnsiTextTest {

    @Test
    void testSimpleStyles() {
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
            \u001B[32mcontinues \u001B[39m\u001B[3;32mat this line \u001B[23;39m\u001B[3mand ends without color\u001B[23m
            """);
        assertThat(escSeqByPos(ansiText.renderAnsi()).entrySet()).containsExactly(
            kv(2, "1"), kv(47, "22"),
            kv(99, "32"), kv(112, "39"),
            kv(118, "32"), kv(133, "39"), kv(138, "3;32"), kv(158, "23;39"), kv(166, "3"), kv(192, "23"));
//            kv(2, "1"), kv(47, "22"),
//            kv(99, "32"), kv(112, "39"),
//            kv(118, "32"), kv(133, "3;32"), kv(153, "3;39"), kv(182, "23"));
//
//        System.out.println("========= switching off sibling-styles-squash =======");
//        renderCtx().setSiblingStylesSquash(false);
//        System.out.println();
//
//        System.out.println("-------- ansiText.renderAnsi(): ------------------");
//        System.out.println(ansiText.renderAnsi());
//        System.out.println("---- escapeJavaWithLS(ansiText.renderAnsi()): ----");
//        System.out.println("renderCtx() --> " + renderCtx());
//        System.out.println(escapeJavaWithLS(ansiText));
//        assertThat(escSeqByPos(ansiText.renderAnsi()).entrySet()).containsExactly(
//            kv(2, "1"), kv(47, "22"),
//            kv(99, "32"), kv(112, "39"),
//            kv(118, "32"), kv(133, "39"), kv(138, "3;32"), kv(158, "23;39"), kv(166, "3"), kv(192, "23"));
//
//        System.out.println("========= switching on line-prefix-reset-all =======");
//        renderCtx().setSiblingStylesSquash(true);
//        renderCtx().setLinePrefixResetAll(true);
//        System.out.println();
//
//        System.out.println("-------- ansiText.renderAnsi(): ------------------");
//        System.out.println(ansiText.renderAnsi());
//        System.out.println("---- escapeJavaWithLS(ansiText.renderAnsi()): ----");
//        System.out.println("renderCtx() --> " + renderCtx());
//        System.out.println(escapeJavaWithLS(ansiText));
//        assertThat(escSeqByPos(ansiText.renderAnsi()).entrySet()).containsExactly(
//            kv(  2, RESET_ALL.ansiCodeSeq()), kv(6, "1"), kv(51, "22"),
//            kv( 79, RESET_ALL.ansiCodeSeq()), kv(107, "32"), kv(120, "39"),
//            kv(126, RESET_ALL.ansiCodeSeq()), kv(130, "32"), kv(145, "3;32"), kv(165, "3;39"), kv(194, "23"),
//            kv(200, RESET_ALL.ansiCodeSeq()));
//
//        System.out.println("========= switching on line-suffix-reset-all =======");
//        renderCtx().setLineSuffixResetAll(true);
//        System.out.println();
//
//        System.out.println("-------- ansiText.renderAnsi(): ------------------");
//        System.out.println(ansiText.renderAnsi());
//        System.out.println("---- escapeJavaWithLS(ansiText.renderAnsi()): ----");
//        System.out.println("renderCtx() --> " + renderCtx());
//        System.out.println(escapeJavaWithLS(ansiText));
//        assertThat(escSeqByPos(ansiText.renderAnsi()).entrySet()).containsExactly(
//            kv(  2, RESET_ALL.ansiCodeSeq()), kv(6, "1"), kv(51, "22"),
//            kv( 78, RESET_ALL.ansiCodeSeq()),
//            kv( 83, RESET_ALL.ansiCodeSeq()), kv(111, "32"), kv(124, "39"),
//            kv(129, RESET_ALL.ansiCodeSeq()),
//            kv(134, RESET_ALL.ansiCodeSeq()), kv(138, "32"), kv(153, "3;32"), kv(173, "3;39"), kv(202, "23"),
//            kv(207, RESET_ALL.ansiCodeSeq()),
//            kv(212, RESET_ALL.ansiCodeSeq()),
//            kv(216, RESET_ALL.ansiCodeSeq()));
    }

    @Test
    void testColor256() {
        asList(
            "fg(cyan)", "fg(#AA)", "bg(#FE)",
            "bg(^yellow)", "fg(#AA),bg(#FE)"
        ). forEach(fmtSt -> {
            String ansiTextFmt = format("@|%s %s|@", fmtSt, fmtSt);
            AnsiText ansiText = AnsiText.ansiText(ansiTextFmt);
            System.out.printf("%s ---> '%s'%n", "'" + ansiTextFmt + "'", ansiText.renderAnsi());
            assertThat(ansiText.spanStyles().map(AnsiStyle::dump).toList())
                .containsExactly(format("ansi-style<%s>", fmtSt));
        });
    }

    @Test
    void testColorRGB() {
        asList(
            "fg(#FF0000)", "fg(#00FF00)", "fg(#0000FF)",
            "fg(#56789A)", "fg(#123DEF)",
            "bg(#ABCDEF)", "bold,bg(#ABCDEF)", "fg(magenta),bg(#ABCDEF)"
        ). forEach(fmtSt -> {
            String ansiTextFmt = format("@|%s %s|@", fmtSt, fmtSt);
            AnsiText ansiText = AnsiText.ansiText(ansiTextFmt);
            System.out.printf("%s ---> '%s'%n", "'" + ansiTextFmt + "'", ansiText.renderAnsi());
            assertThat(ansiText.spanStyles().map(AnsiStyle::dump).toList())
                .containsExactly(format("ansi-style<%s>", fmtSt));
        });
    }


    @ParameterizedTest(name = "[{index}] {0}")
    @CsvSource(delimiterString = " ::: ", value = {
        "@|cyan ::: @|fg(cyan) ::: some cyan fragment" +
            " ::: [ansi-style<fg(cyan)>, ansi-style-empty, ansi-style<fg(cyan)>]",
        "@|^red ::: @|fg(^red) ::: some bright red fragment" +
            " ::: [ansi-style<fg(^red)>, ansi-style-empty, ansi-style<fg(^red)>]",
        "@|bg(#FF),^green ::: @|fg(^green),bg(#FF) ::: some bright green on light grey" +
            " ::: [ansi-style<fg(^green),bg(#FF)>, ansi-style-empty, ansi-style<fg(^green),bg(#FF)>]",
        "@|bold,magenta,bg(#FAFA00) ::: @|bold,fg(magenta),bg(#FAFA00) ::: some bold magenta on yellow" +
            " ::: [ansi-style<bold,fg(magenta),bg(#FAFA00)>, ansi-style-empty, ansi-style<bold,fg(magenta),bg(#FAFA00)>]",
        "@|bg(#FAFA00),red,^green,!bg,blue ::: @|fg(blue) ::: final foreground is blue" +
            " ::: [ansi-style<fg(blue),!bg>, ansi-style-empty, ansi-style<fg(blue)>]",
    })
    void testImpliedForegroundColor(String fmtInput, String fmtEquiv, String content, String strSpanStyles) {
        String fmtInputInner = format("%s %s|@", fmtInput, content);
        String fmtInputOuter = format("%s %s|@", fmtInput, fmtInputInner.replaceAll("\\|", "||"));
        String fmtEquivInner = format("%s %s|@", fmtEquiv, content);
        String fmtEquivOuter = format("%s %s|@", fmtEquiv, fmtEquivInner.replaceAll("\\|", "||"));
        AnsiText ansiText = AnsiText.ansiText(format("%s is the same as %s", fmtInputOuter, fmtEquivOuter));
        System.out.println(ansiText.renderAnsi());
        assertThat(ansiText.content()).isEqualTo(
            format("%s %s|@ is the same as %s %s|@", fmtInput, content, fmtEquiv,content ));
        assertThat("" + ansiText.spanStyles().toList()).isEqualTo(strSpanStyles);
    }

//    public static void main(String[] args) {
//        System.out.println("This is \u001B[31mred fragment\u001B[39m of text");
//    }
//
    @Test void testHelloAnsi() {
        String prefixAnsi = "\u001b[90m testHelloAnsi(): \u001b[39m";
        System.out.println(prefixAnsi + "this is \u001b[31m'red fragment'\u001b[0m of text");
        System.out.println(prefixAnsi + "this is \u001B[1;34m'blue and bold fragment'\u001B[0m of text");
    }
}
