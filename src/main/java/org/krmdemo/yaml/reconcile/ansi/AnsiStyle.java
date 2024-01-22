package org.krmdemo.yaml.reconcile.ansi;

import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;

public class AnsiStyle {

    private static final AnsiStyle EMPTY = new AnsiStyle();

    private final AnsiStyle parent;

    private final String fg;
    private final String bg;

    private final Boolean bold;

    private AnsiStyle() {
        this.parent = null;
        this.fg = null;
        this.bg = null;
        this.bold = null;
    }

    private AnsiStyle(AnsiStyle parent, String fg, String bg, Boolean bold) {
        this.parent = parent;
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

    public int depth() {
        return parent != null ? parent.depth() + 1 : 0;
    }

    public Stream<String> ansiCodes() {
        return Stream.empty(); // TODO: implement
    }

    public String beginAnsiCodes() {
        return "on"; // TODO: implement
    }

    public String endAnsiCodes() {
        return "off"; // TODO: implement
    }

    public String beginAnsi() {
        if (parent == null) {
            return "start-root";
        }
        return format("start[%d]<%s>", depth(), beginAnsiCodes());
    }

    public String endAnsi() {
        if (parent == null) {
            return "end-root";
        }
        return format("end[%d]<%s>", depth(), endAnsiCodes());
    }

    public Builder builder() {
        return new Builder();
    }

    public static Builder empty() {
        return EMPTY.builder();
    }

    public class Builder {
        private String fg;
        private String bg;

        private  Boolean bold;

        public AnsiStyle build() {
            return new AnsiStyle(AnsiStyle.this, fg, bg, bold);
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
