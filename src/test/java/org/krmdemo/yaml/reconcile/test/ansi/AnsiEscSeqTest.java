package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.krmdemo.yaml.reconcile.ansi.AnsiText;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.yaml.reconcile.ansi.AnsiRenderCtx.renderCtx;
import static org.krmdemo.yaml.reconcile.ansi.AnsiText.ansiText;
import static org.krmdemo.yaml.reconcile.test.ansi.AnsiTestUtils.escapeJavaWithLS;
import static org.krmdemo.yaml.reconcile.test.ansi.AnsiTestUtils.unescapeEsqSeq;

/**
 * Unit-test to check the parsing of escape-sequences in {@link AnsiText}
 */
public class AnsiEscSeqTest {

    @BeforeEach
    void before() {
        renderCtx().setLinePrefixResetAll(false);
        renderCtx().setLineSuffixResetAll(false);
    }

    @DisplayName("output '@|' and '|@'")
    @Test void testAtSymbolWithDoublePipe() {
        System.out.println("------------------ no ansi-styles: ---------------------");
        System.out.println(ansiText(">>> @|| <<<").renderAnsi());
        System.out.println(ansiText(">>> ||@ <<<").renderAnsi());
        System.out.println(ansiText(">>> @||la-la-la <<<").renderAnsi());
        System.out.println(ansiText(">>> la-la-la||@ <<<").renderAnsi());
        System.out.println(ansiText(">>> @||la-la-la||@ <<<").renderAnsi());

        System.out.println("-------- ansi-styles (some foreground colors): ---------");
        System.out.println(ansiText(">>> @|red;@|||@ <<<").renderAnsi());
        System.out.println(ansiText(">>> @|blue;||@|@ <<<").renderAnsi());
        System.out.println(ansiText(">>> @|red;@||la-la-la|@ <<<").renderAnsi());
        System.out.println(ansiText(">>> @|blue;la-la-la||@|@ <<<").renderAnsi());
        System.out.println(ansiText(">>> @|magenta;@||la-la-la||@|@ <<<").renderAnsi());

        assertThat(ansiText(">>> @|| <<<").content()).isEqualTo(">>> @| <<<");
        assertThat(ansiText(">>> ||@ <<<").content()).isEqualTo(">>> |@ <<<");
        assertThat(ansiText(">>> @||la-la-la <<<").content()).isEqualTo(">>> @|la-la-la <<<");
        assertThat(ansiText(">>> la-la-la||@ <<<").content()).isEqualTo(">>> la-la-la|@ <<<");
        assertThat(ansiText(">>> @||la-la-la||@ <<<").content()).isEqualTo(">>> @|la-la-la|@ <<<");

        assertThat(ansiText(">>> @|red;@|||@ <<<").content()).isEqualTo(">>> @| <<<");
        assertThat(ansiText(">>> @|blue;||@|@ <<<").content()).isEqualTo(">>> |@ <<<");
        assertThat(ansiText(">>> @|red;@||la-la-la|@ <<<").content()).isEqualTo(">>> @|la-la-la <<<");
        assertThat(ansiText(">>> @|blue;la-la-la||@|@ <<<").content()).isEqualTo(">>> la-la-la|@ <<<");
        assertThat(ansiText(">>> @|magenta;@||la-la-la||@|@ <<<").content()).isEqualTo(">>> @|la-la-la|@ <<<");
    }

    @ParameterizedTest(name = "[{index}] {1}")
    @CsvSource(delimiterString = " ::: ", value = {
        ">>> reset-all \\u001b[0m <<<" +
            " ::: >>> reset-all  <<<" +
            " ::: >>> reset-all  <<<" +
            " ::: [ansi-style-empty, ansi-style<!!>]",
        ">>> apply-single \\u001b[2;5;3;4;1m <<<" +
            " ::: >>> apply-single  <<<" +
            " ::: >>> apply-single \\u001B[1;2;3;4;5m <<<\\u001B[22;22;23;24;25m" +
            " ::: [ansi-style-empty, ansi-style<bold,dim,italic,underline,blinking>]",
        ">>> apply-and-reset \\u001b[25;23;4;1;9m <<<" +
            " ::: >>> apply-and-reset  <<<" +
            " ::: >>> apply-and-reset \\u001B[1;4;9m <<<\\u001B[22;24;29m" +
            " ::: [ansi-style-empty, ansi-style<bold,!italic,underline,!blinking,strikethrough>]",
//        "inputAnsiFmt ::: content ::: escWithLS ::: strSpanStyles",
    })
    void testSingleEscSeq(String inputAnsiFmt, String content, String escWithLS, String strSpanStyles) {
        AnsiText ansiTxt = AnsiText.ansiText(unescapeEsqSeq(inputAnsiFmt));
        System.out.println("--------------------------------------------------------");
        System.out.println("inputEsqSeq    : " + inputAnsiFmt);
        System.out.println("actualContent  : " + ansiTxt.content());
        System.out.println(ansiTxt.dump());
        System.out.println("escapeJavaWithLS(ansiTxt)  : " + escapeJavaWithLS(ansiTxt));
//        System.out.println("charSeq-before : " + inputAnsiFmt.chars().mapToObj(c -> format("0x%02x", c)).toList());
//        System.out.println("charSeq-after  : " + unescapeEsqSeq(inputAnsiFmt).chars().mapToObj(c -> format("0x%02x", c)).toList());
//        System.out.println("charSeq-renderAnsi  : " + ansiTxt.renderAnsi().chars().mapToObj(c -> format("0x%02x", c)).toList());
        assertThat(ansiTxt.content()).isEqualTo(content);
        assertThat(escapeJavaWithLS(ansiTxt)).isEqualTo(escWithLS);
        assertThat("" + ansiTxt.spanStyles().toList()).isEqualTo(strSpanStyles);
    }

