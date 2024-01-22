package org.krmdemo.yaml.reconcile.test.ansi;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;
import org.krmdemo.yaml.reconcile.ansi.AnsiText;
import org.krmdemo.yaml.reconcile.ansi.AnsiTextLexer;
import org.krmdemo.yaml.reconcile.ansi.AnsiTextParser;
import org.krmdemo.yaml.reconcile.ansi.Java8Lexer;
import org.krmdemo.yaml.reconcile.ansi.Java8Parser;
import org.krmdemo.yaml.reconcile.ansi.UppercaseMethodListener;

public class AnsiTextParserTest {

    AnsiText ansiText = new AnsiText();

    @Test
    void testMultiLines() {
        String threeLines = """
              This is the line 1
            This is the second one
            And this is the last
            """;
        AnsiTextLexer lexer = new AnsiTextLexer(CharStreams.fromString(threeLines));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        AnsiTextParser parser = new AnsiTextParser(tokens);
        ParseTree tree = parser.text();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(ansiText.listener(), tree);
    }
}
