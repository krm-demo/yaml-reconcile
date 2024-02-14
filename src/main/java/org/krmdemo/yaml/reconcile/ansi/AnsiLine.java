package org.krmdemo.yaml.reconcile.ansi;

import org.antlr.v4.runtime.misc.Interval;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.krmdemo.yaml.reconcile.ansi.AnsiRenderCtx.renderCtx;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.emptyBuilder;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.resetAll;

/**
 * Immutable continues sequence of {@link AnsiSpan}s with different ansi-styles of any two siblings,
 * that represent the line of text at ansi-terminal (without trailing line-separator).
 */
public class AnsiLine implements AnsiSize {

    private final List<AnsiSpan> spans;

    private AnsiLine(List<AnsiSpan> spansList) {
        this.spans = Collections.unmodifiableList(spansList);
    }

    public Stream<AnsiSpan> spans() {
        return spans.stream();
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
        return spans().mapToInt(AnsiSpan::width).max().orElse(0);
    }

    public static AnsiLine create(AnsiStyle.Holder parentStyleHolder, Stream<AnsiSpan> spanStream) {
        return spanStream.collect(builder(parentStyleHolder));
    }

    public static AnsiLine create(AnsiStyle.Holder parentStyleHolder, AnsiLine... lines) {
        return stream(lines).flatMap(AnsiLine::spans).collect(builder(parentStyleHolder));
    }

    /**
     * @param parentStyleHolder a reference of parrent holder of ansi-style
     * @param contentPosFrom the start-position of in source line content (inclusive)
     * @param contentPosTo the stop-position of in source line  content (inclusive)
     * @return a sub-line with content of source line in the range <code>[contentPosFrom;contentPosTo)</code>.
     */
    public AnsiLine subLine(AnsiStyle.Holder parentStyleHolder, int contentPosFrom, int contentPosTo) {
        AtomicInteger contentPos = new AtomicInteger(0);
        return spans.stream()
            .flatMap(span -> span.subSpan(
                contentPosFrom - contentPos.get(),
                contentPosTo - contentPos.get()).stream())
            .peek(span -> contentPos.getAndAdd(span.width()))
            .collect(builder(parentStyleHolder));
    }

    public String renderSpans() {
        StringBuilder sb = new StringBuilder();
        renderCtx().setLineStyleBuilder(emptyBuilder());
        spans().forEach(span -> {
            span.style().map(renderCtx().getLineStyleBuilder()::apply);
            sb.append(renderCtx().getLineStyleBuilder().build().renderAnsi());
            sb.append(span.content());
            renderCtx().setLineStyleBuilder(emptyBuilder());
            span.style().map(renderCtx().getLineStyleBuilder()::reset);
        });
        sb.append(renderCtx().getLineStyleBuilder().build().renderAnsi());
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

    public static Builder builder(AnsiStyle.Holder parentStyleHolder) {
        return new Builder(parentStyleHolder);
    }

    private static final AnsiLine LINE_EMPTY = new AnsiLine(emptyList());

    public static AnsiLine empty() {
        return LINE_EMPTY;
    }

    public static class Builder implements Collector<AnsiSpan, List<AnsiSpan>, AnsiLine> {

        private final AnsiStyle.Holder parentStyleHolder;
        List<AnsiSpan> spansList = new LinkedList<>();

        private Builder(AnsiStyle.Holder parent) {
            this.parentStyleHolder = parent;
        }

        public AnsiLine build() {
            return new AnsiLine(spansList);
        }

        public Builder append(AnsiSpan... spans) {
            return append(stream(spans));
        }

        public Builder append(Stream<AnsiSpan> spanStream) {
            spanStream.forEach(span -> accumulate(this.spansList, span));
            return this;
        }

        private List<AnsiSpan> spansList() { return spansList; }

        private void accumulate(List<AnsiSpan> spansList, AnsiSpan span) {
            if (spansList.isEmpty() || !spansList.getLast().hasTheSameStyle(span)) {
                spansList.add(span.copy(parentStyleHolder));
            } else {
                AnsiStyle bothStyle = span.style().orElse(null);
                String bothContent = spansList.getLast().content() + span.content();
                spansList.set(spansList.size() - 1, AnsiSpan.create(bothStyle, bothContent));
            }
        }

        private List<AnsiSpan> combine(List<AnsiSpan> left, List<AnsiSpan> right) {
            left.addAll(right);
            return left;
        }

        private AnsiLine finish(List<AnsiSpan> spansList) {
            return spansList.isEmpty() ? empty() : new AnsiLine(spansList);
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
            return null;
        }
    }
}
