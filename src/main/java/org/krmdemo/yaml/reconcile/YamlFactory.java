package org.krmdemo.yaml.reconcile;

import java.util.*;

public interface YamlFactory<OriginType> {

    YamlNode<OriginType> fromScalar(String scalarValue);

    YamlNode<OriginType> fromMap(Map<?,?> map);

    YamlNode<OriginType> fromIter(Iterable<?> iter);

    YamlNode<OriginType> fromAnnotated(Object annotated);
}
