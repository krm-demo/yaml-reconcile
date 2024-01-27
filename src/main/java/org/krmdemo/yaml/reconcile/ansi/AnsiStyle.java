package org.krmdemo.yaml.reconcile.ansi;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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

    private final String formatString;

    private final String fg;
    private final String bg;

    private final Boolean bold;

    /**
     * The constructor of the root-style
     */
    private AnsiStyle() {
        this("");
    }

    private AnsiStyle(String formatString) {
        this.formatString = formatString;
        this.fg = null;
        this.bg = null;
        this.bold = null;
    }

    private AnsiStyle(String fg, String bg, Boolean bold) {
        this.formatString = ".";
        this.fg = fg;
        this.bg = bg;
        this.bold = bold;
    }

    public Optional<String> fg() {
        return Optional.ofNullable(fg);
    }

    public Optional<String> bg() {
        return Optional.ofNullable(bg);
    }

    public Optional<Boolean> bold() {
        return Optional.ofNullable(bold);
    }

    public Stream<String> ansiCodes() {
        return Stream.empty(); // TODO: implement
    }

    public String formatString() {
        return formatString;
    }

    public String dump() {
        asList(
            fg().map(attr -> format("fg(%s)", attr)).orElse(""),
            bg().map(attr -> format("bg(%s)", attr)).orElse("")
        );
        return "???"; // TODO: to be done
    }

    public Builder builder() {
        return new Builder();
    }

    public static AnsiStyle empty() {
        return ROOT;
    }

    public class Builder {

        private String formatString;
        private String fg;
        private String bg;

        private  Boolean bold;

        public AnsiStyle build() {
            if (isNotBlank(formatString)) {
                return new AnsiStyle(formatString);
            } else {
                return new AnsiStyle(fg, bg, bold);
            }
        }

        public Builder formatString(String formatString) {
            this.formatString = formatString;
            return this;
        }

        public Builder fg(String fg) {
            this.fg = fg;
            return this;
        }

        public Builder bg(String bg) {
            this.bg = bg;
            return this;
        }

        public Builder bold(Boolean bold) {
            this.bold = bold;
            return this;
        }
    }
}
