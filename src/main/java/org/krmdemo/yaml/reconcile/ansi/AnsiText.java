package org.krmdemo.yaml.reconcile.ansi;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;

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
                return ":: empty line  ::";
            } else {
                return format(":: width = %3d :: %s ::", builder.length(), builder );
            }
        }
    }

    private final List<LineBuilder> lines = new ArrayList<>();

    public Stream<String> renderAnsi() {
        return lines.stream().map(LineBuilder::renderAnsi);
    }

    public LineBuilder lastLine() {
        return lines.getLast();
    }

    public LineBuilder line() {
        lines.add(new LineBuilder());
        return lastLine();
    }

    public String dump() {
        return lines.stream()
            .map(LineBuilder::dump)
            .collect(joining(lineSeparator()));
    }

    public static AnsiText ansiText(String text) {
        AnsiText ansiText = new AnsiText();
        AnsiTextLexer lexer = new AnsiTextLexer(CharStreams.fromString(text));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        AnsiTextParser parser = new AnsiTextParser(tokens);
        ParseTree tree = parser.text();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(ansiText.new Listener(), tree);
        return ansiText;
    }

    private class Listener extends AnsiTextBaseListener {
        @Override
        public void enterText(AnsiTextParser.TextContext ctx) {
            log.debug("|-> Text");
        }

        @Override
        public void exitText(AnsiTextParser.TextContext ctx) {
            log.debug("|<- Text");
        }

        @Override
        public void enterLineLF(AnsiTextParser.LineLFContext ctx) {
            log.debug("|--> LineLF");
        }

        @Override
        public void exitLineLF(AnsiTextParser.LineLFContext ctx) {
            log.debug("|<-- LineLF");
        }

        @Override
        public void enterLineOpen(AnsiTextParser.LineOpenContext ctx) {
            log.debug(format("|---> enterLineOpen - %3d", lines.size() + 1));
            line();
        }

        @Override
        public void exitLineOpen(AnsiTextParser.LineOpenContext ctx) {
            String content = ctx.getText();
            lastLine().content(content);
            log.debug(format("|<---  exitLineOpen - %3d --> [%d;%d] '%s'",
                lines.size(),
                ctx.getStart().getStartIndex(),
                ctx.getStop().getStopIndex(),
                content));
        }
    }
}
