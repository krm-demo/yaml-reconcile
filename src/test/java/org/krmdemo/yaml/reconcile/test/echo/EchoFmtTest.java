package org.krmdemo.yaml.reconcile.test.echo;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.summarizingDouble;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.apache.commons.text.StringEscapeUtils.escapeJava;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("UnnecessaryUnicodeEscape")
public class EchoFmtTest {

    @Test
    void testEmpty() {
        assertThrows(IllegalArgumentException.class, EchoFmt::main);
    }

    EchoFmt echoFmt = new EchoFmt();

    @Test
    void testSingleArg() {
        assertThat(echoFmt.apply("la-la-la")).isEqualTo("la-la-la");

        dumpCodePointInfo(0x1F4A9);
        System.out.println("3 times example --> '\uD83D\uDCA9'-'\uD83D\uDCA9'-'\uD83D\uDCA9'");
        System.out.println();

        dumpCodePointInfo(0x2504);
        dumpCodePointInfo(0x2505);
        dumpCodePointInfo(0x2506);
        System.out.println("1) all together times example --> '\u2504\u2504\u2504\u2504\u2504'");
        System.out.println("2) all together times example --> '\u2505\u2505\u2505\u2505\u2505'");
        System.out.println("3) all together times example --> '\u2506\u2506\u2506\u2506\u2506'");
        System.out.println();

        dumpCodePointInfo(0x0026A8);
        dumpCodePointInfo(0x0026A9);
        dumpCodePointInfo(0x0026AA);

        System.out.println(repeat("-", 20));
        System.out.printf("|--%s]\t---[%1$s]-----|%n", "A");
        System.out.printf("|--%s]\t---[%1$s]-----|%n", "┆");
        System.out.printf("|--%s]\t---[%1$s]-----|%n", Character.toString(0x0026A8));
        System.out.printf("|--%s]\t---[%1$s]----|%n", Character.toString(0x0026A9));
        System.out.printf("|--%s]\t---[%1$s]-----|%n", Character.toString(0x0026A9));
        System.out.printf("|--%s]\t---[%1$s]----|%n", Character.toString(0x0026AA));
        System.out.println(repeat("-", 20));
    }

    private static void dumpCodePointInfo(int codePoint) {
        String codePointStr = Character.toString(codePoint);
        System.out.printf("\\U+%x --> \\U%08x --> [%s] (%d) '%s' --> '%s' = '%s' : '%s', \"%s\", %s%n",
            codePoint, codePoint,
            codePointStr, codePointStr.codePointCount(0, codePointStr.length()), //length(codePointStr),
            escapeJava(codePointStr),
            forBashEchoX(codePoint), forBashEchoO(codePoint),
            Character.getName(codePoint),
            Character.UnicodeBlock.of(codePoint),
            Character.UnicodeScript.of(codePoint));
    }

    private static Stream<Byte> stream(byte[] bytes) {
        Stream.Builder<Byte> streamBuilder = Stream.builder();
        for (byte b : bytes) streamBuilder.accept(b);
        return streamBuilder.build();
    }

    private static Stream<Byte> stream(String str) {
        return stream(str.getBytes(StandardCharsets.UTF_8));
    }

    private static String forBashEchoX(int codePoint) {
        return stream(Character.toString(codePoint)).map(b -> format("\\x%02x", b)).collect(joining());
    }

    private static String forBashEchoO(int codePoint) {
        return stream(Character.toString(codePoint)).map(b -> format("\\%03o", b)).collect(joining());
    }

    private static int length(String str) {
        String a = "\uD83C\uDDE6";
        String z = "\uD83C\uDDFF";

        Pattern p = Pattern.compile("[" + a + "-" + z + "]{2}");
        Matcher m = p.matcher(str);
        int count = 0;
        while (m.find()) {
            count++;
        }
        return str.codePointCount(0, str.length()) - count;
    }
}
