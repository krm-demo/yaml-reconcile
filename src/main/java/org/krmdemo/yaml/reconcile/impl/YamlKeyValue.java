package org.krmdemo.yaml.reconcile.impl;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.krmdemo.yaml.reconcile.YamlNode;
import org.snakeyaml.engine.v2.api.RepresentToNode;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeTuple;
import org.snakeyaml.engine.v2.nodes.ScalarNode;
import org.snakeyaml.engine.v2.nodes.Tag;

import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.identityHashCode;

public class YamlKeyValue implements YamlNode<Node>, RepresentToNode {

    final NodeTuple tuple;

    final YamlNode<Node> valueNode;

    public YamlKeyValue(@NonNull String key, @NonNull YamlNode<Node> valueNode) {
        if (StringUtils.isBlank(key) || !key.equals(StringEscapeUtils.escapeJava(key))) {
            throw new IllegalArgumentException(format("invalid key (blank or contains invalid symbols) - '%s'", key));
        }
        if (valueNode.getType() == Type.KEY_VALUE) {
            throw new IllegalArgumentException("nested key-value are not supported");
        }
        ScalarNode scalarNode = new ScalarNode(Tag.STR, key, ScalarStyle.SINGLE_QUOTED);
        this.tuple = new NodeTuple(scalarNode, valueNode.asOrigin());
        this.valueNode = valueNode;
    }

    @Override
    public Type getType() {
        return Type.KEY_VALUE;
    }

    @Override
    public Node asOrigin() {
        throw new UnsupportedOperationException("no origin for " + getType());
    }

    public NodeTuple asTuple() {
        return tuple;
    }

    @Override
    public Node representData(@NonNull Object data) {
        throw new UnsupportedOperationException("must not be invoked during representation of " + getType());
    }

    @Override
    public String toString() {
        return toString(getKey().length());
    }

    public String toString(int maxKeyLength) {
        String fmt = "%s(0x%08x) ? %" + (maxKeyLength + 2) + "s : %s";
        String lineFeedWithSpaces = format("%n%" + (maxKeyLength + 5) + "s", " ");  // TODO: '5' is not precise value
        String str = getValue().toString().replaceAll("\\R", lineFeedWithSpaces);
        return format(fmt, getType(), identityHashCode(this), "'" + getKey() + "'", str);
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException("no string representation for " + getType());
    }

    @Override
    public String getKey() {
        Node keyNode = tuple.getKeyNode();
        if (keyNode instanceof ScalarNode scalarNode) {
            return scalarNode.getValue();
        }
        throw new IllegalStateException("invalid type of key in key-value - " + keyNode.getClass());
    }

    @Override
    public YamlNode<Node> getValue() {
        return valueNode;
    }

    @Override
    public YamlNode<Node> childByName(@NonNull String childName) {
        throw new UnsupportedOperationException("no child by name in " + getType());
    }

    @Override
    public YamlNode<Node> childByIndex(int index) {
        throw new UnsupportedOperationException("no child by index in " + getType());
    }

    @Override
    public Stream<YamlNode<Node>> getChildren() {
        throw new UnsupportedOperationException("no children in " + getType());
    }

    @Override
    public String getComment() {
        throw new UnsupportedOperationException("not implemented yet for " + getType());
    }
}
