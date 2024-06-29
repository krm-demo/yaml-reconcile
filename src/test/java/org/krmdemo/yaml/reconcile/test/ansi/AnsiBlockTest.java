package org.krmdemo.yaml.reconcile.test.ansi;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.krmdemo.yaml.reconcile.ansi.AlignHorizontal;
import org.krmdemo.yaml.reconcile.ansi.AnsiBlock;
import org.krmdemo.yaml.reconcile.ansi.AnsiText;
import org.krmdemo.yaml.reconcile.ansi.Layout;

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
@TestMethodOrder(MethodOrderer.DisplayName.class)
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
    @DisplayName("horizontal alignment to the left (no cut)")
    void testBlockLeft() {
        Layout ansiBlock = AnsiBlock.builder()
            .ansiText(ansiText)
            .alignment(AlignHorizontal.LEFT)
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
    @DisplayName("horizontal alignment to the right (no cut)")
    void testBlockRight() {
        Layout ansiBlock = AnsiBlock.builder()
            .ansiText(ansiText)
            .alignment(AlignHorizontal.RIGHT)
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
        assertThat(ansiBlock.content() + lineSeparator()).isEqualTo("""
            some text with bold and red fragment at the first line
                   and some blue and italic fragment at the second
                                             and at the third line
                                                                 \s
                                                  bold -©- marker\s
            """);
    }

    @Test
    @DisplayName("horizontal alignment to the center (no cut)")
    void testBlockCenter() {
        Layout ansiBlock = AnsiBlock.builder()
            .ansiText(ansiText)
            .alignment(AlignHorizontal.CENTER)
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
        assertThat(ansiBlock.content() + lineSeparator()).isEqualTo("""
            some text with bold and red fragment at the first line
               and some blue and italic fragment at the second   \s
                            and at the third line                \s
                                                                 \s
                               bold -©- marker                   \s
            """);
    }

    @Test
    @DisplayName("cut the right part with left alignment")
    void testBlockRightCut() {
        int contentWidth = 30;
        Layout ansiBlock = AnsiBlock.builder()
            .ansiText(ansiText)
            .contentWidth(contentWidth)
            .alignment(AlignHorizontal.LEFT)
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
    @DisplayName("cut the left part with right alignment")
    void testBlockLeftCut() {
        int contentWidth = 30;
        Layout ansiBlock = AnsiBlock.builder()
            .ansiText(ansiText)
            .contentWidth(contentWidth)
            .alignment(AlignHorizontal.RIGHT)
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
    @DisplayName("cut the center part with center alignment")
    void testBlockCenterCut() {
        int contentWidth = 37;
        Layout ansiBlock = AnsiBlock.builder()
            .ansiText(ansiText)
            .contentWidth(contentWidth)
            .alignment(AlignHorizontal.CENTER)
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
              line #0:t with bold and red fragment at the f:width(54/17) \s
              line #1:ome blue and italic fragment at the s:width(47/10) \s
              line #2:        and at the third line        :width(21/0)  \s
              line #3:                                     :width(0/0)   \s
              line #4:           bold -©- marker           :width(17/0)  \s
            """);
    }

    @Test
    @DisplayName("ansi-block from ansi-text with default alignment and no cut")
    void testAnsiTextBlocked() {
        Layout ansiBlock = ansiText.blocked();
        assertThat(ansiBlock.height()).isEqualTo(5);
        assertThat(ansiBlock.width()).isEqualTo(54);
        System.out.println("------ ansiText.blocked().renderAnsi(): -----------");
        System.out.println(ansiBlock.renderAnsi());
        System.out.println("------ ansiText.blocked().content(): --------------");
        System.out.println(ansiBlock.content());
        assertThat(ansiBlock.content() + lineSeparator()).isEqualTo("""
            some text with bold and red fragment at the first line
            and some blue and italic fragment at the second      \s
            and at the third line                                \s
                                                                 \s
             bold -©- marker                                     \s
            """);
        assertThat(ansiBlock.lineAt(0).spanStylesOpen().toList()).containsExactly(
            ansiStyle(),
            ansiStyle(APPLY_BOLD),
            ansiStyle(APPLY_BOLD,fg(RED)),
            ansiStyle(APPLY_BOLD),
            ansiStyle()
        );
        assertThat(ansiBlock.lineAt(1).spanStylesOpen().toList()).containsExactly(
            ansiStyle(),
            ansiStyle(fg(BLUE)),
            ansiStyle(fg(BLUE),APPLY_ITALIC),
            ansiStyle(fg(BLUE)),
            ansiStyle()
        );
        assertThat(ansiBlock.lineAt(2).spanStylesOpen().toList()).containsExactly(
            ansiStyle(fg(BLUE)),
            ansiStyle()
        );
        assertThat(ansiBlock.lineAt(3).spanStylesOpen().toList()).containsExactly(
            ansiStyle()
        );
        assertThat(ansiBlock.lineAt(4).spanStylesOpen().toList()).containsExactly(
            ansiStyle(APPLY_BOLD,fg(MAGENTA),bg(0xFA, 0xFA, 0x00)),
            ansiStyle(fg(MAGENTA),bg(0xFA, 0xFA, 0x00)),
            ansiStyle(APPLY_BOLD,fg(MAGENTA),bg(0xFA, 0xFA, 0x00)),
            ansiStyle()
        );
    }
}
