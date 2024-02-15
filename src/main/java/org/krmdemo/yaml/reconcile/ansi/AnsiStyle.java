package org.krmdemo.yaml.reconcile.ansi;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.stream;
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
    private static final String DELIMITER_ATTR_NAMES = ",";

    /**
     * An interface that holder of cascading ansi-style should implement
     */
    public interface Holder {

        /**
         * @return outer component, which provide the default style
         */
        default Optional<Holder> parent() {
            return Optional.empty();
        }

        /**
         * @return own style-property value
         */
        default Optional<AnsiStyle> style() {
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
            Builder builder = emptyBuilder();
            parentChain().flatMap(AnsiStyle::attrs).forEach(builder::accept);  // <-- think about reduce
            return builder.build();
        }

        default AnsiStyle styleOpen() {
            Builder builder = emptyBuilder();
            builder.apply(parentStyle());
            style().map(builder::apply);
            return builder.build();
        }

        default AnsiStyle styleClose() {
            return emptyBuilder().reset(styleOpen()).build();
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
     * @return string representation of all attributes
     */
    public String attrsNamesStr() {
        return attrsNames().collect(joining(DELIMITER_ATTR_NAMES));
    }

    /**
     * @return dump the {@link AnsiStyle} object for debug purposes
     */
    public String dump() {
        if (this.isEmpty()) {
            return "ansi-style-empty";
        } else {
            return format("ansi-style<%s>", this.attrsNamesStr());
        }
    }

    @Override
    public boolean equals(Object thatObj) {
        if (this == thatObj) return true;
        if (thatObj instanceof AnsiStyle that) {
            return this.attrsNamesStr().equals(that.attrsNamesStr());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.attrsNamesStr().hashCode();
    }

    @Override
    public String toString() {
        return dump();
    }

    public AnsiStyle over(AnsiStyle styleBefore) {
        return diff(this, styleBefore);
    }

    public static AnsiStyle diff(AnsiStyle styleAfter, AnsiStyle styleBefore) {
        Builder diffBuilder = emptyBuilder();
        Builder builderBefore = styleBefore.builder();
        Builder builderAfter = styleAfter.builder();
        styleAfter.attrs()
            .filter(builderBefore::doesNotContain)
            .forEach(diffBuilder::accept);
        styleBefore.attrs()
            .filter(builderAfter::doesNotContainFamily)
            .filter(attr -> attr.operation() == AnsiStyleAttr.Operation.apply)
            .flatMap(attr -> lookupResetFamily(attr.family()).stream())
            .forEach(diffBuilder::accept);
        return diffBuilder.build();
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
     * @param styleAttrs var-array of ansi-style attributes
     * @return ansi-style based on applying passed attributes to empty style
     */
    public static AnsiStyle ansiStyle(AnsiStyleAttr... styleAttrs) {
        return ArrayUtils.isEmpty(styleAttrs) ? empty() : emptyBuilder().acceptAll(styleAttrs).build();
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

        public boolean containsFamily(AnsiStyleAttr attr) {
            return attr != null && attrsMap.containsKey(attr.family());
        }

        public boolean contains(AnsiStyleAttr attr) {
            AnsiStyleAttr familyAttr = attr == null ? null : attrsMap.get(attr.family());
            return familyAttr != null && familyAttr.equals(attr);
        }

        public boolean doesNotContainFamily(AnsiStyleAttr attr) {
            return !this.containsFamily(attr);
        }

        public boolean doesNotContain(AnsiStyleAttr attr) {
            return !this.contains(attr);
        }

        public AnsiStyle build() {
            return attrsMap.isEmpty() ? empty() : new AnsiStyle(attrsMap.values().stream());
        }

        public Builder accept(AnsiStyleAttr styleAttr) {
            if (styleAttr.equals(RESET_ALL)) {
                attrsMap.clear();
            }
            attrsMap.put(styleAttr.family(), styleAttr);
            return this;
        }

        public Builder acceptAll(AnsiStyleAttr... attrs) {
            stream(attrs).forEach(this::accept);
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
