package org.krmdemo.yaml.reconcile.ansi;

import java.util.*;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.ansiStyle;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.emptyStyle;
import static org.krmdemo.yaml.reconcile.ansi.Layout.blank;
import static org.krmdemo.yaml.reconcile.ansi.Layout.emptyLayout;
import static org.krmdemo.yaml.reconcile.ansi.LayoutBuilder.BuildContext.defaultContext;

public interface LayoutBuilder extends AnsiSize {

    Supplier<Layout> NO_PARENT = () -> null;

    Layout build();

    default Layout buildTopFrame(AnsiBorder border) { return emptyLayout(); }
    default Layout buildBottomFrame(AnsiBorder border) { return emptyLayout(); }
    default Layout buildLeftFrame(AnsiBorder border) { return emptyLayout(); }
    default Layout buildRightFrame(AnsiBorder border) { return emptyLayout(); }

    class BuildContext {
        private final BuildContext parent;
        private final AnsiBorder border;
        private final char paddingChar;

        private Layout main = null;

        private BuildContext(BuildContext parent, AnsiBorder border, char paddingChar) {
            this.parent = parent;
            this.border = border;
            this.paddingChar = paddingChar;
        }

        private final static BuildContext DEFAULT =
            new BuildContext(null, AnsiBorder.NONE, ' ');

        public static BuildContext defaultContext() {
            return BuildContext.DEFAULT;
        }

        public BuildContext create(LayoutBuilder builder) {
            return new BuildContext(this, useBorder(builder), usePaddingChar(builder));
        }

        public BuildContext withPaddingChar(char paddingChar) {
            return new BuildContext(this, this.border, paddingChar);
        }

        private AnsiBorder useBorder(LayoutBuilder builder) {
            return builder instanceof Base<?> b && b.border != null ? b.border : this.border;
        }

        private char usePaddingChar(LayoutBuilder builder) {
            return builder instanceof Base<?> b && b.paddingChar != null ? b.paddingChar : this.paddingChar;
        }

        Supplier<Layout> parentLayout() {
            return parent == null ? NO_PARENT : parent::layoutMain;
        }

        AnsiBorder border() {
            return this.border;
        }

        char paddingChar() {
            return this.paddingChar;
        }

        Layout layoutMain() {
            return this.main;
        }

        Layout storeLayoutMain(Layout layout) {
            if (layout == null) {
                throw new IllegalArgumentException("Created layout must not be null");
            }
            if (this.main != null) {
                throw new IllegalStateException("Attempt to store the created layout more than once");
            }
            this.main = layout;
            return layout;
        }
    }

    abstract class Base<B extends Base<B>> implements LayoutBuilder {
        protected AnsiStyle style = emptyStyle();
        protected AnsiBorder border = null;
        protected Character paddingChar = null;

        @SuppressWarnings("unchecked")
        protected B asThis() {
            return (B)this;
        }

        public B style(AnsiStyle style) {
            this.style = style;
            return asThis();
        }

        public B style(AnsiStyleAttr... styleAttrs) {
            this.style = ansiStyle(styleAttrs);
            return asThis();
        }

        public B border(AnsiBorder border) {
            this.border = border;
            return asThis();
        }

        public B paddingChar(char paddingChar) {
            this.paddingChar = paddingChar;
            return asThis();
        }

        @Override public int height()   { return height(defaultContext()); }
        @Override public int width()    { return width(defaultContext());  }
        @Override public Layout build() { return build(defaultContext());  }

        abstract int height(BuildContext ctx);
        abstract int width(BuildContext ctx);
        abstract Layout build(BuildContext parentCtx);
    }

    abstract class OuterSingle<B extends OuterSingle<B>> extends Base<B> {
        protected LayoutBuilder inner;

        B over(LayoutBuilder inner) {
            this.inner = inner;
            return asThis();
        }

        int innerHeight(BuildContext ctx) {
            return inner instanceof Base<?> b ? b.height(ctx) : inner.height();
        }

        int innerWidth(BuildContext ctx) {
            return inner instanceof Base<?> b ? b.width(ctx) : inner.width();
        }

        Layout innerBuild(BuildContext parentCtx) {
            return inner instanceof Base<?> b ? b.build(parentCtx) : inner.build();
        }
    }

