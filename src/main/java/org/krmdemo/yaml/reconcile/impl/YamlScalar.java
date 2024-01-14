package org.krmdemo.yaml.reconcile.impl;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
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
import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.countMatches;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.text.StringEscapeUtils.escapeJava;
import static org.apache.commons.text.StringEscapeUtils.unescapeJava;

public class YamlScalar implements YamlNode<Node>, RepresentToNode {

    final ScalarNode scalar;

    final Object scalarObj;

    public YamlScalar(@NonNull Object scalarObj) {
        if (scalarObj instanceof YamlNode<?>) {
            throw new IllegalArgumentException("origin object must not be a yaml-node");
        }
        this.scalarObj = scalarObj;
        String scalarStr = scalarObj.toString();
        if (scalarObj instanceof Number || scalarObj instanceof Boolean) {
            this.scalar = new ScalarNode(Tag.STR, scalarStr, ScalarStyle.PLAIN);
        } else if (countMatches(scalarStr, lineSeparator()) > 0) {
            this.scalar = new ScalarNode(Tag.STR, escapeJava(scalarStr), ScalarStyle.DOUBLE_QUOTED);
        } else {
             this.scalar = new ScalarNode(Tag.STR, scalarStr, ScalarStyle.SINGLE_QUOTED);
         }
    }

    public YamlScalar(@NonNull ScalarNode scalarOriginal) {
        this.scalarObj = scalarOriginal;
        String scalarStr = scalarObj.toString();
        ScalarStyle scalarStyle = scalarOriginal.getScalarStyle();
        if (countMatches(scalarStr, lineSeparator()) > 0
                || scalarStyle == ScalarStyle.FOLDED
                || scalarStyle == ScalarStyle.LITERAL
                || scalarStyle == ScalarStyle.DOUBLE_QUOTED) {
            this.scalar = new ScalarNode(Tag.STR, escapeJava(scalarStr), ScalarStyle.DOUBLE_QUOTED);
        } else {
            this.scalar = new ScalarNode(Tag.STR, scalarStr, scalarStyle);
        }
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
        if (isEmpty(scalar.getValue())) {
            return format("%s(0x%08x)", getType(), identityHashCode(this));
        }
        ScalarStyle scalarStyle = scalar.getScalarStyle();
        String fmt = switch(scalar.getScalarStyle()) {
            case ScalarStyle.PLAIN -> "%s(0x%08x --> %s)";
            case ScalarStyle.SINGLE_QUOTED -> "%s(0x%08x) --> '%s'";
            case ScalarStyle.DOUBLE_QUOTED -> "%s(0x%08x) --> \"%s\"";
            default -> throw new IllegalStateException(format(
                "ScalarStyle was not properly detected for this(%s) - %s", identityHashCode(this), scalarStyle));
        };
        return format(fmt, getType(), identityHashCode(this), scalar.getValue());
    }

    @Override
    public String asString() {
        return scalar.getScalarStyle() == ScalarStyle.DOUBLE_QUOTED ?
            unescapeJava(scalar.getValue()) : scalar.getValue();
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
