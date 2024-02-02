package org.krmdemo.yaml.reconcile.ansi;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.krmdemo.yaml.reconcile.ansi.AnsiRenderContext.renderCtx;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.empty;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.resetAll;

/**
 * This class represents the multi-line text with global outer ansi-style and sequential fragments
 * with their own ansi-style that are applied according to nested hierarchy.
 * <p/>
 * The class is not immutable and could also be used as a builder of its content
 * during parsing the formatted ansi-text or programmatically.
 */
@Slf4j
public class AnsiText implements AnsiStyle.Holder {

    /**
     * Immutable class that represents a continues fragment of text within the same line and the same ansi-style,
     * which is mostly correspond to HTML element <code>&lt;span&gt;</code>.
     */
    public class Span implements AnsiStyle.Holder {

        private final AnsiStyle style;
        private final String content;

        private Span(AnsiStyle style, String content) {
            requireNonNull(content, "span's content must be NOT null");
            requireNonNull(content, "span's style must be NOT null");
            this.content = content;
            this.style = style;
        }

        /**
         * @return the content of this span of text
         */
        public String content() {
            return content;
        }

        /**
         * @return the ansy-style of this span of text
         */
        public Optional<AnsiStyle> style() {
            // effective style could be evaluated here or inside AnsiStyle
            return Optional.ofNullable(style);
        }

        /**
         * @return a holder of this span of text, which is an outer {@link AnsiText}
         */
        public Optional<AnsiStyle.Holder> parent() {
            return Optional.of(AnsiText.this);
        }

        /**
         * @return the length of this span of text
         */
        public int width() {
            return content.length();
        }

        /**
         * @return the content of span, surrounded with open and close escape-sequences
         */
        public String renderAnsi() {
            return styleOpen().renderAnsi() + content() + styleClose().renderAnsi();
        }

        /**
         * @return dump the {@link AnsiText.Span} object for debug purposes
         */
        public String dump() {
            return format(":: - span width=%3d |%-30s<|%s|>", content.length(), style.dump(), content);
        }
    }

