package org.krmdemo.yaml.reconcile.ansi;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.Family.ALL;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.lookupByName;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.lookupResetFamily;

/**
 * Text-style of ansi-text that is supported by most of ansi-terminals (including colors, intensity, etc...)
 */
@Slf4j
public class AnsiStyle {

    private static final AnsiStyle ROOT = new AnsiStyle();

    private static final String DELIMITER_ANSI_SEQ = ";";

    /**
     * An interface that holder of cascading ansi-style should implement
     */
    public interface Holder {
        /**
         * @return own style-property value
         */
        Optional<AnsiStyle> style();

        /**
         * @return outer component, which provide the default style
         */
        default Optional<Holder> parent() {
            return Optional.empty();
        }

        /**
         * @return a stream of styles that should be applied (from top-parent to this one EXCLUSIVE)
         */
        default Stream<AnsiStyle> parentChain() {
            return parent().stream().flatMap(Holder::styleChain);
        }

        /**
         * @return a stream of styles that should be applied (from top-parent to this one INCLUSIVE)
         */
        default Stream<AnsiStyle> styleChain() {
            return Stream.concat(parentChain(), style().stream());
        }

        default AnsiStyle parentStyle() {
            Builder builder = empty().builder();
            parentChain().flatMap(AnsiStyle::attrs).forEach(builder::accept);  // <-- think about reduce
            return builder.build();
        }
    }

    private final List<AnsiStyleAttr> attrs;  // <-- think about unmodifiable List

    private AnsiStyle() {
        this.attrs = emptyList();
    }

    private AnsiStyle(Stream<AnsiStyleAttr> attrsStream) {
        this.attrs = attrsStream.toList();
    }

    public boolean isEmpty() {
        return attrs.isEmpty();
    }

    public Stream<AnsiStyleAttr> attrs() {
        return attrs.stream();
    }

    public Stream<String> ansiCodeSeq() {
        return attrs().map(AnsiStyleAttr::ansiCodeSeq);
    }

    public String renderAnsi() {
        if (attrs.isEmpty()) {
            return "";
        } else {
            return format("\u001b[%sm", ansiCodeSeq().collect(joining(DELIMITER_ANSI_SEQ)));
        }
    }

    public Stream<String> attrsNames() {
        return attrs().map(AnsiStyleAttr::name);
    }

    public String dump() {
        if (attrs.isEmpty()) {
            return "ansi-style-empty";
        } else {
            return format("ansi-style<%s>", attrsNames().collect(joining(",")));
        }
    }

    @Override
    public String toString() {
        return dump();
    }

    public Builder builder() {
        return new Builder();
    }

    public static AnsiStyle empty() {
        return ROOT;
    }

    public class Builder {

        private final Map<AnsiStyleAttr.Family, AnsiStyleAttr> attrsMap = new TreeMap<>();

        private Builder() {
            attrs().forEach(this::accept);
        }

        public AnsiStyle build() {
            return new AnsiStyle(attrsMap.values().stream());
        }

        public Builder accept(AnsiStyleAttr styleAttr) {
            attrsMap.put(styleAttr.family(), styleAttr);
            return this;
        }

        public Builder acceptByName(String styleAttrName) {
            lookupByName(styleAttrName).ifPresent(this::accept);
            return this;
        }

        public Builder apply(AnsiStyle ansiStyleToApply) {
            ansiStyleToApply.attrs()
                .filter(attr -> attr.operation() == AnsiStyleAttr.Operation.apply)
                .forEach(this::accept);
            return this;
        }

        public Builder reset(AnsiStyle ansiStyleToReset) {
            ansiStyleToReset.attrs()
                .filter(attr -> attr.operation() == AnsiStyleAttr.Operation.apply)
                .flatMap(attr -> lookupResetFamily(attr.family()).stream())
                .forEach(this::accept);
            return this;
        }
    }
}
