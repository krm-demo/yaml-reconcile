package org.krmdemo.yaml.reconcile.ansi;

import java.util.*;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class AnsiStyle {

    private static final AnsiStyle ROOT = new AnsiStyle();

    private final AnsiStyle parent;

    private final String formatString;

    private final String fg;
    private final String bg;

    private final Boolean bold;

    /**
     * The constructor of the root-style
     */
    private AnsiStyle() {
        this(null, "");
    }

    private AnsiStyle(AnsiStyle parent, String formatString) {
        this.parent = parent;
        this.formatString = formatString;
        this.fg = null;
        this.bg = null;
        this.bold = null;
    }

    private AnsiStyle(AnsiStyle parent, String fg, String bg, Boolean bold) {
        this.parent = parent;
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
        String parentDump = parent == null ? "" : parent.dump() + ":";
        return parentDump + formatString;
    }

    public Builder builder() {
        return new Builder();
    }

    public static Builder empty() {
        return ROOT.builder();
    }

    public class Builder {

        private String formatString;
        private String fg;
        private String bg;

        private  Boolean bold;

        public AnsiStyle build() {
            if (isNotBlank(formatString)) {
                return new AnsiStyle(AnsiStyle.this, formatString);
            } else {
                return new AnsiStyle(AnsiStyle.this, fg, bg, bold);
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
