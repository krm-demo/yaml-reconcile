package org.krmdemo.yaml.reconcile.ansi;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.joining;
import static org.krmdemo.yaml.reconcile.ansi.AnsiRenderCtx.renderCtx;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.resetAll;

/**
 * Immutable continues sequence of {@link AnsiSpan}s with different ansi-styles of any two siblings,
 * that represent the line of text at ansi-terminal (without trailing line-separator).
 */
public class AnsiLine implements AnsiSize {

    /**
     * An interface for continues sequence of {@link AnsiLine}
     */
    public interface Provider {

        /**
         * @return the number of lines in multi-line area of rendering
         */
        default int linesCount() {
            return 0;
        }

        /**
         * @return the maximum width of line in multi-line area
         */
        default int maxWidth() {
            return 0;
        }

        /**
         * @param lineNum zero-based index of line in multi-line area
         * @return the instance of {@link AnsiLine} that correspond to line with "lineNum" index
         */
        default AnsiLine lineAt(int lineNum) {
            return emptyLine();
        }

        /**
         * @return the first line (default delegating to {@link #lineAt(int)})
         */
        default AnsiLine firstLine() {
            return lineAt(0);
        }
    }

    private final List<AnsiSpan> spans;

    private AnsiLine(List<AnsiSpan> spansList) {
        this.spans = Collections.unmodifiableList(spansList);
    }

    public Stream<AnsiSpan> spans() {
        return spans.stream();
    }

    /**
     * @return true if line is empty
     */
    public boolean isEmpty() {
        return spans.isEmpty();
    }

    /**
     * @return true if line is NOT empty
     */
    public boolean isNotEmpty() {
        return !this.isEmpty();
    }

    public String content() {
        return spans().map(AnsiSpan::content).collect(joining());
    }

    @Override
    public int height() {
        return 1;
    }

    @Override
    public int width() {
        return spans().mapToInt(AnsiSpan::width).sum();
    }

    public static AnsiLine create(String ansiTextFmt) {
        return AnsiText.ansiLine(ansiTextFmt);
    }

    public static AnsiLine create(AnsiStyle.Holder parentStyleHolder, Stream<AnsiSpan> spanStream) {
        return spanStream.collect(builder(parentStyleHolder));
    }

    public static AnsiLine create(AnsiStyle.Holder parentStyleHolder, AnsiLine... lines) {
        return stream(lines).flatMap(AnsiLine::spans).collect(builder(parentStyleHolder));
    }

    /**
     * @param parentStyleHolder a reference to parent holder of ansi-style
     * @param contentPosFrom the start-position in source line content (inclusive)
     * @param contentPosTo the stop-position in source line content (inclusive)
     * @return a sub-line with content of source line in the range <code>[contentPosFrom;contentPosTo)</code>.
     */
    public AnsiLine subLine(AnsiStyle.Holder parentStyleHolder, int contentPosFrom, int contentPosTo) {
        if (contentPosFrom >= contentPosTo || contentPosTo < 0 || contentPosFrom >= width()) {
            return emptyLine();
        }
        int currentPos = 0;
        List<AnsiSpan> subSpans = new ArrayList<>();
        for (AnsiSpan span : spans) {
            if (currentPos > contentPosTo) {
                break;
            } else if (currentPos + span.width() < contentPosFrom) {
                currentPos += span.width();
                continue;
            }
            span.subSpan(contentPosFrom - currentPos, contentPosTo - currentPos).map(subSpans::add);
            currentPos += span.width();
        }
        return create(parentStyleHolder, subSpans.stream());
    }

    public String renderSpans() {
        StringBuilder sb = new StringBuilder();
        AnsiStyle lastOpen = AnsiStyle.emptyStyle();
        AnsiStyle lastClose = AnsiStyle.emptyStyle();
        for (AnsiSpan span : spans) {
            AnsiStyle currentOpen = span.styleOpen();
            sb.append(currentOpen.over(lastOpen).renderAnsi());
            sb.append(span.content());
            lastOpen = currentOpen;
            lastClose = span.styleClose();
        };
        sb.append(lastClose.renderAnsi());
        return sb.toString();
    }

    public String renderAnsi() {
        StringBuilder sb = new StringBuilder();
        if (renderCtx().isLinePrefixResetAll()) {
            sb.append(resetAll().renderAnsi());
        }
        sb.append(this.renderSpans());
        if (renderCtx().isLineSuffixResetAll()) {
            sb.append(resetAll().renderAnsi());
        }
        return sb.toString();
    }

    /**
     * @return sequence of ansi-styles that open each span
     */
    public Stream<AnsiStyle> spanStylesOpen() {
        return spans().map(AnsiSpan::styleOpen);
    }

    /**
     * @return dump the {@link AnsiLine} object for debug purposes
     */
    public String dump() {
        return isEmpty() ? "empty-line" : spans().map(AnsiSpan::dump).collect(joining(";"));
    }

    public static Builder builder(AnsiStyle.Holder parentStyleHolder) {
        return new Builder(parentStyleHolder);
    }

    private static final AnsiLine LINE_EMPTY = new AnsiLine(emptyList());

    public static AnsiLine emptyLine() {
        return LINE_EMPTY;
    }

    private static final AnsiLine.Provider BLOCK_EMPTY = new AnsiLine.Provider(){};

    public static AnsiLine.Provider emptyBlock() {
        return BLOCK_EMPTY;
    }

    /**
     * A builder to create an instance of immutable object {@link AnsiLine}
     * or to reduce the stream of {@link AnsiSpan}s as a standard {@link Collector}.
     */
    public static class Builder implements Collector<AnsiSpan, List<AnsiSpan>, AnsiLine> {

        private final AnsiStyle.Holder parentStyleHolder;
        List<AnsiSpan> spansList = new LinkedList<>();

        private Builder(AnsiStyle.Holder parent) {
            this.parentStyleHolder = parent;
        }

        public AnsiLine build() {
            return finish(this.spansList);
        }

        public Builder append(AnsiSpan... spans) {
            return append(stream(spans));
        }

        public Builder append(Stream<AnsiSpan> spanStream) {
            spanStream.forEach(span -> accumulate(this.spansList, span.copy(parentStyleHolder)));
            return this;
        }

        private List<AnsiSpan> spansList() { return spansList; }

        private void accumulate(List<AnsiSpan> spansList, AnsiSpan span) {
            if (spansList.isEmpty() || !spansList.getLast().hasTheSameOpenStyle(span)) {
                spansList.add(span.copy(parentStyleHolder));
            } else {
                AnsiStyle bothStyle = span.style().orElse(null);
                String bothContent = spansList.getLast().content() + span.content();
                spansList.set(spansList.size() - 1, AnsiSpan.create(parentStyleHolder, bothStyle, bothContent));
            }
        }

        private List<AnsiSpan> combine(List<AnsiSpan> left, List<AnsiSpan> right) {
            left.addAll(right);
            return left;
        }

        private AnsiLine finish(List<AnsiSpan> spansList) {
            return spansList.isEmpty() ? emptyLine() : new AnsiLine(spansList);
        }

        @Override
        public Supplier<List<AnsiSpan>> supplier() {
            return this::spansList;
        }

        @Override
        public BiConsumer<List<AnsiSpan>, AnsiSpan> accumulator() {
            return this::accumulate;
        }

        @Override
        public BinaryOperator<List<AnsiSpan>> combiner() {
            return this::combine;
        }

        @Override
        public Function<List<AnsiSpan>, AnsiLine> finisher() {
            return this::finish;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return emptySet();
        }
    }
}
