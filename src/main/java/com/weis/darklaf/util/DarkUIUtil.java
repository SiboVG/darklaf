package com.weis.darklaf.util;

import com.bulenkov.iconloader.util.ColorUtil;
import com.bulenkov.iconloader.util.DoubleColor;
import com.bulenkov.iconloader.util.Gray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.awt.SunToolkit;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public final class DarkUIUtil {

    public final static AlphaComposite ALPHA_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
    public final static AlphaComposite SHADOW_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f);
    public static final boolean USE_QUARTZ = "true".equals(System.getProperty("apple.awt.graphics.UseQuartz"));

    private static Color getErrorGlow() {
        return UIManager.getColor("Glow.error");
    }

    private static Color getErrorFocusGlow() {
        return UIManager.getColor("Glow.errorFocus");
    }

    private static Color getFocusGlow() {
        return UIManager.getColor("Glow.focus");
    }

    private static Color getWarningGlow() {
        return UIManager.getColor("Glow.warning");
    }

    public static void paintOutlineBorder(final Graphics2D g, final int width, final int height, final float arc,
                                          final boolean symmetric, final boolean hasFocus, final Outline type) {
        type.setGraphicsColor(g, hasFocus);
        doPaint(g, width, height, arc, symmetric);
    }

    public static void paintFocusBorder(final Graphics2D g, final int width, final int height, final float arc,
                                        final boolean symmetric) {
        Outline.focus.setGraphicsColor(g, true);
        doPaint(g, width, height, arc, symmetric);
    }

    public static void paintFocusOval(final Graphics2D g, final int x, final int y, final int width, final int height) {
        paintFocusOval(g, (float) x, (float) y, (float) width, (float) height);
    }

    public static void paintFocusOval(final Graphics2D g, final float x, final float y,
                                      final float width, final float height) {
        Outline.focus.setGraphicsColor(g, true);

        float blw = 2f + 1f;
        Path2D shape = new Path2D.Float(Path2D.WIND_EVEN_ODD);
        shape.append(new Ellipse2D.Float(x - blw, y - blw, width + blw * 2, height + blw * 2), false);
        shape.append(new Ellipse2D.Float(x, y, width, height), false);
        g.fill(shape);
    }

    public static void paintLineBorder(final Graphics2D g, final float x, final float y,
                                       final float width, final float height, final int arc, final boolean growByLW) {
        float lw = 0.5f;
        float adj = growByLW ? lw : 0;
        var config = GraphicsUtil.setupStrokePainting(g);
        Path2D border = new Path2D.Float(Path2D.WIND_EVEN_ODD);
        border.append(new RoundRectangle2D.Float(x - adj, y - adj, width + 2 * adj, height + 2 * adj,
                                                 arc + lw, arc + lw), false);
        border.append(new RoundRectangle2D.Float(x + 2 * lw - adj, y + 2 * lw - adj,
                                                 width - 4 * lw + 2 * adj, height - 4 * lw + 2 * adj,
                                                 arc, arc), false);
        g.fill(border);
        config.restore();
    }


    @SuppressWarnings("SuspiciousNameCombination")
    private static void doPaint(@NotNull final Graphics2D g, final int width, final int height, final float arc,
                                final boolean symmetric) {
        float bw = 2f;
        float lw = 1f;
        var context = GraphicsUtil.setupStrokePainting(g);
        float outerArc = arc > 0 ? arc + bw - 2f : bw;
        float rightOuterArc = symmetric ? outerArc : 6f;
        Path2D outerRect = new Path2D.Float(Path2D.WIND_EVEN_ODD);
        outerRect.moveTo(width - rightOuterArc, 0);
        outerRect.quadTo(width, 0, width, rightOuterArc);
        outerRect.lineTo(width, height - rightOuterArc);
        outerRect.quadTo(width, height, width - rightOuterArc, height);
        outerRect.lineTo(outerArc, height);
        outerRect.quadTo(0, height, 0, height - outerArc);
        outerRect.lineTo(0, outerArc);
        outerRect.quadTo(0, 0, outerArc, 0);
        outerRect.closePath();

        bw += lw;
        float rightInnerArc = symmetric ? outerArc : 7f;
        Path2D innerRect = new Path2D.Float(Path2D.WIND_EVEN_ODD);
        innerRect.moveTo(width - rightInnerArc, bw);
        innerRect.quadTo(width - bw, bw, width - bw, rightInnerArc);
        innerRect.lineTo(width - bw, height - rightInnerArc);
        innerRect.quadTo(width - bw, height - bw, width - rightInnerArc, height - bw);
        innerRect.lineTo(outerArc, height - bw);
        innerRect.quadTo(bw, height - bw, bw, height - outerArc);
        innerRect.lineTo(bw, outerArc);
        innerRect.quadTo(bw, bw, outerArc, bw);
        innerRect.closePath();

        Path2D path = new Path2D.Float(Path2D.WIND_EVEN_ODD);
        path.append(outerRect, false);
        path.append(innerRect, false);
        g.fill(path);
        context.restore();
    }

    @NotNull
    public static Color blendColors(@NotNull final Color color1, @NotNull final Color color2, final double percent) {
        double inverse_percent = 1.0 - percent;
        int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
        int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }

    public static void applyInsets(final Rectangle rect, final Insets insets) {
        if (insets != null && rect != null) {
            rect.x += insets.left;
            rect.y += insets.top;
            rect.width -= (insets.right + rect.x);
            rect.height -= (insets.bottom + rect.y);
        }
    }

    public static boolean hasFocus(final Component c) {
        final Component owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        final Component owner2 = FocusManager.getCurrentManager().getFocusOwner();
        return (owner != null && SwingUtilities.isDescendingFrom(owner, c))
               || (owner2 != null && SwingUtilities.isDescendingFrom(owner2, c));
    }

    @Nullable
    public static <T> T getParentOfType(final Class<? extends T> cls, final Component c) {
        for (Component eachParent = c; eachParent != null; eachParent = eachParent.getParent()) {
            if (cls.isAssignableFrom(eachParent.getClass())) {
                return (T) eachParent;
            }
        }
        return null;
    }

    //Todo: COlors
    public static Color getTreeSelectionBackground(final boolean focused) {
        return focused ? getTreeSelectionBackground() : getTreeUnfocusedSelectionBackground();
    }

    private static Color getTreeSelectionBackground() {
        return UIManager.getColor("Tree.selectionBackground");
    }

    public static Color getTreeUnfocusedSelectionBackground() {
        Color background = getTreeTextBackground();
        return (Color) (ColorUtil.isDark(background) ? new DoubleColor(Gray._30, new Color(13, 41, 62)) : Gray._212);
    }

    public static Color getTreeTextBackground() {
        return UIManager.getColor("Tree.textBackground");
    }

    public static int getFocusAcceleratorKeyMask() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        if (tk instanceof SunToolkit) {
            return ((SunToolkit)tk).getFocusAcceleratorKeyMask();
        }
        return ActionEvent.ALT_MASK;
    }

    public static Object getUIOfType(final ComponentUI ui, final Class<?> klass) {
        if (klass.isInstance(ui)) {
            return ui;
        }
        return null;
    }


    public enum Outline {
        error {
            @Override
            public void setGraphicsColor(final Graphics2D g, final boolean focused) {
                if (focused) {
                    g.setColor(getErrorFocusGlow());
                } else {
                    g.setColor(getErrorGlow());
                }
            }
        },

        warning {
            @Override
            public void setGraphicsColor(final Graphics2D g, final boolean focused) {
                g.setColor(getWarningGlow());
            }
        },

        defaultButton {
            @Override
            public void setGraphicsColor(final Graphics2D g, final boolean focused) {
                if (focused) {
                    g.setColor(getFocusGlow());
                }
            }
        },

        focus {
            @Override
            public void setGraphicsColor(final Graphics2D g, final boolean focused) {
                if (focused) {
                    g.setColor(getFocusGlow());
                }
            }
        };

        public abstract void setGraphicsColor(Graphics2D g, boolean focused);
    }
}
