package org.krmdemo.yaml.reconcile.ansi;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.RESET_ALL;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.lookupByName;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.lookupResetFamily;

/**
 * Text-style of ansi-text that is supported by most of ansi-terminals (including colors, intensity, etc...)
 */
@Slf4j
public class AnsiStyle {

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
            return Stream.concat(parentChain(), style().stream()).filter(AnsiStyle::isNotEmpty);
        }

        default AnsiStyle parentStyle() {
            Builder builder = empty().builder();
            parentChain().flatMap(AnsiStyle::attrs).forEach(builder::accept);  // <-- think about reduce
            return builder.build();
        }

        default AnsiStyle styleOpen() {
            return style()
                .map(parentStyle().builder()::apply)
                .map(AnsiStyle.Builder::build)
                .orElse(empty());
        }

        default AnsiStyle styleClose() {
            return style()
                .map(parentStyle().builder()::reset)
                .map(AnsiStyle.Builder::build)
                .orElse(empty());
        }
    }

    private final List<AnsiStyleAttr> attrs;  // <-- think about unmodifiable List

    private AnsiStyle() {
        this.attrs = emptyList();
    }

    private AnsiStyle(Stream<AnsiStyleAttr> attrsStream) {
        this.attrs = attrsStream.toList();
    }

    /**
     * @return true if style does not have any attribute (otherwise - false)
     */
    public boolean isEmpty() {
        return attrs.isEmpty();
    }

    /**
     * @return true if style has at least one attribute (otherwise - false)
     */
    public boolean isNotEmpty() {
        return !this.isEmpty();
    }

    /**
     * @return the {@link Stream} of ansi-style attributes
     */
    public Stream<AnsiStyleAttr> attrs() {
        return attrs.stream();
    }

    /**
     * @return the escape-sequence as a {@link Stream} of escape-codes for each ansi-attribute
     */
    public Stream<String> ansiCodeSeq() {
        return attrs().map(AnsiStyleAttr::ansiCodeSeq);
    }

    /**
     * @return the escape-sequence surrounded with proper suffix and prefix
     */
    public String renderAnsi() {
        if (attrs.isEmpty()) {
            return "";
        } else {
            return format("\u001b[%sm", ansiCodeSeq().collect(joining(DELIMITER_ANSI_SEQ)));
        }
    }

    /**
     * @return the {@link Stream} of ansi-style attributes' names
     */
    public Stream<String> attrsNames() {
        return attrs().map(AnsiStyleAttr::name);
    }

    /**
     * @return dump the {@link AnsiStyle} object for debug purposes
     */
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

    /**
     * @return an instance of ansi-style builder from this ansi-style
     */
    public Builder builder() {
        return new Builder();
    }

    private static final AnsiStyle STYLE_EMPTY = new AnsiStyle();
    private static final AnsiStyle STYLE_RESET_ALL = new AnsiStyle(Stream.of(RESET_ALL));

    /**
     * @return an instance of empty style
     */
    public static AnsiStyle empty() {
        return STYLE_EMPTY;
    }

    /**
     * @return an instance of style that reset all previously applied styles
     */
    public static AnsiStyle resetAll() {
        return STYLE_RESET_ALL;
    }

    /**
     * @return a new instance of {@link AnsiStyle.Builder} from empty style
     */
    public static Builder emptyBuilder() {
        return empty().builder();
    }

    /**
     * A mutable builder to create an instance of immutable class {@link AnsiStyle}
     */
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
