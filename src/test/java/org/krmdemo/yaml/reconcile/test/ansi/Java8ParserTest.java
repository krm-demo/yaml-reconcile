package org.krmdemo.yaml.reconcile.test.ansi;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;
import org.krmdemo.yaml.reconcile.ansi.Java8Lexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.krmdemo.yaml.reconcile.ansi.Java8Parser;
import org.krmdemo.yaml.reconcile.ansi.UppercaseMethodListener;

import static org.assertj.core.api.Assertions.assertThat;

public class Java8ParserTest {

    @Test
    void testUpperCaseMethod() {
        String javaClassContent = "public class SampleClass { void DoSomething(){} }";
        Java8Lexer java8Lexer = new Java8Lexer(CharStreams.fromString(javaClassContent));
        CommonTokenStream tokens = new CommonTokenStream(java8Lexer);
        Java8Parser java8Parser = new Java8Parser(tokens);
        ParseTree tree = java8Parser.compilationUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        UppercaseMethodListener uppercaseMethodListener = new UppercaseMethodListener();
        walker.walk(uppercaseMethodListener, tree);

        assertThat(uppercaseMethodListener.getErrors())
            .hasSize(1);
        assertThat(uppercaseMethodListener.getErrors().getFirst())
            .isEqualTo("Method 'DoSomething' is uppercased!");
    }
}
