package org.krmdemo.yaml.reconcile.ansi;

import lombok.extern.slf4j.Slf4j;

import static java.lang.String.format;

@Slf4j
public class AnsiText {

    private final Listener listener = new Listener();

    public Listener listener() {
        return listener;
    }

    public static class Listener extends AnsiTextBaseListener {
        @Override
        public void enterText(AnsiTextParser.TextContext ctx) {
            log.debug("enterText");
        }

        @Override
        public void exitText(AnsiTextParser.TextContext ctx) {
            log.debug("exitText");
        }

        @Override
        public void enterLineLF(AnsiTextParser.LineLFContext ctx) {
            log.debug("enterLineLF");
        }

        @Override
        public void exitLineLF(AnsiTextParser.LineLFContext ctx) {
            log.debug("exitLineLF");
        }

        @Override
        public void enterLineOpen(AnsiTextParser.LineOpenContext ctx) {
            log.debug("enterLineOpen");
        }

        @Override
        public void exitLineOpen(AnsiTextParser.LineOpenContext ctx) {
            log.debug(format("exitLineOpen --> [%d;%d] '%s'",
                ctx.getStart().getStartIndex(),
                ctx.getStop().getStopIndex(),
                ctx.getText()));
        }
    }
}
