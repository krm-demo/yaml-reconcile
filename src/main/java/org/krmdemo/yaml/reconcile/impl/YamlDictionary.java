package org.krmdemo.yaml.reconcile.impl;

import org.krmdemo.yaml.reconcile.YamlNode;
import org.snakeyaml.engine.v2.api.RepresentToNode;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeTuple;
import org.snakeyaml.engine.v2.nodes.Tag;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.identityHashCode;
import static java.util.Collections.unmodifiableMap;

public class YamlDictionary implements YamlNode<Node>, RepresentToNode {

    final MappingNode mapping;

    final Map<String, YamlKeyValue> childrenMap;

    private int maxKeyLength = 0;

    public YamlDictionary(YamlKeyValue... childrenArr) {
        this(Arrays.stream(childrenArr));
    }

    public YamlDictionary(Stream<YamlKeyValue> children) {
        SortedMap<String, YamlKeyValue> yamlMap = new TreeMap<>();
        List<NodeTuple> tuples = new ArrayList<>();
        children.forEachOrdered(keyValue -> {
            yamlMap.put(keyValue.getKey(), keyValue);
            tuples.add(keyValue.asTuple());
            maxKeyLength = Math.max(maxKeyLength, keyValue.getKey().length());
        });
        this.mapping = new MappingNode(Tag.MAP, tuples, FlowStyle.BLOCK);;
        this.childrenMap = unmodifiableMap(yamlMap);
    }

    @Override
    public YamlNode.Type getType() {
        return Type.DICTIONARY;
    }

    @Override
    public Node asOrigin() {
        return mapping;
    }

    @Override
    public Node representData(Object data) {
        Objects.requireNonNull(data, format("Representation data is null for %s", this));
        if (data != this) {
            throw new IllegalStateException(format(
                "Representation structure is corrupted: this(%X) != data(%X)",
                identityHashCode(this), identityHashCode(data)));
        }
        return asOrigin();
    }

    @Override
    public String toString() {
        String header = format("%s(%X - %d elements):", getType(), identityHashCode(this), childrenMap.size());
        return childrenMap.values().stream()
            .map(keyValue -> keyValue.toString(maxKeyLength))
            .collect(Collectors.joining("%n- ", header, ""));
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException("no string representation for " + getType());
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
        return childrenMap.get(childName);
    }

    @Override
    public YamlNode<Node> childByIndex(int index) {
        throw new UnsupportedOperationException("no child by index in " + getType());
    }

    @Override
    public Stream<? extends YamlNode<Node>> getChildren() {
        return childrenMap.values().stream();
    }

    @Override
    public String getComment() {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
