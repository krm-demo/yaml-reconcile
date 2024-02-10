package org.krmdemo.yaml.reconcile.ansi;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

public class AnsiLine {

    private final List<AnsiSpan> spans;

    private AnsiLine(List<AnsiSpan> spansList) {
        this.spans = Collections.unmodifiableList(spansList);
    }

    public Stream<AnsiSpan> spans() {
        return spans.stream();
    }

    public static AnsiLine create(AnsiStyle.Holder parentStyleHolder, AnsiLine ...lines) {
        return stream(lines).flatMap(AnsiLine::spans).collect(builder(parentStyleHolder).collector());
    }

    public static AnsiLine create(AnsiStyle.Holder parentStyleHolder, Stream<AnsiSpan> spanStream) {
        return spanStream.collect(builder(parentStyleHolder).collector());
    }

    public static Builder builder(AnsiStyle.Holder parentStyleHolder) {
        return new Builder(parentStyleHolder);
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

        public Builder span(AnsiSpan span) {
            this.accumulate(this.spansList, span);
            return this;
        }

        public AnsiLine collect(Stream<AnsiSpan> spanStream) {
            return spanStream.collect(this.collector());
        }

        public Collector<AnsiSpan, List<AnsiSpan>, AnsiLine> collector() {
            return Collector.of(this::spansList, this::accumulate, this::combine, this::finish);
        }

        private List<AnsiSpan> spansList() { return spansList; }

        private void accumulate(List<AnsiSpan> spansList, AnsiSpan span) {
            // implement merging styles and content here:
            spansList.add(span.copy(parentStyleHolder));
        }

        private List<AnsiSpan> combine(List<AnsiSpan> left, List<AnsiSpan> right) {
            left.addAll(right);
            return left;
        }

        private AnsiLine finish(List<AnsiSpan> spansList) {
            return new AnsiLine(spansList);
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
