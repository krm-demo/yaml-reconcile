package org.krmdemo.yaml.reconcile.ansi;

import java.util.*;

import static java.util.Collections.emptySet;

public class CrossSubst {

    enum CrossEnd {
            UP,
        LEFT, RIGHT,
            DOWN
    }

    enum CrossKind {
        NONE(null, null),
        SINGLE(Set.of('─'), Set.of('│')),
        HEAVY(Set.of('─'), Set.of('│')),
        DOUBLE(Set.of('─'), Set.of('│'));

        private final Set<Character> horizontal;
        private final Set<Character> vertical;

        CrossKind(Set<Character> horizontal, Set<Character> vertical) {
            this.horizontal = horizontal;
            this.vertical = vertical;
        }
    }
}
