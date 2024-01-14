package org.krmdemo.yaml.reconcile.impl;

import org.junit.jupiter.api.Test;
import org.krmdemo.yaml.reconcile.YamlNode;
import org.snakeyaml.engine.v2.nodes.Node;

import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;

public class ToStringTest {

    @Test
    void testScalar() {
        assertThat(maskId(scalar("la-la-la")))
            .isEqualTo("SCALAR(xxxx --> \"la-la-la\")");
        assertThat(maskId(scalar(123)))
            .isEqualTo("SCALAR(xxxx --> 123)");
        assertThat(maskId(scalar(Math.PI)))
            .isEqualTo("SCALAR(xxxx --> 3.141592653589793)");
        assertThat(maskId(scalar(true)))
            .isEqualTo("SCALAR(xxxx --> true)");
    }

    @Test
    void testSequence() {
        YamlSequence seq3 = new YamlSequence(
            scalar(1), scalar(2), scalar(3)
        );
        assertThat(maskIdLit(seq3)).isEqualTo("""
            |-
            SEQUENCE(xxxx - 3 elements):
            - (1) SCALAR(xxxx --> 1)
            - (2) SCALAR(xxxx --> 2)
            - (3) SCALAR(xxxx --> 3)""");
        YamlSequence seq103 = new YamlSequence(rangeClosed(1, 103).mapToObj(ToStringTest::scalar));
        assertThat(maskIdLit(seq103)).startsWith("""
            |-
            SEQUENCE(xxxx - 103 elements):
            -   (1) SCALAR(xxxx --> 1)
            -   (2) SCALAR(xxxx --> 2)
            -   (3) SCALAR(xxxx --> 3)""");
        assertThat(maskIdLit(seq103)).contains("""
            -   (8) SCALAR(xxxx --> 8)
            -   (9) SCALAR(xxxx --> 9)
            -  (10) SCALAR(xxxx --> 10)""");
        assertThat(maskIdLit(seq103)).endsWith("""
            -  (98) SCALAR(xxxx --> 98)
            -  (99) SCALAR(xxxx --> 99)
            - (100) SCALAR(xxxx --> 100)
            - (101) SCALAR(xxxx --> 101)
            - (102) SCALAR(xxxx --> 102)
            - (103) SCALAR(xxxx --> 103)""");
    }

    private static YamlScalar scalar(Object scalarObj) {
        return new YamlScalar(scalarObj);
    }

    private static String maskId(YamlNode<Node> yamlNode) {
        return yamlNode.toString().replaceAll("0x[0-9A-Fa-f]{8}", "xxxx");
    }

    private static String maskIdLit(YamlNode<Node> yamlNode) {
        return "|-\n" + maskId(yamlNode);
    }
}
