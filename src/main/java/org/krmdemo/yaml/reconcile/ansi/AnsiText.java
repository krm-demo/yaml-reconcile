package org.krmdemo.yaml.reconcile.ansi;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.krmdemo.yaml.reconcile.ansi.AnsiRenderCtx.renderCtx;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.empty;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.emptyBuilder;
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
         * @return the content of this span of text without any styles
         */
        public String content() {
            return content;
        }

        /**
         * @return the ansy-style of this span of text
         */
        public Optional<AnsiStyle> style() {
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

        /**
         * @return content of text-line without any ansi-styles
         */
        public String content() {
            return spans().map(Span::content).collect(joining());
        }

        public int width() {
            return spans().mapToInt(Span::width).sum();
        }

        public String renderSpans() {
            if (renderCtx().isSiblingStylesSquash()) {
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
            } else {
                return spans().map(Span::renderAnsi).collect(joining());
            }
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

        public void appendSpan(AnsiStyle style, String content) {
            Span newSpan = new Span(style, content);
            spans.add(newSpan);
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
     * @return content of text without any ansi-styles
     */
    public String content() {
        return lines.stream().map(Line::content).collect(joining(lineSeparator()));
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
        currentLine().appendSpan(styleBuilder.build(), content);
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
            log.trace("|-> Text (lines of spans)");
        }

        @Override
        public void exitText(AnsiTextParser.TextContext ctx) {
            log.trace("|<- Text (lines of spans)");
        }

        @Override
        public void enterLine(AnsiTextParser.LineContext ctx) {
            log.trace(format("|--> enterLine - #%d", lines.size()));
        }

        @Override
        public void exitLine(AnsiTextParser.LineContext ctx) {
            log.trace(format("|<--  exitLine - #%d", lines.size()));
        }

        @Override
        public void enterSpan(AnsiTextParser.SpanContext ctx) {
            log.trace(format("|---> enterSpan #%d in line #%d", currentLine().spans.size(), lines.size()));
        }

        @Override
        public void exitSpan(AnsiTextParser.SpanContext ctx) {
            log.trace(format("|<---  exitSpan - '%s'", ctx.getText()));
            span(ctx.getText());
        }

        @Override
        public void enterStyleOpen(AnsiTextParser.StyleOpenContext ctx) {
            AnsiStyle parentStyle = styleBuilder.build();
            styleStack.addLast(parentStyle);
            log.trace(format("|---> enterStyleOpen - push in stack (%d) %s", styleStack.size(), parentStyle));
        }

        @Override
        public void exitStyleOpen(AnsiTextParser.StyleOpenContext ctx) {
            log.trace(format("|<---  exitStyleOpen - stack (%d) %s", styleStack.size(), styleBuilder.build()));
        }

        @Override
        public void enterStyleClose(AnsiTextParser.StyleCloseContext ctx) {
            log.trace(format("|---> enterStyleClose - push in stack (%d)", styleStack.size()));
        }

        @Override
        public void exitStyleClose(AnsiTextParser.StyleCloseContext ctx) {
            if (!styleStack.isEmpty()) {
                styleBuilder = styleStack.removeLast().builder();
            }
            log.trace(format("|<---  exitStyleClose - stack (%d) %s", styleStack.size(), styleBuilder.build()));
        }

        @Override
        public void enterStyleAttrName(AnsiTextParser.StyleAttrNameContext ctx) {
            log.trace(format("|----> enterStyleAttrName - style before: %s", styleBuilder.build()));
        }

        @Override
        public void exitStyleAttrName(AnsiTextParser.StyleAttrNameContext ctx) {
            styleBuilder.acceptByName(ctx.getText());
            log.trace(format("|<----  exitStyleAttrName - '%s', style after: %s", ctx.getText(), styleBuilder.build()));
        }

        @Override
        public void exitFgColorName(AnsiTextParser.FgColorNameContext ctx) {
            styleBuilder.acceptByName(ctx.getText());
            log.trace(format("|<----  exitFgColorName - '%s', style after: %s", ctx.getText(), styleBuilder.build()));
        }

        @Override
        public void exitFgColor256(AnsiTextParser.FgColor256Context ctx) {
            int color256 = fromHex(ctx.getText(), patternHex256,
                "could not convert the '#'-prefixed hex-value of foreground 256-color '%s'");
            styleBuilder.accept(AnsiStyleAttr.fg(color256));
            log.trace(format("|<----  exitFgColor256 - '%s', style after: %s", ctx.getText(), styleBuilder.build()));
        }

        @Override
        public void exitBgColorName(AnsiTextParser.BgColorNameContext ctx) {
            styleBuilder.acceptByName("bg(" + ctx.getText() + ")");
            log.trace(format("|<----  exitBgColorName - '%s', style after: %s", ctx.getText(), styleBuilder.build()));
        }

        @Override
        public void exitBgColor256(AnsiTextParser.BgColor256Context ctx) {
            int color256 = fromHex(ctx.getText(), patternHex256,
                "could not convert the '#'-prefixed hex-value of background 256-color '%s'");
            styleBuilder.accept(AnsiStyleAttr.bg(color256));
            log.trace(format("|<----  exitBgColor256 - '%s', style after: %s", ctx.getText(), styleBuilder.build()));
        }

        @Override
        public void exitFgColorRGB(AnsiTextParser.FgColorRGBContext ctx) {
            int colorRGB = fromHex(ctx.getText(), patternHexRGB,
                "could not convert the '#'-prefixed hex-value of foreground RGB-color '%s'");
            int red =   (colorRGB & 0xFF0000) >> 16;
            int green = (colorRGB & 0x00FF00) >> 8;
            int blue =   colorRGB & 0x0000FF;
            styleBuilder.accept(AnsiStyleAttr.fg(red, green, blue));
            log.trace(format("|<----  exitFgColorRGB - '%s', style after: %s #%X rgb(%d, %d, %d)",
                ctx.getText(), styleBuilder.build(), colorRGB, red, green, blue));
        }

        @Override
        public void exitBgColorRGB(AnsiTextParser.BgColorRGBContext ctx) {
            int colorRGB = fromHex(ctx.getText(), patternHexRGB,
                "could not convert the '#'-prefixed hex-value of background RGB-color '%s'");
            int red =   (colorRGB & 0xFF0000) >> 16;
            int green = (colorRGB & 0x00FF00) >> 8;
            int blue =   colorRGB & 0x0000FF;
            styleBuilder.accept(AnsiStyleAttr.bg(red, green, blue));
            log.trace(format("|<----  exitBgColorRGB - '%s', style after: %s #%X rgb(%d, %d, %d)",
                ctx.getText(), styleBuilder.build(), colorRGB, red, green, blue));
        }

        @Override
        public void visitTerminal(TerminalNode node) {
//            log.trace(format("|| visit %s", dumpTerminalNode(node)));
            if (node.getSymbol().getType() == AnsiTextLexer.CRLF) {
                newLine();
            }
        }

        private final static Pattern patternHex256 = Pattern.compile("#([0-9A-Fa-f]{2})");
        private final static Pattern patternHexRGB = Pattern.compile("#([0-9A-Fa-f]{6})");

        private static int fromHex(String strColor, Pattern pattern, String fmtErrMsg) {
            Matcher matcher = pattern.matcher(strColor);
            if (matcher.find()) {
                int returnValue = Integer.valueOf(matcher.group(1), 16);
                log.trace(format("fromHex('%s', '%s') = 0x%X", strColor, pattern.pattern(), returnValue));
                return returnValue;
            } else {
                throw new IllegalArgumentException(format(fmtErrMsg, strColor));
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
