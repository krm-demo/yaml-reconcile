package org.krmdemo.yaml.reconcile.ansi;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.text.StringEscapeUtils.escapeJava;
import static org.krmdemo.yaml.reconcile.ansi.AnsiRenderCtx.renderCtx;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.empty;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.emptyBuilder;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.resetAll;

/**
 * This class represents the multi-line text with global outer ansi-style and sequential fragments of
 * {@link AnsiSpan}s, which are grouped by {@link Line})
 * <p/>
 * The class is not immutable and could also be used as a builder of its content
 * during parsing the formatted ansi-text or programmatically.
 */
@Slf4j
public class AnsiText implements AnsiSize {

    /**
     * A mutable parsing-line of multi-line {@link AnsiText} (without trailing line-separator),
     * which consist of continues sequence of spans with different ansi-styles of any two siblings.
     */
    private static class Line {
        private final List<AnsiSpan> spans = new ArrayList<>();

        public Stream<AnsiSpan> spans() {
            return spans.stream();
        }

        /**
         * @return content of text-line without any ansi-styles
         */
        public String content() {
            return spans().map(AnsiSpan::content).collect(joining());
        }

        public int width() {
            return spans().mapToInt(AnsiSpan::width).sum();
        }

        public String renderAnsi() {
            return spans().map(AnsiSpan::renderAnsi).collect(joining());
        }

        /**
         * @return dump the {@link AnsiText.Line} object for debug purposes
         */
        public String dump() {
            if (spans.isEmpty()) {
                return ":: empty line ::";
            } else {
                return format(":: line of %d spans ( width = %3d ) ::%n%s",
                    spans.size(), width(), spans().map(AnsiSpan::dump).collect(joining(lineSeparator())));
            }
        }

        public void appendSpan(AnsiStyle style, String content) {
            spans.add(AnsiSpan.create(style, content));
        }
    }

    private final List<Line> lines = new ArrayList<>();

    private AnsiStyle.Builder styleBuilder = empty().builder();
    private final LinkedList<AnsiStyle> styleStack = new LinkedList<>();

    @Override
    public int height() {
        return lines.size();
    }

    @Override
    public int width() {
        return lines.stream().mapToInt(Line::width).max().orElse(0);
    }

    public Stream<AnsiSpan> lineSpansAt(int lineNum) {
        return lineNum < 0 || lineNum >= lines.size() ? Stream.empty() : lines.get(lineNum).spans();
    }

    public int lineWidthAt(int lineNum) {
        return lineNum < 0 || lineNum >= lines.size() ? 0 : lines.get(lineNum).width();
    }

    public AnsiLine lineAt(int lineNum) {
        return lineNum < 0 || lineNum >= lines.size() ?
            AnsiLine.empty() : AnsiLine.create(null, lineSpansAt(lineNum));
    }

