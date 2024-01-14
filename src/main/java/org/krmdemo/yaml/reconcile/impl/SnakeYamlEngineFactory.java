package org.krmdemo.yaml.reconcile.impl;

import org.krmdemo.yaml.reconcile.YamlFactory;
import org.krmdemo.yaml.reconcile.YamlNode;
import org.snakeyaml.engine.v2.nodes.Node;

import java.util.*;

public class SnakeYamlEngineFactory implements YamlFactory<Node> {

    @Override
    public YamlNode<Node> fromScalar(String scalarValue) {
        return null;
    }

    @Override
    public YamlNode<Node> fromMap(Map<?, ?> map) {
        return null;
    }

    @Override
    public YamlNode<Node> fromIter(Iterable<?> iter) {
        return null;
    }

    @Override
    public YamlNode<Node> fromAnnotated(Object annotated) {
        return null;
    }
}