    /**
     * A line of multi-line {@link AnsiText} (without trailing line-separator),
     * which consist of continues sequence of spans with different ansi-styles of any two siblings.
     */
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
            if (renderCtx().siblingStylesSquash()) {
                // TODO: looks like style-builder should be put into render-context
                StringBuilder sb = new StringBuilder();
                spans().forEach(span -> {
                    sb.append(span.styleOpen().renderAnsi());
                    sb.append(span.content());
                    sb.append(span.styleClose().renderAnsi());
                });
                sb.append(resetAll().renderAnsi());
                return sb.toString();
            } else {
                return spans().map(Span::renderAnsi).collect(joining()) + resetAll().renderAnsi();
            }
        }

        /**
         * @return dump the {@link AnsiText.Line} object for debug purposes
         */
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

    private final List<Line> lines = new ArrayList<>();

    private AnsiStyle.Builder styleBuilder = empty().builder();
    private final LinkedList<AnsiStyle> styleStack = new LinkedList<>();

    private AnsiText(AnsiStyle style) {
        this.style = style;
    }

    private AnsiText(AnsiStyle style, List<Line> lines) {
        this(style);
        this.lines.addAll(lines);
    }

    /**
     * @return an optional global ansi-style within this ansi-text
     */
    public Optional<AnsiStyle> style() {
        return Optional.ofNullable(style);
    }

    /**
     * @return the lines of this ansi-text (each consista of spans)
     */
    public List<Line> lines() {
        return unmodifiableList(this.lines);
    }

    /**
     * @return ansi-text, which is properly decorated with ansi-sequences
     */
    public String renderAnsi() {
        return lines.stream().map(Line::renderAnsi).collect(joining(lineSeparator()));
    }

    /**
     * @return dump the {@link AnsiText} object for debug purposes
     */
    public String dump() {
        if (lines.isEmpty()) {
            return "~~ empty ansi-text ~~";
        } else {
            return format("~~ ansi-text of %d lines ~~%n%s",
                lines.size(), lines.stream().map(Line::dump).collect(joining(lineSeparator())));
        }
    }

    /**
     * Creates a new empty line for further adding the spans to.
     *
     * @return this object as a builder for further mutations
     */
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

    /**
     * Adds a span to current line (the initial one or the last one that was added by {@link #newLine()}).
     *
     * @return this object as a builder for further mutations
     */
    public AnsiText span(String content) {
        currentLine().span(styleBuilder.build(), content);
        return this;
    }

    public AnsiText withStyle (AnsiStyle style) {
        return new AnsiText(style, this.lines);
    }

    public static AnsiText ansiText() {
        return new AnsiText(empty());
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

    public static AnsiText ansiText(String text) {
        return ansiText(empty(), text);
    }

    public static AnsiText ansiText(AnsiStyle style, String text) {
        AnsiText ansiText = ansiText(style);
        ParseTree textTree = ansiText.parseText(text);
//        log.trace("textTree:\n" + dumpParseTree(textTree));
//        log.trace("=====================================");
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
    private class TextListener extends AnsiTextParserBaseListener {
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
            log.debug(format("|--> enterLine - #%d", lines.size()));
        }

        @Override
        public void exitLine(AnsiTextParser.LineContext ctx) {
            log.debug(format("|<--  exitLine - #%d", lines.size()));
        }

        @Override
        public void enterSpan(AnsiTextParser.SpanContext ctx) {
            log.debug(format("|---> enterSpan #%d in line #%d", currentLine().spans.size(), lines.size()));
        }

        @Override
        public void exitSpan(AnsiTextParser.SpanContext ctx) {
            log.debug(format("|<---  exitSpan - '%s'", ctx.getText()));
            span(ctx.getText());
        }

        @Override
        public void enterStyleOpen(AnsiTextParser.StyleOpenContext ctx) {
            AnsiStyle parentStyle = styleBuilder.build();
            styleStack.addLast(parentStyle);
            log.debug(format("|---> enterStyleOpen - push in stack (%d) %s", styleStack.size(), parentStyle));
        }

        @Override
        public void exitStyleOpen(AnsiTextParser.StyleOpenContext ctx) {
            log.debug(format("|<---  exitStyleOpen - stack (%d) %s", styleStack.size(), styleBuilder.build()));
        }

        @Override
        public void enterStyleClose(AnsiTextParser.StyleCloseContext ctx) {
            log.debug(format("|---> enterStyleClose - push in stack (%d)", styleStack.size()));
        }

        @Override
        public void exitStyleClose(AnsiTextParser.StyleCloseContext ctx) {
            if (!styleStack.isEmpty()) {
                styleBuilder = styleStack.removeLast().builder();
            }
            log.debug(format("|<---  exitStyleClose - stack (%d) %s", styleStack.size(), styleBuilder.build()));
        }

        @Override
        public void enterStyleAttrName(AnsiTextParser.StyleAttrNameContext ctx) {
            log.debug(format("|----> enterStyleAttrName - style before: %s", styleBuilder.build()));
        }

        @Override
        public void exitStyleAttrName(AnsiTextParser.StyleAttrNameContext ctx) {
            styleBuilder.acceptByName(ctx.getText());
            log.debug(format("|<----  exitStyleAttrName - '%s', style after: %s", ctx.getText(), styleBuilder.build()));
        }

        @Override
        public void visitTerminal(TerminalNode node) {
//            log.trace(format("|| visit %s", dumpTerminalNode(node)));
            if (node.getSymbol().getType() == AnsiTextLexer.CRLF) {
                newLine();
            }
        }
    }

//    private static String escapeAll(String str) {
//        return str.chars().boxed().map(c -> format("\\u%04x", c)).collect(joining());
//    }
//
//    private static String dumpParseTree(ParseTree parseTree) {
//        String nodeText = Utils.escapeWhitespace(getNodeText(parseTree, (List<String>)null), true);
//        StringBuilder sb = new StringBuilder(format("<< %s >> ", nodeText));
//        sb.append(format("(%s) ", parseTree.getClass().getSimpleName()));
//        sb.append(lineSeparator());
//        for (int i = 0; i < parseTree.getChildCount(); i++) {
//            sb.append("+-- ");
//            String childText = dumpParseTree(parseTree.getChild(i));
//            sb.append(childText.replaceAll("\n", "\n    "));
//        }
//        return sb.toString();
//    }
//
//    private static String dumpTerminalNode(TerminalNode node) {
//        if (node.getSymbol() == null) {
//            return format("<< unknown symbol for terminal node <%s>%s",
//                escapeJava(node.getText()), escapeAll(node.getText()));
//        } else {
//            return dumpToken(node.getSymbol());
//        }
//    }
//
//    private static String dumpToken(Token token) {
//        if (token == null) {
//            return "<< unknown token >>";
//        }
//        String nodeText = token.getText();
//        int nodeType = token.getType();
//        String nodeTypeName = nodeType > 0 ? AnsiTextLexer.ruleNames[nodeType - 1] : "nodeType#" + nodeType;
//        return format("%s<%s>%s(%d..%d) in line %d at position %d",
//            nodeTypeName + "--" + token.getTokenIndex(), escapeJava(nodeText), escapeAll(nodeText),
//            token.getStartIndex(),
//            token.getStopIndex(),
//            token.getLine(),
//            token.getCharPositionInLine());
//    }
}
