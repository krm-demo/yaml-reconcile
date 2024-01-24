package org.krmdemo.yaml.reconcile.ansi;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.text.StringEscapeUtils.escapeJava;

@Slf4j
public class AnsiText {

    private AnsiText() {
        // direct instantiating is prohibited
    }

    public static class LineBuilder {
        private final StringBuilder builder = new StringBuilder();

        public String renderAnsi() {
            return builder.toString();
        }

        public LineBuilder content(String content) {
            builder.append(content);
            return this;
        }

        public String dump() {
            if (builder.isEmpty()) {
                return "::   empty line     ::";
            } else {
                return format(":: line-width = %3d :: %s ::", builder.length(), builder );
            }
        }
    }

    public static class SpanBuilder {
        private final StringBuilder builder = new StringBuilder();

        public String renderAnsi() {
            return builder.toString();
        }

        public SpanBuilder content(String content) {
            builder.append(content);
            return this;
        }

        public String dump() {
            if (builder.isEmpty()) {
                return "::   empty span     ::";
            } else {
                return format(":: span-width = %3d :: %s ::", builder.length(), builder );
            }
        }
    }

    private final List<LineBuilder> lines = new ArrayList<>();
    private final List<SpanBuilder> spans = new ArrayList<>();

    public Stream<String> renderLinesAnsi() {
        return lines.stream().map(LineBuilder::renderAnsi);
    }

    public LineBuilder lastLine() {
        return lines.getLast();
    }

    public LineBuilder line() {
        lines.add(new LineBuilder());
        return lastLine();
    }

    public String dumpLines() {
        return lines.stream()
            .map(LineBuilder::dump)
            .collect(joining(lineSeparator()));
    }

    public Stream<String> renderSpansAnsi() {
        return spans.stream().map(SpanBuilder::renderAnsi);
    }

    public SpanBuilder lastSpan() {
        return spans.getLast();
    }

    public SpanBuilder span() {
        spans.add(new SpanBuilder());
        return lastSpan();
    }

    public String dumpSpans() {
        return spans.stream()
            .map(SpanBuilder::dump)
            .collect(joining(lineSeparator()));
    }

    public static AnsiText ansiText(String text) {
        AnsiText ansiText = new AnsiText();
        log.debug("=====================================");
        ParseTree textTree = ansiText.parseText(text);
        log.debug("=====================================");
        ParseTree linesTree = ansiText.parseLines(text);
        log.debug("=====================================");
        ParseTree spansTree = ansiText.parseSpans(text);
        log.debug("=====================================");
        return ansiText;
    }

    private ParseTree parseText(String text) {
        AnsiTextLexer lexer = new AnsiTextLexer(CharStreams.fromString(text));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        AnsiTextLinesParser parser = new AnsiTextLinesParser(tokens);
        ParseTree tree = parser.text();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new TextListener(), tree);
        return tree;
    }

    private ParseTree parseLines(String text) {
        AnsiTextLinesLexer lexer = new AnsiTextLinesLexer(CharStreams.fromString(text));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        AnsiTextLinesParser parser = new AnsiTextLinesParser(tokens);
        ParseTree tree = parser.text();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new LinesListener(), tree);
        return tree;
    }

    private ParseTree parseSpans(String text) {
        AnsiTextSpansLexer lexer = new AnsiTextSpansLexer(CharStreams.fromString(text));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        AnsiTextSpansParser parser = new AnsiTextSpansParser(tokens);
        ParseTree tree = parser.text();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new SpansListener(), tree);
        return tree;
    }

    /**
     * Listen to lines of spans
     */
    private class TextListener extends AnsiTextBaseListener {
        @Override
        public void enterText(AnsiTextParser.TextContext ctx) {
            log.debug("|-> Text (lines of spans)");
        }

        @Override
        public void exitText(AnsiTextParser.TextContext ctx) {
            log.debug("|<- Text (lines of spans)");
        }
    }

    /**
     * Listen to lines
     */
    private class LinesListener extends AnsiTextLinesBaseListener {

        @Override
        public void enterText(AnsiTextLinesParser.TextContext ctx) {
            log.debug("|-> Text (lines)");
        }

        @Override
        public void exitText(AnsiTextLinesParser.TextContext ctx) {
            log.debug("|<- Text (lines)");
        }

        @Override
        public void enterLineLF(AnsiTextLinesParser.LineLFContext ctx) {
            log.debug("|--> LineLF");
        }

        @Override
        public void exitLineLF(AnsiTextLinesParser.LineLFContext ctx) {
            log.debug("|<-- LineLF");
        }

        @Override
        public void enterLineOpen(AnsiTextLinesParser.LineOpenContext ctx) {
            log.debug(format("|---> enterLineOpen - %3d", lines.size() + 1));
            line();
        }

        @Override
        public void exitLineOpen(AnsiTextLinesParser.LineOpenContext ctx) {
            String content = ctx.getText();
            lastLine().content(content);
            log.debug(format("|<---  exitSpanOpen - %3d --> [ %s ; %s ] '%s'",
                lines.size(), startStopIndexes(ctx.getStart()), startStopIndexes(ctx.getStop()), content));
        }
    }

    /**
     * Listen to spans
     */
    private class SpansListener extends AnsiTextSpansBaseListener {

        @Override
        public void enterText(AnsiTextSpansParser.TextContext ctx) {
            log.debug("|-> Text (spans)");
        }

        @Override
        public void exitText(AnsiTextSpansParser.TextContext ctx) {
            log.debug("|<- Text (spans)");
        }

        @Override
        public void enterSpanAnsiExpr(AnsiTextSpansParser.SpanAnsiExprContext ctx) {
            log.debug("|--> SpanAnsiExpr");
        }

        @Override
        public void exitSpanAnsiExpr(AnsiTextSpansParser.SpanAnsiExprContext ctx) {
            log.debug("|<-- SpanAnsiExpr");
        }

        @Override
        public void enterSpanOpen(AnsiTextSpansParser.SpanOpenContext ctx) {
            log.debug(format("|---> enterSpanOpen - %3d", spans.size() + 1));
            span();
        }

        @Override
        public void exitSpanOpen(AnsiTextSpansParser.SpanOpenContext ctx) {
            String content = escapeJava(ctx.getText());
            lastSpan().content(content);
            log.debug(format("|<---  exitSpanOpen - %3d --> [ %s ; %s ] '%s'",
                spans.size(), startStopIndexes(ctx.getStart()), startStopIndexes(ctx.getStop()), content));
        }
    }

    private static String startStopIndexes(Token token) {
        if (token == null) {
            return "???";
        }
        return format("(%d;%d)", token.getStartIndex(), token.getStopIndex());
    }
}
