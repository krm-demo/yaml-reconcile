package org.krmdemo.yaml.reconcile.ansi;

import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.joining;

/**
 * Text-style of ansi-text that is supported by most of ansi-terminals (including colors, intensity, etc...)
 */
public class AnsiStyle {

    private static final AnsiStyle ROOT = new AnsiStyle();

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
         * @return a stream of styles that should be applied (from top-parent to this one)
         */
        default Stream<AnsiStyle> styleChain() {
            Stream<AnsiStyle> parentChain = parent().stream().flatMap(Holder::styleChain);
            return Stream.concat(parentChain, style().stream());
        }
    }

    private final Map<AnsiStyleAttr.Family, AnsiStyleAttr> attrs;

    private AnsiStyle() {
        this.attrs = emptyMap();
    }

    private AnsiStyle(Map<AnsiStyleAttr.Family, AnsiStyleAttr> attrs) {
        this.attrs = unmodifiableMap(attrs);
    }

    public Stream<AnsiStyleAttr> attrs() {
        return attrs.values().stream().sorted();
    }

    public Stream<String> ansiCodeSeq() {
        return attrs().map(AnsiStyleAttr::ansiCodeSeq);
    }

    public String renderAnsi() {
        if (attrs.isEmpty()) {
            return "";
        } else {
            return format("\u001b[%s;m", ansiCodeSeq().collect(joining(";")));
        }
    }

    public Stream<String> dumpSeq() {
        return attrs().map(AnsiStyleAttr::dump);
    }

    public String dump() {
        if (attrs.isEmpty()) {
            return "ansi-style-empty";
        } else {
            return format("ansi-style<%s>", dumpSeq().collect(joining(",")));
        }
    }

    public Builder builder() {
        return new Builder();
    }

    public static AnsiStyle empty() {
        return ROOT;
    }

    public class Builder {

        private final Map<AnsiStyleAttr.Family, AnsiStyleAttr> attrsMap = new LinkedHashMap<>();

        private Builder() {
            attrs().forEach(this::accept);
        }

        public AnsiStyle build() {
            return new AnsiStyle(attrsMap);
        }

        public Builder accept(AnsiStyleAttr styleAttr) {
            attrsMap.put(styleAttr.family(), styleAttr);
            return this;
        }
    }
}