    /**
     * @return sequence of ansi-styles used in this text
     */
    public Stream<AnsiStyle> spanStyles() {
        return lines.stream().flatMap(Line::spans).map(AnsiSpan::style).flatMap(Optional::stream);
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

    public static AnsiText ansiText() {
        return new AnsiText();
    }

    public static AnsiText ansiText(String text) {
        AnsiText ansiText = new AnsiText();
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
            // TODO: check that only color-name is used (prohibit 'bold', 'italic', ...)
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
        public void exitEscSeqAttrCode(AnsiTextParser.EscSeqAttrCodeContext ctx) {
            int startBegin = ctx.getStart() == null ? -1 : ctx.getStart().getStartIndex();
            int startEnd = ctx.getStart() == null ? -1 : ctx.getStart().getStopIndex();
            int stopBegin = ctx.getStop() == null ? -1 : ctx.getStop().getStartIndex();
            int stopEnd = ctx.getStop() == null ? -1 : ctx.getStop().getStopIndex();
            styleBuilder.acceptByName(ctx.getText());
            log.trace(format("|<----  exitEscSeqAttrCode - '%s' (%d;%d)..(%d;%d), style after: %s",
                ctx.getText(), startBegin, startEnd, stopBegin, stopEnd, styleBuilder.build()));
        }

        @Override
        public void exitFgEscColor256(AnsiTextParser.FgEscColor256Context ctx) {
            int color256 = ctx.getChildCount() < 2 ? -1 : parseInt(ctx.getChild(1).getText());
            int startBegin = ctx.getStart() == null ? -1 : ctx.getStart().getStartIndex();
            int startEnd = ctx.getStart() == null ? -1 : ctx.getStart().getStopIndex();
            int stopBegin = ctx.getStop() == null ? -1 : ctx.getStop().getStartIndex();
            int stopEnd = ctx.getStop() == null ? -1 : ctx.getStop().getStopIndex();
            styleBuilder.accept(AnsiStyleAttr.fg(color256));
            log.trace(format("|<----  exitFgEscColor256(%d) : fg(%d) (%d;%d)..(%d;%d), style after: %s",
                ctx.children.size(), color256,
                startBegin, startEnd, stopBegin, stopEnd,
                styleBuilder.build()));
        }

        @Override
        public void exitBgEscColor256(AnsiTextParser.BgEscColor256Context ctx) {
            int color256 = ctx.getChildCount() < 2 ? -1 : parseInt(ctx.getChild(1).getText());
            int startBegin = ctx.getStart() == null ? -1 : ctx.getStart().getStartIndex();
            int startEnd = ctx.getStart() == null ? -1 : ctx.getStart().getStopIndex();
            int stopBegin = ctx.getStop() == null ? -1 : ctx.getStop().getStartIndex();
            int stopEnd = ctx.getStop() == null ? -1 : ctx.getStop().getStopIndex();
            styleBuilder.accept(AnsiStyleAttr.bg(color256));
            log.trace(format("|<----  exitBgEscColor256(%d) : bg(%d) (%d;%d)..(%d;%d), style after: %s",
                ctx.children.size(), color256,
                startBegin, startEnd, stopBegin, stopEnd,
                styleBuilder.build()));
        }

        @Override
        public void exitFgEscColorRGB(AnsiTextParser.FgEscColorRGBContext ctx) {
            MatchResult mrRGB = matchRGB(ctx, "could not convert the ';'-separated foreground RGB-color '%s'");
            int red =   parseInt(mrRGB.group(1));
            int green = parseInt(mrRGB.group(2));
            int blue =  parseInt(mrRGB.group(3));
            int startBegin = ctx.getStart() == null ? -1 : ctx.getStart().getStartIndex();
            int startEnd = ctx.getStart() == null ? -1 : ctx.getStart().getStopIndex();
            int stopBegin = ctx.getStop() == null ? -1 : ctx.getStop().getStartIndex();
            int stopEnd = ctx.getStop() == null ? -1 : ctx.getStop().getStopIndex();
            styleBuilder.accept(AnsiStyleAttr.fg(red, green, blue));
            log.trace(format("|<----  exitFgEscColorRGB(%d) : fg(%d, %d, %d) (%d;%d)..(%d;%d), style after: %s",
                ctx.children.size(), red, green, blue,
                startBegin, startEnd, stopBegin, stopEnd,
                styleBuilder.build()));
        }

        @Override
        public void exitBgEscColorRGB(AnsiTextParser.BgEscColorRGBContext ctx) {
            MatchResult mrRGB = matchRGB(ctx, "could not convert the ';'-separated background RGB-color '%s'");
            int red =   parseInt(mrRGB.group(1));
            int green = parseInt(mrRGB.group(2));
            int blue =  parseInt(mrRGB.group(3));
            int startBegin = ctx.getStart() == null ? -1 : ctx.getStart().getStartIndex();
            int startEnd = ctx.getStart() == null ? -1 : ctx.getStart().getStopIndex();
            int stopBegin = ctx.getStop() == null ? -1 : ctx.getStop().getStartIndex();
            int stopEnd = ctx.getStop() == null ? -1 : ctx.getStop().getStopIndex();
            styleBuilder.accept(AnsiStyleAttr.bg(red, green, blue));
            log.trace(format("|<----  exitBgEscColorRGB(%d) : fg(%d, %d, %d) (%d;%d)..(%d;%d), style after: %s",
                ctx.children.size(), red, green, blue,
                startBegin, startEnd, stopBegin, stopEnd,
                styleBuilder.build()));
        }

        @Override
        public void enterEscSeq(AnsiTextParser.EscSeqContext ctx) {
            log.trace(format("|---> enterEscSeq - in stack (%d) %s", styleStack.size(), styleBuilder.build()));
        }

        @Override
        public void exitEscSeq(AnsiTextParser.EscSeqContext ctx) {
            int startBegin = ctx.getStart() == null ? -1 : ctx.getStart().getStartIndex();
            int startEnd = ctx.getStart() == null ? -1 : ctx.getStart().getStopIndex();
            int stopBegin = ctx.getStop() == null ? -1 : ctx.getStop().getStartIndex();
            int stopEnd = ctx.getStop() == null ? -1 : ctx.getStop().getStopIndex();
            log.trace(format("|<---  exitEscSeq(%d) - '%s' (%d;%d)..(%d;%d)",
                ctx.children.size(),
                escapeJava(ctx.getText()),
                startBegin, startEnd, stopBegin, stopEnd));
            //span(format("ESC(%s)", escapeJava(ctx.getText())));
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

        private final static Pattern patternEscRGB = Pattern.compile("(\\d+);(\\d+);(\\d+)");

        private static MatchResult matchRGB(ParserRuleContext ctxRGB, String fmtErrMsg) {
            if (ctxRGB.getChildCount() < 1) {
                throw new IllegalArgumentException(format(fmtErrMsg + " - missing token", ctxRGB.getText()));
            }
            String strRGB = ctxRGB.getChild(1).getText();
            Matcher matcher = patternEscRGB.matcher(strRGB);
            if (matcher.find()) {
                return matcher.toMatchResult();
            } else {
                throw new IllegalArgumentException(format(fmtErrMsg + " - no match to " + patternEscRGB.pattern(), strRGB));
            }
        }
   }
}
