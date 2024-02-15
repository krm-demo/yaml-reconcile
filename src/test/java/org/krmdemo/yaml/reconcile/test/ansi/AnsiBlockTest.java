package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.krmdemo.yaml.reconcile.ansi.AnsiBlock;
import org.krmdemo.yaml.reconcile.ansi.AnsiLine;
import org.krmdemo.yaml.reconcile.ansi.AnsiStyle;
import org.krmdemo.yaml.reconcile.ansi.AnsiText;

import static java.lang.Math.max;
import static java.lang.String.format;
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
import static org.krmdemo.yaml.reconcile.ansi.AnsiText.ansiLine;

/**
 * Unit-test to check the functionality of {@link AnsiBlock} class
 */
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
    void testAnsiTextBlock() {

    }

    @Test
    void testBlockLeft() {
        AnsiBlock ansiBlock = AnsiBlock.builder()
            .ansiText(ansiText)
            .horizontal(AnsiBlock.Horizontal.LEFT)
            .style(bg(255))
            .build();
        assertThat(ansiBlock.height()).isEqualTo(5);
        assertThat(ansiBlock.width()).isEqualTo(54);
        System.out.println("------ ansiBlock(Left).renderAnsi(): -----------");
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
        System.out.println("------ ansiBlock(Left).content(): --------------");
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
        System.out.println("------ ansiBlock(Right).renderAnsi(): -----------");
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
        System.out.println("------ ansiBlock(Right).content(): --------------");
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
        System.out.println("------ ansiBlock(Center).renderAnsi(): -----------");
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
        System.out.println("------ ansiBlock(Center).content(): --------------");
        System.out.println(ansiBlock.content());
        assertThat(ansiBlock.content()).isEqualTo(
            "some text with bold and red fragment at the first line" + lineSeparator() +
            "   and some blue and italic fragment at the second    " + lineSeparator() +
            "                and at the third line                 " + lineSeparator() +
            "                                                      " + lineSeparator() +
            "                   bold -©- marker                    "
        );
    }

    @Test
    void testBlockRightCut() {
        int contentWidth = 30;
        AnsiBlock ansiBlock = AnsiBlock.builder()
            .ansiText(ansiText)
            .contentWidth(contentWidth)
            .horizontal(AnsiBlock.Horizontal.LEFT)
            .leftIndentWidth(10)
            .leftIndent(lineNum -> ansiLine(format("@|fg(#f9) line @|bold #%d|@:", lineNum)))
            .rightIndentWidth(15)
            .rightIndent(lineNum -> ansiLine(format("@|fg(#f9) :width(@|bold %d/%d|@)",
                ansiText.lineWidthAt(lineNum), max(0, ansiText.lineWidthAt(lineNum) - contentWidth))))
            .style(bg(240, 240, 255))
            .build();
        assertThat(ansiBlock.height()).isEqualTo(5);
        assertThat(ansiBlock.width()).isEqualTo(55);
        System.out.println("------ ansiBlock(RightCut).renderAnsi(): -----------");
        System.out.println(ansiBlock.renderAnsi());
        System.out.println("------ ansiBlock(RightCut).content(): --------------");
        System.out.println(ansiBlock.content());
        assertThat(ansiBlock.content() + lineSeparator()).isEqualTo("""
              line #0:some text with bold and red fr:width(54/24) \s
              line #1:and some blue and italic fragm:width(47/17) \s
              line #2:and at the third line         :width(21/0)  \s
              line #3:                              :width(0/0)   \s
              line #4: bold -©- marker              :width(17/0)  \s
            """);
    }

    @Test
    void testBlockLeftCut() {
        int contentWidth = 30;
        AnsiBlock ansiBlock = AnsiBlock.builder()
            .ansiText(ansiText)
            .contentWidth(contentWidth)
            .horizontal(AnsiBlock.Horizontal.RIGHT)
            .leftIndentWidth(10)
            .leftIndent(lineNum -> ansiLine(format("@|fg(#f9) line @|bold #%d|@:", lineNum)))
            .rightIndentWidth(15)
            .rightIndent(lineNum -> ansiLine(format("@|fg(#f9) :width(@|bold %d/%d|@)",
                ansiText.lineWidthAt(lineNum), max(0, ansiText.lineWidthAt(lineNum) - contentWidth))))
            .style(bg(240, 240, 255))
            .build();
        assertThat(ansiBlock.height()).isEqualTo(5);
        assertThat(ansiBlock.width()).isEqualTo(55);
        System.out.println("------ ansiBlock(LeftCut).renderAnsi(): -----------");
        System.out.println(ansiBlock.renderAnsi());
        System.out.println("------ ansiBlock(LeftCut).content(): --------------");
        System.out.println(ansiBlock.content());
        assertThat(ansiBlock.content() + lineSeparator()).isEqualTo("""
              line #0:red fragment at the first line:width(54/24) \s
              line #1: italic fragment at the second:width(47/17) \s
              line #2:         and at the third line:width(21/0)  \s
              line #3:                              :width(0/0)   \s
              line #4:              bold -©- marker :width(17/0)  \s
            """);
    }

    @Test
    @Disabled("TODO: fix it")
    void testBlockCenterCut() {
        int contentWidth = 37;
        AnsiBlock ansiBlock = AnsiBlock.builder()
            .ansiText(ansiText)
            .contentWidth(contentWidth)
            .horizontal(AnsiBlock.Horizontal.CENTER)
            .leftIndentWidth(10)
            .leftIndent(lineNum -> ansiLine(format("@|fg(#f9) line @|bold #%d|@:", lineNum)))
            .rightIndentWidth(15)
            .rightIndent(lineNum -> ansiLine(format("@|fg(#f9) :width(@|bold %d/%d|@)",
                ansiText.lineWidthAt(lineNum), max(0, ansiText.lineWidthAt(lineNum) - contentWidth))))
            .style(bg(240, 240, 255))
            .build();
        assertThat(ansiBlock.height()).isEqualTo(5);
        assertThat(ansiBlock.width()).isEqualTo(62);
        System.out.println("------ ansiBlock(CenterCut).renderAnsi(): -----------");
        System.out.println(ansiBlock.renderAnsi());
        System.out.println("------ ansiBlock(CenterCut).content(): --------------");
        System.out.println(ansiBlock.content());
        assertThat(ansiBlock.content() + lineSeparator()).isEqualTo("""
              line #0:red fragment at the first line:width(54/24) \s
              line #1: italic fragment at the second:width(47/17) \s
              line #2:         and at the third line:width(21/0)  \s
              line #3:                              :width(0/0)   \s
              line #4:              bold -©- marker :width(17/0)  \s
            """);
    }
}
