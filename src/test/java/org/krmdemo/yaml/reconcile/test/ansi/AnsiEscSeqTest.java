package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.krmdemo.yaml.reconcile.ansi.AnsiText;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.yaml.reconcile.ansi.AnsiRenderCtx.renderCtx;
import static org.krmdemo.yaml.reconcile.ansi.AnsiText.ansiText;
import static org.krmdemo.yaml.reconcile.test.ansi.AnsiTestUtils.escapeJavaWithLS;
import static org.krmdemo.yaml.reconcile.test.ansi.AnsiTestUtils.unescapeEsqSeq;

public class AnsiEscSeqTest {

    @BeforeEach
    void before() {
        renderCtx().setSiblingStylesSquash(false);
        renderCtx().setLinePrefixResetAll(false);
        renderCtx().setLineSuffixResetAll(false);
    }

    @DisplayName("output '@|' and '|@'")
    @Test void testAtSymbolWithDoublePipe() {
        System.out.println("no ansi-styles:");
        System.out.println(ansiText(">>> @|| <<<").renderAnsi());
        System.out.println(ansiText(">>> ||@ <<<").renderAnsi());
        System.out.println(ansiText(">>> @||la-la-la <<<").renderAnsi());
        System.out.println(ansiText(">>> la-la-la||@ <<<").renderAnsi());
        System.out.println(ansiText(">>> @||la-la-la||@ <<<").renderAnsi());

        System.out.println("ansi-styles (some foreground colors):");
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

    @ParameterizedTest
    @CsvSource(delimiterString = " ::: ", value = {
        ">>> reset-all \\u001b[0m <<<" +
            " ::: >>> reset-all  <<<" +
            " ::: >>> reset-all  <<<" +
            " ::: [ansi-style-empty, ansi-style<!!>]",
        ">>> apply-single \\u001b[2;5;3;4;1m <<<" +
            " ::: >>> apply-single  <<<" +
            " ::: >>> apply-single \\u001B[1;2;3;4;5m <<<\\u001B[22;22;23;24;24m" +
            " ::: [ansi-style-empty, ansi-style<bold,dim,italic,underline,blinking>]",
        ">>> apply-and-reset \\u001b[25;23;4;1;0;9m <<<" +
            " ::: >>> apply-and-reset  <<<" +
            " ::: >>> apply-and-reset \\u001B[1;3;4;5;9m <<<\\u001B[22;23;24;24;29m" +
            " ::: [ansi-style-empty, ansi-style<bold,italic,underline,blinking,strikethrough,!!>]",
//        "inputAnsiFmt ::: content ::: escWithLS ::: strSpanStyles",
    })
    void testSingleEscSeq(String inputAnsiFmt, String content, String escWithLS, String strSpanStyles) {
        AnsiText ansiTxt = AnsiText.ansiText(unescapeEsqSeq(inputAnsiFmt));
        System.out.println("inputEsqSeq : " + inputAnsiFmt);
        System.out.println("actualContent  : " + ansiTxt.content());
        System.out.println("actualDump  : " + ansiTxt.dump());
        System.out.println("escapeJavaWithLS(ansiTxt)  : " + escapeJavaWithLS(ansiTxt));
//        System.out.println("charSeq-before : " + inputAnsiFmt.chars().mapToObj(c -> format("0x%02x", c)).toList());
//        System.out.println("charSeq-after  : " + unescapeEsqSeq(inputAnsiFmt).chars().mapToObj(c -> format("0x%02x", c)).toList());
//        System.out.println("charSeq-renderAnsi  : " + ansiTxt.renderAnsi().chars().mapToObj(c -> format("0x%02x", c)).toList());
        assertThat(ansiTxt.content()).isEqualTo(content);
        assertThat(escapeJavaWithLS(ansiTxt)).isEqualTo(escWithLS);
        assertThat("" + ansiTxt.spanStyles().toList()).isEqualTo(strSpanStyles);
    }
}
