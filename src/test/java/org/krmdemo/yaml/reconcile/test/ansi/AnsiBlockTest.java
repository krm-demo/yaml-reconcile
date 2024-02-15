package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.krmdemo.yaml.reconcile.ansi.AnsiBlock;
import org.krmdemo.yaml.reconcile.ansi.AnsiText;

import static java.lang.System.lineSeparator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.yaml.reconcile.ansi.AnsiRenderCtx.renderCtx;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.ansiStyle;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.APPLY_BOLD;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.APPLY_ITALIC;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.Color.BLUE;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.Color.MAGENTA;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.Color.RED;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.bg;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.fg;

public class AnsiBlockTest {

    static AnsiText ansiText = AnsiText.ansiText("""
        some text @|bold with bold @|red and red|@ fragment|@ at the first line
        and @|blue some blue and @|italic italic fragment|@ at the second
        and at the|@ third line
        
        @|bold,magenta,bg(#FAFA00); bold @|!bold;-©-|@ marker |@""");

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
            //.horizontal(AnsiBlock.Horizontal.LEFT)  // <-- this is the default alignment
            .style(bg(255))
            .build();
        assertThat(ansiBlock.height()).isEqualTo(5);
        assertThat(ansiBlock.width()).isEqualTo(54);
        System.out.println("------ ansiBlock(LEFT).renderAnsi(): -----------");
        System.out.println(ansiBlock.renderAnsi());
        assertThat(ansiBlock.spanStylesOpen().toList()).containsExactly(
            // line #0 :
            ansiStyle(bg(255)),
                ansiStyle(bg(255),APPLY_BOLD),
                    ansiStyle(bg(255),APPLY_BOLD,fg(RED)),
                ansiStyle(bg(255),APPLY_BOLD),
            ansiStyle(bg(255)),
            // line #1 :
            ansiStyle(bg(255)),
                ansiStyle(bg(255),fg(BLUE)),
                    ansiStyle(bg(255),fg(BLUE),APPLY_ITALIC),
                ansiStyle(bg(255),fg(BLUE)),
            ansiStyle(bg(255)),
            // line #2 :
                ansiStyle(bg(255),fg(BLUE)),
            ansiStyle(bg(255)),
            // line #3 (empty) :
            ansiStyle(bg(255)),
            // line #4 :
                ansiStyle(APPLY_BOLD,fg(MAGENTA),bg(0xFA, 0xFA, 0x00)),
                    ansiStyle(fg(MAGENTA),bg(0xFA, 0xFA, 0x00)),
                ansiStyle(APPLY_BOLD,fg(MAGENTA),bg(0xFA, 0xFA, 0x00)),
            ansiStyle(bg(255))
        );
        assertThat(ansiBlock.lineAt(0).spanStylesOpen().toList()).containsExactly(
            ansiStyle(bg(255)),
                ansiStyle(bg(255),APPLY_BOLD),
                    ansiStyle(bg(255),APPLY_BOLD,fg(RED)),
                ansiStyle(bg(255),APPLY_BOLD),
            ansiStyle(bg(255))
        );
        assertThat(ansiBlock.lineAt(1).spanStylesOpen().toList()).containsExactly(
            ansiStyle(bg(255)),
                ansiStyle(bg(255),fg(BLUE)),
                    ansiStyle(bg(255),fg(BLUE),APPLY_ITALIC),
                ansiStyle(bg(255),fg(BLUE)),
            ansiStyle(bg(255))
        );
        assertThat(ansiBlock.lineAt(2).spanStylesOpen().toList()).containsExactly(
                ansiStyle(bg(255),fg(BLUE)),
            ansiStyle(bg(255))
        );
        assertThat(ansiBlock.lineAt(3).spanStylesOpen().toList()).containsExactly(
            ansiStyle(bg(255))
        );
        assertThat(ansiBlock.lineAt(4).spanStylesOpen().toList()).containsExactly(
                ansiStyle(APPLY_BOLD,fg(MAGENTA),bg(0xFA, 0xFA, 0x00)),
                    ansiStyle(fg(MAGENTA),bg(0xFA, 0xFA, 0x00)),
                ansiStyle(APPLY_BOLD,fg(MAGENTA),bg(0xFA, 0xFA, 0x00)),
            ansiStyle(bg(255))
        );
        System.out.println("------ ansiBlock(LEFT).content(): --------------");
        System.out.println(ansiBlock.content());
        assertThat(ansiBlock.content()).isEqualTo(
            "some text with bold and red fragment at the first line" + lineSeparator() +
            "and some blue and italic fragment at the second       " + lineSeparator() +
            "and at the third line                                 " + lineSeparator() +
            "                                                      " + lineSeparator() +
            " bold -©- marker                                      "
        );
    }

    @Test
    void testBlockRight() {
        AnsiBlock ansiBlock = AnsiBlock.builder()
            .ansiText(ansiText)
            .horizontal(AnsiBlock.Horizontal.RIGHT)
            .style(bg(255))
            .build();
        assertThat(ansiBlock.height()).isEqualTo(5);
        assertThat(ansiBlock.width()).isEqualTo(54);
        System.out.println("------ ansiBlock(RIGHT).renderAnsi(): -----------");
        System.out.println(ansiBlock.renderAnsi());
        assertThat(ansiBlock.lineAt(0).spanStylesOpen().toList()).containsExactly(
            ansiStyle(bg(255)),
                ansiStyle(bg(255),APPLY_BOLD),
                    ansiStyle(bg(255),APPLY_BOLD,fg(RED)),
                ansiStyle(bg(255),APPLY_BOLD),
            ansiStyle(bg(255))
        );
        assertThat(ansiBlock.lineAt(1).spanStylesOpen().toList()).containsExactly(
            ansiStyle(bg(255)),
                ansiStyle(bg(255),fg(BLUE)),
                    ansiStyle(bg(255),fg(BLUE),APPLY_ITALIC),
                ansiStyle(bg(255),fg(BLUE))
        );
        assertThat(ansiBlock.lineAt(2).spanStylesOpen().toList()).containsExactly(
            ansiStyle(bg(255)),
                ansiStyle(bg(255),fg(BLUE)),
            ansiStyle(bg(255))
        );
        assertThat(ansiBlock.lineAt(3).spanStylesOpen().toList()).containsExactly(
            ansiStyle(bg(255))
        );
        assertThat(ansiBlock.lineAt(4).spanStylesOpen().toList()).containsExactly(
            ansiStyle(bg(255)),
                ansiStyle(APPLY_BOLD,fg(MAGENTA),bg(0xFA, 0xFA, 0x00)),
                    ansiStyle(fg(MAGENTA),bg(0xFA, 0xFA, 0x00)),
                ansiStyle(APPLY_BOLD,fg(MAGENTA),bg(0xFA, 0xFA, 0x00))
        );
        System.out.println("------ ansiBlock(RIGHT).content(): --------------");
        System.out.println(ansiBlock.content());
        assertThat(ansiBlock.content()).isEqualTo(
            "some text with bold and red fragment at the first line" + lineSeparator() +
            "       and some blue and italic fragment at the second" + lineSeparator() +
            "                                 and at the third line" + lineSeparator() +
            "                                                      " + lineSeparator() +
            "                                      bold -©- marker "
        );
    }

    @Test
    void testBlockCenter() {
        AnsiBlock ansiBlock = AnsiBlock.builder()
            .ansiText(ansiText)
            .horizontal(AnsiBlock.Horizontal.CENTER)
            .style(bg(255))
            .build();
        assertThat(ansiBlock.height()).isEqualTo(5);
        assertThat(ansiBlock.width()).isEqualTo(54);
        System.out.println("------ ansiBlock(CENTER).renderAnsi(): -----------");
        System.out.println(ansiBlock.renderAnsi());
        assertThat(ansiBlock.spanStylesOpen().toList()).containsExactly(
            // line #0 :
            ansiStyle(bg(255)),
                ansiStyle(bg(255),APPLY_BOLD),
                    ansiStyle(bg(255),APPLY_BOLD,fg(RED)),
                ansiStyle(bg(255),APPLY_BOLD),
            ansiStyle(bg(255)),
            // line #1 :
            ansiStyle(bg(255)),
                ansiStyle(bg(255),fg(BLUE)),
                    ansiStyle(bg(255),fg(BLUE),APPLY_ITALIC),
                ansiStyle(bg(255),fg(BLUE)),
            ansiStyle(bg(255)),
            // line #2 :
            ansiStyle(bg(255)),
                ansiStyle(bg(255),fg(BLUE)),
            ansiStyle(bg(255)),
            // line #3 (empty) :
            ansiStyle(bg(255)),
            // line #4 :
            ansiStyle(bg(255)),
                ansiStyle(APPLY_BOLD,fg(MAGENTA),bg(0xFA, 0xFA, 0x00)),
                    ansiStyle(fg(MAGENTA),bg(0xFA, 0xFA, 0x00)),
                ansiStyle(APPLY_BOLD,fg(MAGENTA),bg(0xFA, 0xFA, 0x00)),
            ansiStyle(bg(255))
        );
        System.out.println("------ ansiBlock(CENTER).content(): --------------");
        System.out.println(ansiBlock.content());
        assertThat(ansiBlock.content()).isEqualTo(
            "some text with bold and red fragment at the first line" + lineSeparator() +
            "   and some blue and italic fragment at the second    " + lineSeparator() +
            "                and at the third line                 " + lineSeparator() +
            "                                                      " + lineSeparator() +
            "                   bold -©- marker                    "
        );
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
