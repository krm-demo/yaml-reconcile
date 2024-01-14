package org.krmdemo.yaml.reconcile;

import java.util.stream.Stream;

public interface YamlNode<OriginType> {

    enum Type {
        SCALAR,

        SCALAR_CSV,

        KEY_VALUE,

        SEQUENCE,

        DICTIONARY,

        ROOT
    }

    Type getType();

    OriginType asOrigin();

    String asString();

    String getKey();

    YamlNode<OriginType> getValue();

    YamlNode<OriginType> childByName(String childName);

    YamlNode<OriginType> childByIndex(int index);

    Stream<? extends YamlNode<OriginType>> getChildren();

    String getComment();
}
