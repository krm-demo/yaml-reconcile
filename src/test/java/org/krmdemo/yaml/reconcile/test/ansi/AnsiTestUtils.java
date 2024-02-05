package org.krmdemo.yaml.reconcile.test.ansi;

import org.krmdemo.yaml.reconcile.ansi.AnsiText;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.lineSeparator;
import static org.apache.commons.text.StringEscapeUtils.escapeJava;
import static org.krmdemo.yaml.reconcile.util.StreamUtils.toSortedMap;

/**
 * Utility class for unit-tests in "org.krmdemo.yaml.reconcile.test.ansi" package
 */
class AnsiTestUtils {

    static final Pattern PATTERN_ESQ_SEQ = Pattern.compile("\u001B\\[(.*?)m");

    static String unescapeEsqSeq(String text) {
        return escapeJava(text).replaceAll("\\\\\\\\u001[bB]\\[", "\u001B[");
    }
    static String escapeJavaWithLS(AnsiText ansiTxt) {
        return escapeJava(ansiTxt.renderAnsi()).replaceAll("\\\\n", lineSeparator());
    }

    static Map<Integer, String> escSeqByPos(String str) {
        Matcher mtch = PATTERN_ESQ_SEQ.matcher(str);
        return mtch.results().collect(toSortedMap(mr -> mr.start(1), mr -> mr.group(1)));
    }

    static <K,V> Map.Entry<K,V> kv(K key, V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    /**
     * Prohibit the instantiation of utility-class
     */
    private AnsiTestUtils() {
        throw new UnsupportedOperationException();
    }
}
