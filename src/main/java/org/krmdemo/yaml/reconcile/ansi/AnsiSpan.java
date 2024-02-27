package org.krmdemo.yaml.reconcile.ansi;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.emptyStyle;

/**
 * Immutable class that represents a continues fragment of text within the same line and the same ansi-style,
 * which is mostly correspond to HTML element <code>&lt;span&gt;</code>.
 */
public class AnsiSpan implements AnsiStyle.Holder, AnsiSize {

    private final AnsiStyle.Holder parent;
    private final AnsiStyle style;
    private final String content;

    private AnsiSpan(AnsiStyle.Holder parent, AnsiStyle style, String content) {
        if (StringUtils.isEmpty(content)) {
            throw new IllegalArgumentException("the content of ansi-span must not be null or empty");
        }
        this.parent = parent;
        this.style = style;
        this.content = content;
    }

    public static AnsiSpan create(AnsiStyle.Holder parent, AnsiStyle style, String content) {
        return new AnsiSpan(parent, style, content);
    }

    public static AnsiSpan create(AnsiStyle style, String content) {
        return new AnsiSpan(null, style, content);
    }

    public static AnsiSpan create(String content) {
        return new AnsiSpan(null, null, content);
    }

    public static AnsiSpan copyFrom(AnsiStyle.Holder newParent, AnsiSpan spanToCopy) {
        return new AnsiSpan(newParent, spanToCopy.style, spanToCopy.content);
    }

    public AnsiSpan copy(AnsiStyle.Holder newParent) {
        return copyFrom(newParent, this);
    }

    @Override
    public Optional<AnsiStyle> style() {
        return Optional.ofNullable(style);
    }

    @Override
    public Optional<AnsiStyle.Holder> parent() {
        return Optional.ofNullable(this.parent);
    }

    public String content() {
        return this.content;
    }

    @Override
    public int height() {
        return 1;
    }

    public int width() {
        return this.content.length();
    }

    public Optional<AnsiSpan> subSpan(int beginIndex, int endIndex) {
        endIndex = min(this.content.length(), endIndex);
        beginIndex = Math.max(0, beginIndex);
        return endIndex <= beginIndex ? Optional.empty()
            : Optional.of(create(this.parent, this.style, this.content.substring(beginIndex, endIndex)));
    }

    /**
     * @return the content of span, surrounded with open and close escape-sequences
     */
    public String renderAnsi() {
        return styleOpen().renderAnsi() + content() + styleClose().renderAnsi();
    }

    /**
     * @return dump the {@link AnsiSpan} object for debug purposes
     */
    public String dump() {
        return format("ansi-span(%d)<%s|%s|%s>\"%s\"",
            content.length(),
            styleOpen().attrsNamesStr(),
            style().orElse(emptyStyle()).attrsNamesStr(),
            styleClose().attrsNamesStr(),
            content);
    }

    /**
     * @param that an ansi-style to compare with
     * @return <code>true</code>if the passed ansi-span has the same ansi-style to open (start rendering)
     */
    public boolean hasTheSameOpenStyle(AnsiSpan that) {
        return this.styleOpen().equals(that.styleOpen());
    }
}
