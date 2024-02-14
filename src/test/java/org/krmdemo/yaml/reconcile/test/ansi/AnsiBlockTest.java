package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.krmdemo.yaml.reconcile.ansi.AnsiBlock;
import org.krmdemo.yaml.reconcile.ansi.AnsiText;

import static org.krmdemo.yaml.reconcile.ansi.AnsiRenderCtx.renderCtx;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.bg;

public class AnsiBlockTest {

    static AnsiText ansiText = AnsiText.ansiText("""
        some text @|bold with bold @|red and red|@ fragament|@ at the first line
        and @|blue some blue and @|italic italic fragment|@ at the second
        and at the|@ third line""");

    @BeforeAll
    static void beforeAll() {
        renderCtx().setLinePrefixResetAll(false);
        renderCtx().setLineSuffixResetAll(false);

        System.out.println("------ ansiText.renderAnsi(): -----------");
        System.out.println(ansiText.renderAnsi());
        System.out.println("------ ansiText.content(): --------------");
        System.out.println(ansiText.content());
    }

    @AfterAll
    static void afterAll() {
        System.out.println("------------------------------------------");
    }

    @Test
    void testBlock() {
        AnsiBlock ansiBlock = AnsiBlock.builder()
            .ansiText(ansiText)
            .style(bg(255))
            .build();
        System.out.println("------ ansiBlock.renderAnsi(): -----------");
        System.out.println(ansiBlock.renderAnsi());
        System.out.println("------ ansiBlock.content(): --------------");
        System.out.println(ansiBlock.content());
    }

    @ParameterizedTest(name = "[{index}] line[{0}]")
    @CsvSource(delimiterString = " ::: ", value = {
        "0 ::: some text @|bold with bold @|red and red|@ fragament|@ at the first line",
        "1 ::: some text @|bold with bold @|red and red|@ fragament|@ at the first line",
        "2 ::: some text @|bold with bold @|red and red|@ fragament|@ at the first line"
    })
    void testSubSpans(int lineNum) {
//        AnsiText.Line line = ansiText.lines().get(lineNum);
//        System.out.printf("------- ansiText.lines[%d].renderAnsi(): ----------------%n", lineNum);
//        System.out.printf("|%s|%n", line.renderAnsi());
//        System.out.printf("------- ansiText.lines[%d].content(): -------------------%n", lineNum);
//        System.out.printf("|%s|%n", line.content());
        //String subContent = AnsiTextUtils.subSpans(line.spans(), 0, 0);
    }
}
