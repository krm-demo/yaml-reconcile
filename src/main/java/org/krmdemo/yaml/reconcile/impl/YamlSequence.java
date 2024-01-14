package org.krmdemo.yaml.reconcile.impl;

import lombok.NonNull;
import org.krmdemo.yaml.reconcile.YamlNode;
import org.snakeyaml.engine.v2.api.RepresentToNode;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.SequenceNode;
import org.snakeyaml.engine.v2.nodes.Tag;

import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.identityHashCode;
import static java.util.Collections.unmodifiableList;

public class YamlSequence implements YamlNode<Node>, RepresentToNode {

    final SequenceNode sequence;

    final List<? extends YamlNode<Node>> childrenList;

    @SafeVarargs
    public YamlSequence(@NonNull YamlNode<Node>... childrenArr) {
        this(Arrays.stream(childrenArr));
    }

    public YamlSequence(Stream<YamlNode<Node>> children) {
        List<YamlNode<Node>> yamlList = new ArrayList<>();
        List<Node> snakeList = new ArrayList<>();
        children.forEachOrdered(yamlNode -> {
            if (yamlNode.getType() == Type.KEY_VALUE) {
                throw new IllegalArgumentException("key-value is not allowed in sequence");
            }
            yamlList.add(yamlNode);
            snakeList.add(yamlNode.asOrigin());
        });
        this.sequence = new SequenceNode(Tag.SEQ, snakeList, FlowStyle.BLOCK);
        this.childrenList = unmodifiableList(yamlList);
    }

    @Override
    public YamlNode.Type getType() {
        return Type.SEQUENCE;
    }

    @Override
    public Node asOrigin() {
        return sequence;
    }

    @Override
    public Node representData(@NonNull Object data) {
        Objects.requireNonNull(data, format("Representation data is null for %s", this));
        if (data != this) {
            throw new IllegalStateException(format(
                "Representation structure is corrupted: %s(0x%08x) != data(0x%08x)",
                getType(), identityHashCode(this), identityHashCode(data)));
        }
        return asOrigin();
    }

    @Override
    public String toString() {
        if (childrenList.isEmpty()) {
            return format("%s(0x%08x - empty)", getType(), identityHashCode(this));
        }
        StringBuilder sb = new StringBuilder(format("%s(0x%08x - %d elements):",
            getType(), identityHashCode(this), childrenList.size()));
        int maxNumLength = ("" + childrenList.size()).length();
        String fmt = "%n- %" + (maxNumLength + 2) + "s %s";
        for (int num = 0; num < childrenList.size(); num++) {
            String strNum = format("(%d)", num + 1);
            YamlNode<Node> child = childrenList.get(num);
            String lineFeedWithSpaces = format("%n%" + (maxNumLength + 5) + "s", " ");
            sb.append(format(fmt, strNum, child.toString().replaceAll("\\R", lineFeedWithSpaces)));
        }
        return sb.toString();
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException("no string representation for sequence");
    }

    @Override
    public String getKey() {
        throw new UnsupportedOperationException("no key in sequence");
    }

    @Override
    public YamlNode<Node> getValue() {
        throw new UnsupportedOperationException("no value in sequence");
    }

    @Override
    public YamlNode<Node> childByName(String childName) {
        throw new UnsupportedOperationException("no child by name in key-value");
    }

    @Override
    public YamlNode<Node> childByIndex(int index) {
        return childrenList.get(index);
    }

    @Override
    public Stream<? extends YamlNode<Node>> getChildren() {
        return childrenList.stream();
    }

    @Override
    public String getComment() {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