    class AlignHorz extends OuterSingle<AlignHorz> {
        protected int targetWidth = 0;
        protected AlignHorizontal alignment;

        AlignHorz targetWidth(int targetWidth) {
            this.targetWidth = targetWidth;
            return asThis();
        }

        AlignHorz alignment(AlignHorizontal alignment) {
            this.alignment = alignment;
            return asThis();
        }

        @Override
        public int height(BuildContext ctx) {
            return innerHeight(ctx);
        }

        @Override
        public int width(BuildContext ctx) {
            return targetWidth;
        }

        @Override
        public Layout build(BuildContext parentCtx) {
            BuildContext ctx = parentCtx.create(this);

            int targetHeight = this.height(ctx);
            int targetWidth = this.targetWidth;
            int innerWidth = innerWidth(ctx);
            if (innerWidth >= targetWidth) {
                return ctx.storeLayoutMain(innerBuild(ctx));
            }
            int blankTotal = targetWidth - inner.width();
            int blankLeft = blankTotal / 2; // <-- it could be zero, if `blankTotal` equals to `1`
            int blankRight = blankTotal - blankLeft;
            if (alignment == AlignHorizontal.LEFT) {
                return ctx.storeLayoutMain(new Layout.Horizontal(ctx,
                    innerBuild(ctx),
                    blank(ctx, targetHeight, blankTotal)
                ));
            } else if (alignment == AlignHorizontal.RIGHT || targetWidth == innerWidth + 1) {
                return ctx.storeLayoutMain(new Layout.Horizontal(ctx,
                    blank(ctx, targetHeight, blankTotal),
                    innerBuild(ctx)
                ));
            } else { // it implies that alignment is AlignHorizontal.CENTER and both parts are not empty
                return ctx.storeLayoutMain(new Layout.Horizontal(ctx,
                    blank(ctx, targetHeight, blankLeft),
                    innerBuild(ctx),
                    blank(ctx, targetHeight, blankRight)
                ));
            }
        }
    }

    static AlignHorz alignHorz(LayoutBuilder inner, AlignHorizontal alignment, int targetWidth) {
        return new AlignHorz().over(inner).alignment(alignment).targetWidth(targetWidth);
    }

    class AlignVert extends OuterSingle<AlignVert> {
        protected int targetHeight = 0;
        protected AlignVertical alignment;

        AlignVert targetHeight(int targetHeight) {
            this.targetHeight = targetHeight;
            return asThis();
        }

        AlignVert alignment(AlignVertical alignment) {
            this.alignment = alignment;
            return asThis();
        }

        @Override
        public int height(BuildContext ctx) {
            return targetHeight;
        }

        @Override
        public int width(BuildContext ctx) {
            return innerWidth(ctx);
        }

        @Override
        public Layout build(BuildContext parentCtx) {
            BuildContext ctx = parentCtx.create(this);

            int targetHeight = this.targetHeight;
            int targetWidth = this.width(ctx);
            int innerHeight = innerHeight(ctx);
            if (innerHeight >= targetHeight) {
                return innerBuild(ctx);
            }
            int blankTotal = targetHeight - innerHeight;
            int blankTop = blankTotal / 2; // <-- it could be zero, if `blankTotal` equals to `1`
            int blankBottom = blankTotal - blankTop;
            if (alignment == AlignVertical.TOP) {
                return ctx.storeLayoutMain(new Layout.Vertical(ctx,
                    innerBuild(ctx),
                    blank(ctx, blankTotal, targetWidth)
                ));
            } else if (alignment == AlignVertical.BOTTOM || blankTop == 0) {
                return ctx.storeLayoutMain(new Layout.Vertical(ctx,
                    blank(ctx, blankTotal, targetWidth),
                    innerBuild(ctx)
                ));
            } else { // it implies that alignment is AlignVertical.MIDDLE and both parts are not empty
                return ctx.storeLayoutMain(new Layout.Vertical(ctx,
                    blank(ctx, blankTop, targetWidth),
                    innerBuild(ctx),
                    blank(ctx, blankBottom, targetWidth)
                ));
            }
        }
    }

