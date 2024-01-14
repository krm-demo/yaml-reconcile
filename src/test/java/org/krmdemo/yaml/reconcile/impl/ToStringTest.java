package org.krmdemo.yaml.reconcile.impl;

import org.junit.jupiter.api.Test;
import org.krmdemo.yaml.reconcile.YamlNode;
import org.snakeyaml.engine.v2.nodes.Node;

import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static java.util.stream.IntStream.rangeClosed;
import static org.apache.commons.lang3.StringUtils.countMatches;
import static org.assertj.core.api.Assertions.assertThat;

public class ToStringTest {

    @Test
    void testScalar() {
        assertThat(maskId(scalar("la-la-la")))
            .isEqualTo("SCALAR(xxxx) --> 'la-la-la'");
        assertThat(maskId(scalar(123)))
            .isEqualTo("SCALAR(xxxx --> 123)");
        assertThat(maskId(scalar(Math.PI)))
            .isEqualTo("SCALAR(xxxx --> 3.141592653589793)");
        assertThat(maskId(scalar(true)))
            .isEqualTo("SCALAR(xxxx --> true)");
        assertThat(maskId(scalar("un-escaped tab - (\t)")))
            .isEqualTo("SCALAR(xxxx) --> 'un-escaped tab - (\t)'");
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
        YamlSequence seq103 = new YamlSequence(rangeClosed(1, 103).mapToObj(this::scalar));
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

    @Test
    void testSystemProperties() {
        System.setProperty("junit.test.class.simple-name", this.getClass().getSimpleName());
        System.setProperty("junit.test.math.E", "" + Math.E);
        String multiLineStr = """
            1.1. Goals
            The design goals for YAML are, in decreasing priority:
                        
            YAML is easily readable by humans.
            YAML data is portable between programming languages.
            YAML matches the native data structures of agile languages.
            YAML has a consistent model to support generic tools.
            YAML supports one-pass processing.
            YAML is expressive and extensible.
            YAML is easy to implement and use.
            """;
        System.setProperty("junit.test.multi-line", multiLineStr);
        Stream<YamlKeyValue> keyValueStream = System.getProperties().entrySet().stream()
            .map(e -> new YamlKeyValue("" + e.getKey(), scalar(e.getValue())));
        YamlDictionary dictSysProps = new YamlDictionary(keyValueStream);
        System.out.println(dictSysProps);
        System.out.println(dictSysProps.childByName("junit.test.multi-line").getValue().asString());

        assertThat(childValueStr(dictSysProps,"junit.test.class.simple-name")).isEqualTo("ToStringTest");
        assertThat(childValueStr(dictSysProps,"junit.test.math.E")).isEqualTo("2.718281828459045");

        YamlNode<Node> multiLineNode = dictSysProps.childByName("junit.test.multi-line").getValue();
        assertThat(countMatches("" + multiLineNode, lineSeparator())).isZero();
        assertThat(countMatches(multiLineNode.asString(), lineSeparator())).isEqualTo(10);
        assertThat(multiLineNode.asString()).isEqualTo(multiLineStr);
    }

    private YamlScalar scalar(Object scalarObj) {
        return new YamlScalar(scalarObj);
    }

    private static String childValueStr(YamlNode<Node> yamlNode, String childName) {
        return yamlNode.childByName(childName).getValue().asString();
    }

    private static String maskId(YamlNode<Node> yamlNode) {
        return yamlNode.toString().replaceAll("0x[0-9A-Fa-f]{8}", "xxxx");
    }

    private static String maskIdLit(YamlNode<Node> yamlNode) {
        return "|-\n" + maskId(yamlNode);
    }
}