    @ParameterizedTest(name = "[{index}] {1}")
    @CsvSource(delimiterString = " ::: ", value = {
        ">>> \\u001b[31m'red'\\u001b[39m <<<" +
            " ::: >>> 'red' <<<" +
            " ::: >>> \\u001B[31m'red'\\u001B[39m <<<" +
            " ::: [ansi-style-empty, ansi-style<fg(red)>, ansi-style<!fg>]",
        ">>> \\u001b[38;5;1m'#01'\\u001b[39m <<<" +
            " ::: >>> '#01' <<<" +
            " ::: >>> \\u001B[38;5;1m'#01'\\u001B[39m <<<" +
            " ::: [ansi-style-empty, ansi-style<fg(#01)>, ansi-style<!fg>]",
        ">>> \\u001b[47m'on grey'\\u001b[49m <<<" +
            " ::: >>> 'on grey' <<<" +
            " ::: >>> \\u001B[47m'on grey'\\u001B[49m <<<" +
            " ::: [ansi-style-empty, ansi-style<bg(white)>, ansi-style<!bg>]",
        ">>> \\u001b[48;5;251m'on #FB'\\u001b[49m <<<" +
            " ::: >>> 'on #FB' <<<" +
            " ::: >>> \\u001B[48;5;251m'on #FB'\\u001B[49m <<<" +
            " ::: [ansi-style-empty, ansi-style<bg(#FB)>, ansi-style<!bg>]",
        ">>> \\u001b[91;107m'bright red on light grey'\\u001b[39;49m <<<" +
            " ::: >>> 'bright red on light grey' <<<" +
            " ::: >>> \\u001B[91;107m'bright red on light grey'\\u001B[39;49m <<<" +
            " ::: [ansi-style-empty, ansi-style<fg(^red),bg(^white)>, ansi-style<!fg,!bg>]",
        ">>> \\u001b[38;2;220;120;20;1;48;2;170;210;240m'#DC7814 on #AAD2F0'\\u001b[0m <<<" +
            " ::: >>> '#DC7814 on #AAD2F0' <<<" +
            " ::: >>> \\u001B[1;38;2;220;120;20;48;2;170;210;240m'#DC7814 on #AAD2F0'\\u001B[22;39;49m <<<" +
            " ::: [ansi-style-empty, ansi-style<bold,fg(#DC7814),bg(#AAD2F0)>, ansi-style<!!>]",
//        "inputAnsiFmt ::: content ::: escWithLS ::: strSpanStyles",
    })
    void testColorEscSeq(String inputAnsiFmt, String content, String escWithLS, String strSpanStyles) {
        AnsiText ansiTxt = AnsiText.ansiText(unescapeEsqSeq(inputAnsiFmt));
        System.out.println("--------------------------------------------------------");
        System.out.printf("%s : '%s'%n", "inputEsqSeq", inputAnsiFmt);
        System.out.printf("%s : '%s'%n", "ansiTxt.renderAnsi()", ansiTxt.renderAnsi());
        System.out.printf("%s : '%s'%n", "escapeJavaWithLS(ansiTxt)", escapeJavaWithLS(ansiTxt));
        System.out.println(ansiTxt.dump());
//        System.out.println("charSeq-before : " + inputAnsiFmt.chars().mapToObj(c -> format("0x%02x", c)).toList());
//        System.out.println("charSeq-after  : " + unescapeEsqSeq(inputAnsiFmt).chars().mapToObj(c -> format("0x%02x", c)).toList());
//        System.out.println("charSeq-renderAnsi  : " + ansiTxt.renderAnsi().chars().mapToObj(c -> format("0x%02x", c)).toList());
        assertThat(ansiTxt.content()).isEqualTo(content);
        assertThat(escapeJavaWithLS(ansiTxt)).isEqualTo(escWithLS);
        assertThat("" + ansiTxt.spanStyles().toList()).isEqualTo(strSpanStyles);
    }
}
