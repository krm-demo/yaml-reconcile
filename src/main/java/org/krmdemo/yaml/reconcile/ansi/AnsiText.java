package org.krmdemo.yaml.reconcile.ansi;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.text.StringEscapeUtils;

import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;
import static org.antlr.v4.runtime.tree.Trees.getNodeText;
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
        log.debug("textTree:\n" + dumpParseTree(textTree));
        log.debug("=====================================");
        ParseTree linesTree = ansiText.parseLines(text);
        log.debug("linesTree:\n" + dumpParseTree(linesTree));
        log.debug("=====================================");
        ParseTree spansTree = ansiText.parseSpans(text);
        log.debug("spansTree:\n" + dumpParseTree(spansTree));
        log.debug("=====================================");
        return ansiText;
    }

    private ParseTree parseText(String text) {
        AnsiTextLexer lexer = new AnsiTextLexer(CharStreams.fromString(text));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        AnsiTextParser parser = new AnsiTextParser(tokens);
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

        @Override
        public void enterLine(AnsiTextParser.LineContext ctx) {
            log.debug(format("|--> enterLine - start { %s }", dumpToken(ctx.start)));
        }

        @Override
        public void exitLine(AnsiTextParser.LineContext ctx) {
            log.debug(format("|<--  exitLine - stop  { %s }", dumpToken(ctx.stop)));
        }

        @Override
        public void enterSpan(AnsiTextParser.SpanContext ctx) {
            log.debug(format("|---> enterSpan - start { %s }", dumpToken(ctx.start)));
        }

        @Override
        public void exitSpan(AnsiTextParser.SpanContext ctx) {
            log.debug(format("|<---  exitSpan - stop  { %s }", dumpToken(ctx.stop)));
        }

        @Override
        public void visitTerminal(TerminalNode node) {
            log.debug(format("|| visit %s", dumpTerminalNode(node)));
        }
    }

    private static String escapeAll(String str) {
        return str.chars().boxed().map(c -> format("\\u%04x", c)).collect(joining());
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
        public void enterLine(AnsiTextLinesParser.LineContext ctx) {
            log.debug(format("|---> enterLine - %3d", lines.size() + 1));
            line();
        }

        @Override
        public void exitLine(AnsiTextLinesParser.LineContext ctx) {
            String content = ctx.getText();
            lastLine().content(content);
            log.debug(format("|<---  exitLine - %3d --> [ %s ; %s ] '%s'",
                lines.size(), startStopIndexes(ctx.getStart()), startStopIndexes(ctx.getStop()), content));
        }

        @Override
        public void visitTerminal(TerminalNode node) {
             if (node.getSymbol().getType() == AnsiTextLinesLexer.CRLF) {
                log.debug(format("|| visit CRLF(%s) in line %d at position %d",
                    node.getSourceInterval(),
                    node.getSymbol().getLine(),
                    node.getSymbol().getCharPositionInLine()));
            }
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
        public void enterSpan(AnsiTextSpansParser.SpanContext ctx) {
            log.debug(format("|---> enterSpan - %3d", spans.size() + 1));
            span();
        }

        @Override
        public void exitSpan(AnsiTextSpansParser.SpanContext ctx) {
            String content = escapeJava(ctx.getText());
            lastSpan().content(content);
            log.debug(format("|<---  exitSpan - %3d --> [ %s ; %s ] '%s'",
                spans.size(), startStopIndexes(ctx.getStart()), startStopIndexes(ctx.getStop()), content));
        }

        public void visitTerminal(TerminalNode node) {
            if (node.getSymbol().getType() == AnsiTextSpansLexer.ANSI_EXPR) {
                log.debug(format("|| visit ANSI_EXPR<%s>(%s) in line %d at position %d",
                    node.getText(),
                    node.getSourceInterval(),
                    node.getSymbol().getLine(),
                    node.getSymbol().getCharPositionInLine()));
            }
        }
    }

    private static String startStopIndexes(Token token) {
        if (token == null) {
            return "???";
        }
        return format("(%d;%d)", token.getStartIndex(), token.getStopIndex());
    }

    private static String dumpParseTree(ParseTree parseTree) {
        String nodeText = Utils.escapeWhitespace(getNodeText(parseTree, (List<String>)null), true);
        StringBuilder sb = new StringBuilder(format("<< %s >> ", nodeText));
        sb.append(format("(%s) ", parseTree.getClass().getSimpleName()));
        sb.append(lineSeparator());
        for (int i = 0; i < parseTree.getChildCount(); i++) {
            sb.append("+-- ");
            String childText = dumpParseTree(parseTree.getChild(i));
            sb.append(childText.replaceAll("\n", "\n    "));
        }
        return sb.toString();
    }

    private static String dumpTerminalNode(TerminalNode node) {
        if (node.getSymbol() == null) {
            return format("<< unknown symbol for terminal node <%s>%s",
                escapeJava(node.getText()), escapeAll(node.getText()));
        } else {
            return dumpToken(node.getSymbol());
        }
    }

    private static String dumpToken(Token token) {
        if (token == null) {
            return "<< unknown token >>";
        }
        String nodeText = token.getText();
        int nodeType = token.getType();
        String nodeTypeName = AnsiTextLexer.ruleNames[nodeType - 1];
        return format("%s<%s>%s(%d..%d) in line %d at position %d",
            nodeTypeName, escapeJava(nodeText), escapeAll(nodeText),
            token.getStartIndex(),
            token.getStopIndex(),
            token.getLine(),
            token.getCharPositionInLine());
    }
}
