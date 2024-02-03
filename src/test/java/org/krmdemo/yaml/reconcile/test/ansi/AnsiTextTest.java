package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.Test;
import org.krmdemo.yaml.reconcile.ansi.AnsiText;

import static java.lang.System.lineSeparator;
import static org.apache.commons.text.StringEscapeUtils.escapeJava;
import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.yaml.reconcile.ansi.AnsiRenderCtx.renderCtx;

public class AnsiTextTest {

    @Test
    void testSimpleStyles() {
        String threeLines = """
              the first line 1 with 2 leading spaces
            this is '@|red the red fragment|@' without leading space or semicolon;
            this is '@|red;the same red one |@'with neither leading space nor semicolon again;
            this is '@|red,bold; red and bold fragment|@' with leading space;
            and '@|underline,blue ;underline and blue fragment|@' fragment with leading semicolon;
            """;
        System.out.println(threeLines);
        AnsiText ansiText = AnsiText.ansiText(threeLines);
        System.out.println("========== dump lines: ==========================================");
        System.out.println(ansiText.dump());

        renderCtx().setSiblingStylesSquash(true);
        System.out.println("renderCtx() --> " + renderCtx());
        System.out.println("========== renderAnsi (squash sibling styles): ==================");
        System.out.println(ansiText.renderAnsi());
        System.out.println("========== renderAnsi (squash sibling styles - escaped): ========");
        System.out.println(escapeJavaWithLS(ansiText));
        assertThat(ansiText.renderAnsi()).as("renderAnsi WITH squash sibling styles").isEqualTo("""
              the first line 1 with 2 leading spaces
            this is '\u001B[31mthe red fragment\u001B[39m' without leading space or semicolon;
            this is '\u001B[31mthe same red one \u001B[39m'with neither leading space nor semicolon again;
            this is '\u001B[1;31m red and bold fragment\u001B[22;39m' with leading space;
            and '\u001B[4;34m;underline and blue fragment\u001B[24;39m' fragment with leading semicolon;
            """);

        renderCtx().setSiblingStylesSquash(false);
        System.out.println("renderCtx() --> " + renderCtx());
        System.out.println("========== renderAnsi (no squash sibling styles): ==================");
        System.out.println(ansiText.renderAnsi());
        System.out.println("========== renderAnsi (no squash sibling styles - escaped): ========");
        System.out.println(escapeJavaWithLS(ansiText));
        assertThat(ansiText.renderAnsi()).as("renderAnsi WITHOUT squash sibling styles").isEqualTo("""
              the first line 1 with 2 leading spaces
            this is '\u001B[31mthe red fragment\u001B[39m' without leading space or semicolon;
            this is '\u001B[31mthe same red one \u001B[39m'with neither leading space nor semicolon again;
            this is '\u001B[1;31m red and bold fragment\u001B[22;39m' with leading space;
            and '\u001B[4;34m;underline and blue fragment\u001B[24;39m' fragment with leading semicolon;
            """);
    }

    private static String escapeJavaWithLS(AnsiText text) {
        return escapeJava(text.renderAnsi()).replaceAll("\\\\n", lineSeparator());
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
