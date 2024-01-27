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
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.antlr.v4.runtime.tree.Trees.getNodeText;
import static org.apache.commons.text.StringEscapeUtils.escapeJava;

@Slf4j
public class AnsiText implements AnsiStyle.Holder {

    public class Span implements AnsiStyle.Holder {

        private final AnsiStyle style;
        private final String content;

        private Span(String content) {
            this(AnsiStyle.empty(), content);
        }

        private Span(AnsiStyle style, String content) {
            requireNonNull(content, "span's content must be NOT null");
            requireNonNull(content, "span's style must be NOT null");
            this.content = content;
            this.style = style;
        }

        public String content() {
            return content;
        }

        public Optional<AnsiStyle> style() {
            // effective style could be evaluated here or inside AnsiStyle
            return Optional.ofNullable(style);
        }

        public Optional<AnsiStyle.Holder> parent() {
            return Optional.of(AnsiText.this);
        }

        public int width() {
            return content.length();
        }

        public String renderAnsi() {
            // completely ignore the styles at the moment
            return content();
        }

        public String dump() {
            return format("span(%d|%3s|%s)", content.length(), style.dump(), content);
        }
    }

    public class Line {
        private final List<Span> spans = new ArrayList<>();

        public Stream<Span> spans() {
            return spans.stream();
        }

        public Span span(AnsiStyle style, String content) {
            Span newSpan = new Span(style, content);
            spans.add(newSpan);
            return newSpan;
        }

        public int width() {
            return spans().mapToInt(Span::width).sum();
        }

        public String renderAnsi() {
            return spans().map(Span::renderAnsi).collect(joining());
        }

        public String dump() {
            if (spans.isEmpty()) {
                return ":: empty line ::";
            } else {
                return format(":: line of %d spans ( width = %3d ) ::%n%s",
                    spans.size(), width(), spans().map(Span::dump).collect(joining(lineSeparator())));
            }
        }
    }

    private final AnsiStyle style;

    private final AnsiStyle.Builder styleBuilder;

    private final List<Line> lines = new ArrayList<>();

    private AnsiText(AnsiStyle style) {
        this.style = style;
        this.styleBuilder = style.builder();
    }

    private AnsiText(AnsiStyle style, List<Line> lines) {
        this(style);
        this.lines.addAll(lines);
    }

    @Override
    public Optional<AnsiStyle> style() {
        return Optional.ofNullable(style);
    }

    public String renderAnsi() {
        return lines.stream().map(Line::renderAnsi).collect(joining(lineSeparator()));
    }

    public AnsiText newLine() {
        lines.add(new Line());
        return this;
    }

    private Line currentLine() {
        if (lines.isEmpty()) {
            lines.add(new Line());
        }
        return lines.getLast();
    }

    public AnsiText span(String content) {
        currentLine().span(styleBuilder.build(), content);
        return this;
    }

    public AnsiText withStyle (AnsiStyle style) {
        return new AnsiText(style, this.lines);
    }

    public static AnsiText ansiText() {
        return new AnsiText(AnsiStyle.empty());
    }

    public static AnsiText ansiText(AnsiStyle style) {
        return new AnsiText(style);
    }

    public static AnsiText ansiText(AnsiStyle style, AnsiText ansiText) {
        return ansiText.withStyle(style);
    }

    public static AnsiText ansiText(AnsiText ansiText) {
        return new AnsiText(ansiText.style, ansiText.lines);
    }

    public static AnsiText ansiText(AnsiStyle style, String text) {
        AnsiText ansiText = ansiText(style);
        log.debug("=====================================");
        ParseTree textTree = ansiText.parseText(text);
        log.debug("textTree:\n" + dumpParseTree(textTree));
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
            span(ctx.getText());
        }

        @Override
        public void visitTerminal(TerminalNode node) {
            log.debug(format("|| visit %s", dumpTerminalNode(node)));
            if (node.getSymbol().getType() == AnsiTextLexer.CRLF) {
                newLine();
            }
        }
    }

    private static String escapeAll(String str) {
        return str.chars().boxed().map(c -> format("\\u%04x", c)).collect(joining());
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
