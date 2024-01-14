package org.krmdemo.yaml.reconcile.impl;

import lombok.NonNull;
import org.apache.commons.text.StringEscapeUtils;
import org.krmdemo.yaml.reconcile.YamlNode;
import org.snakeyaml.engine.v2.api.RepresentToNode;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.ScalarNode;
import org.snakeyaml.engine.v2.nodes.Tag;

import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.identityHashCode;

public class YamlScalar implements YamlNode<Node>, RepresentToNode {

    final ScalarNode scalar;

    final Object scalarObj;

    public YamlScalar(@NonNull Object scalarObj) {
        String scalarStr = StringEscapeUtils.escapeJava(scalarObj.toString());
        ScalarStyle scalarStyle = ScalarStyle.DOUBLE_QUOTED;
        if (scalarObj instanceof Number || scalarObj instanceof Boolean) {
            scalarStyle = ScalarStyle.PLAIN;
        }
        this.scalar = new ScalarNode(Tag.STR, scalarStr, scalarStyle);
        this.scalarObj = scalarObj;
    }

    @Override
    public Type getType() {
        return Type.SCALAR;
    }

    @Override
    public Node asOrigin() {
        return scalar;
    }

    @Override
    public Node representData(Object data) {
        Objects.requireNonNull(data, format("Representation data is null for %s", this));
        if (data != this) {
            throw new IllegalStateException(format(
                "Representation structure is corrupted: %s(%X) != data(%X)",
                getType(), identityHashCode(this), identityHashCode(data)));
        }
        return asOrigin();
    }

    @Override
    public String toString() {
        String fmt = switch(scalar.getScalarStyle()) {
            case ScalarStyle.PLAIN -> "%s(0x%08x --> %s)";
            case ScalarStyle.SINGLE_QUOTED -> "%s(0x%08x --> '%s')";
            case ScalarStyle.DOUBLE_QUOTED -> "%s(0x%08x --> \"%s\")";
            default -> "%s(0x%08x," + scalar.getScalarStyle().name() + ") --> %s";
        };
        return format(fmt, getType(), identityHashCode(this), asString());
    }

    @Override
    public String asString() {
        return scalar.getValue();
    }

    @Override
    public String getKey() {
        throw new UnsupportedOperationException("no key in " + getType());
    }

    @Override
    public YamlNode<Node> getValue() {
        throw new UnsupportedOperationException("no value in " + getType());
    }

    @Override
    public YamlNode<Node> childByName(String childName) {
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