    static AlignVert alignVert(LayoutBuilder inner, AlignVertical alignment, int targetHeight) {
        return new AlignVert().over(inner).alignment(alignment).targetHeight(targetHeight);
    }

    abstract class OuterComposite<B extends OuterComposite<B>> extends Base<B> {
        protected List<LayoutBuilder> innerItems = new ArrayList<>();

        public B append(LayoutBuilder inner) {
            innerItems.add(inner);
            return asThis();
        }

        static int itemHeight(BuildContext ctx, LayoutBuilder item) {
            return item instanceof Base<?> b ? b.height(ctx) : item.height();
        }

        static int itemWidth(BuildContext ctx, LayoutBuilder item) {
            return item instanceof Base<?> b ? b.width(ctx) : item.width();
        }

        int maxHeight(BuildContext ctx) {
            return innerItems.stream().mapToInt(item -> itemHeight(ctx, item)).max().orElse(0);
        }

        int maxWidth(BuildContext ctx) {
            return innerItems.stream().mapToInt(item -> itemWidth(ctx, item)).max().orElse(0);
        }

        int sumHeight(BuildContext ctx) {
            return innerItems.stream().mapToInt(item -> itemHeight(ctx, item)).sum();
        }

        int sumWidth(BuildContext ctx) {
            return innerItems.stream().mapToInt(item -> itemWidth(ctx, item)).sum();
        }

        int frameHeight(BuildContext ctx) {
            return 2 * ctx.border().borderHeight();  // <-- TODO: check whether it's a root frame
        }

        int frameWidth(BuildContext ctx) {
            return 2 * ctx.border().borderWidth();  // <-- TODO: check whether it's a root frame
        }

        int totalGridHeight(BuildContext ctx) {
            return 0;
        }

        int totalGridWidth(BuildContext ctx) {
            return 0;
        }
    }

    class Horizontal extends OuterComposite<Horizontal> {
        protected AlignVertical alignment;

        Horizontal alignment(AlignVertical alignment) {
            this.alignment = alignment;
            return asThis();
        }

        int totalGridWidth(BuildContext ctx) {
            return (innerItems.size() - 1) * ctx.border().borderWidth();
        }

        @Override
        public int height(BuildContext ctx) {
            return maxHeight(ctx) + frameHeight(ctx) + totalGridHeight(ctx);
        }

        @Override
        public int width(BuildContext ctx) {
            return sumWidth(ctx) + frameWidth(ctx) + totalGridWidth(ctx);
        }

        public Layout build(BuildContext parentCtx) {
            BuildContext ctx = parentCtx.create(this);
            int maxHeight = maxHeight(ctx);
            List<Layout> children = new ArrayList<>();
            for (LayoutBuilder inner : innerItems) {
                Layout innerLayout = alignVert(inner, alignment, maxHeight).build(ctx);
                if (innerLayout.isEmpty()) {
                    continue;
                }
                children.add(innerLayout);
                // TODO: add inner vertical grid-lines
            }
            return ctx.storeLayoutMain(new Layout.Horizontal(ctx, children));
        }
    }

    class Vertical extends OuterComposite<Vertical> {
        protected AlignHorizontal alignment;

        Vertical alignment(AlignHorizontal alignment) {
            this.alignment = alignment;
            return asThis();
        }

        int totalGridHeight(BuildContext ctx) {
            return (innerItems.size() - 1) * ctx.border().borderHeight();
        }

        @Override
        public int height(BuildContext ctx) {
            return sumHeight(ctx) + frameHeight(ctx) + totalGridHeight(ctx);
        }

        @Override
        public int width(BuildContext ctx) {
            return maxWidth(ctx) + frameWidth(ctx) + totalGridWidth(ctx);
        }

        public Layout build(BuildContext parentCtx) {
            BuildContext ctx = parentCtx.create(this);
            int maxWidth = maxWidth(ctx);
            List<Layout> children = new ArrayList<>();
            for (LayoutBuilder inner : innerItems) {
                Layout innerLayout = alignHorz(inner, alignment, maxWidth).build(ctx);
                if (innerLayout.isEmpty()) {
                    continue;
                }
                children.add(innerLayout);
                // TODO: add inner horizontal grid-lines
            }
            return ctx.storeLayoutMain(new Layout.Horizontal(ctx, children));
        }
    }
}
