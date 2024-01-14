package org.krmdemo.yaml.reconcile.impl;

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

    final List<YamlNode<Node>> childrenList;

    public YamlSequence(YamlNode<Node>... childrenArr) {
        this(Arrays.stream(childrenArr));
    }

    public YamlSequence(Stream<YamlNode<Node>> children) {
        List<YamlNode<Node>> yamlList = new ArrayList<>();
        List<Node> snakeList = new ArrayList<>();
        children.forEachOrdered(yamlNode -> {
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
        StringBuilder sb = new StringBuilder(format("%s(%X - %d elements):",
            getType(), identityHashCode(this), childrenList.size()));
        int maxNumLength = ("" + childrenList.size()).length();
        String fmt = "%n- %" + (maxNumLength + 2) + "s %s";
        for (int num = 0; num < childrenList.size(); num++) {
            YamlNode<Node> child = childrenList.get(num);
            String lineFeedWithSpaces = format("%n%" + (maxNumLength + 5) + "s", " ");
            sb.append(format(fmt, '(' + num + ')', child.toString().replaceAll("\\R", lineFeedWithSpaces)));
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
    public Stream<YamlNode<Node>> getChildren() {
        return childrenList.stream();
    }

    @Override
    public String getComment() {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
