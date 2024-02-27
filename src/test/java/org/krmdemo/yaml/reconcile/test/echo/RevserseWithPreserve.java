package org.krmdemo.yaml.reconcile.test.echo;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class RevserseWithPreserve {

    private String reverse(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = str.length() - 1; i >= 0; i--) {
            sb.append(str.charAt(i));
        }
        return sb.toString();
    }

    String process(String str, char charToPreserve) {
        List<Integer> posToPreserve = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == charToPreserve) {
                posToPreserve.add(i);
            } else {
                sb.append(ch);
            }
        }
        StringBuilder reversed = new StringBuilder(reverse(sb.toString()));
        for (int pos : posToPreserve) {
            reversed.insert(pos, charToPreserve);
        }
        return reversed.toString();
    }

    @Test
    void test() {
        assertThat(process("abcdef.gh", '.')).isEqualTo("hgfedc.ba");
        assertThat(process("a.bc.def.gh", '.')).isEqualTo("h.gf.edc.ba");
        assertThat(process("a.bc.defgh", '.')).isEqualTo("h.gf.edcba");
    }
}
