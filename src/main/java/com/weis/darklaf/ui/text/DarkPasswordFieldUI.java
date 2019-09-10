package com.weis.darklaf.ui.text;

import com.weis.darklaf.decorators.MouseMovementListener;
import com.weis.darklaf.util.GraphicsContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;

public class DarkPasswordFieldUI extends DarkPasswordFieldUIBridge {

    private char echo_dot = '*';
    private boolean showTriggered = false;

    private final FocusListener focusListener = new FocusAdapter() {
        public void focusGained(final FocusEvent e) {
            getComponent().repaint();
        }

        public void focusLost(final FocusEvent e) {
            getComponent().repaint();
        }
    };
    private final MouseMotionListener mouseMotionListener = (MouseMovementListener) e -> updateCursor(e.getPoint());
    private final MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mousePressed(@NotNull final MouseEvent e) {
            if (isOverEye(e.getPoint())) {
                ((JPasswordField) getComponent()).setEchoChar((char) 0);
                showTriggered = true;
                getComponent().repaint();
            }
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            ((JPasswordField) getComponent()).setEchoChar(echo_dot);
            showTriggered = false;
            getComponent().repaint();
        }
    };
    private final KeyListener keyListener = new KeyAdapter() {
        @Override
        public void keyTyped(final KeyEvent e) {
            SwingUtilities.invokeLater(() -> {
                Point p = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(p, getComponent());
                updateCursor(p);
            });
        }
    };


    private boolean isOverEye(final Point p) {
        return showShowIcon() && DarkTextFieldUI.isOver(getShowIconCoord(), getShowIcon(), p);
    }

    private boolean showShowIcon() {
        var c = (JPasswordField) getComponent();
        char[] pw = c.getPassword();
        boolean show = pw.length > 0;
        Arrays.fill(pw, (char) 0);
        return show;
    }

    private void updateCursor(final Point p) {
        boolean insideTextArea = DarkTextFieldUI.getDrawingRect(getComponent()).contains(p)
                                 && p.x >= DarkTextFieldUI.getTextRect(getComponent()).x
                                 && p.x < getShowIconCoord().x;
        if (insideTextArea) {
            getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        } else if (isOverEye(p)) {
            getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            getComponent().setCursor(Cursor.getDefaultCursor());
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static ComponentUI createUI(final JComponent c) {
        return new DarkPasswordFieldUI();
    }

    @Override
    public void installUI(final JComponent c) {
        super.installUI(c);
        if (c instanceof JPasswordField) {
            echo_dot = ((JPasswordField) c).getEchoChar();
        }
    }

    protected void installListeners() {
        super.installListeners();
        var c = getComponent();
        c.addFocusListener(focusListener);
        c.addMouseListener(mouseListener);
        c.addMouseMotionListener(mouseMotionListener);
        c.addKeyListener(keyListener);
    }

    protected void uninstallListeners() {
        super.uninstallListeners();
        var c = getComponent();
        c.removeFocusListener(focusListener);
        c.removeMouseListener(mouseListener);
        c.removeMouseMotionListener(mouseMotionListener);
        c.removeKeyListener(keyListener);
    }

    protected void paintBackground(final Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        JTextComponent c = getComponent();

        Container parent = c.getParent();
        if (parent != null) {
            g.setColor(parent.getBackground());
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }

        Border border = c.getBorder();
        GraphicsContext config = new GraphicsContext(g);
        if (border instanceof DarkTextBorder) {
            if (c.isEnabled() && c.isEditable()) {
                g.setColor(c.getBackground());
            }
            int width = c.getWidth();
            int height = c.getHeight();
            int w = DarkTextBorder.BORDER_SIZE;
            if (c.hasFocus()) {
                g.fillRoundRect(w, w, width - 2 * w, height - 2 * w,
                                DarkTextFieldUI.ARC_SIZE, DarkTextFieldUI.ARC_SIZE);
            } else {
                g.fillRect(w, w, width - 2 * w, height - 2 * w);
            }
            if (hasShowIcon(c) && showShowIcon()) {
                paintShowIcon(g);
            }
        } else {
            super.paintBackground(g);
        }
        config.restore();
    }

    private void paintShowIcon(final Graphics2D g) {
        var p = getShowIconCoord();
        if (showTriggered) {
            getShowTriggeredIcon().paintIcon(getComponent(), g, p.x, p.y);
        } else {
            getShowIcon().paintIcon(getComponent(), g, p.x, p.y);
        }
    }

    @NotNull
    private Point getShowIconCoord() {
        Rectangle r = DarkTextFieldUI.getDrawingRect(getComponent());
        int w = getShowIcon().getIconWidth();
        return new Point(r.x + r.width - w - DarkTextBorder.PADDING, r.y + (r.height - w) / 2);
    }

    @Contract(pure = true)
    public static Icon getShowIcon() {
        return UIManager.getIcon("PasswordField.show.icon");
    }

    @Contract(pure = true)
    public static Icon getShowTriggeredIcon() {
        return UIManager.getIcon("PasswordField.showPressed.icon");
    }


    @Contract("null -> false")
    public static boolean hasShowIcon(final Component c) {
        return c instanceof JPasswordField
               && Boolean.TRUE.equals(((JComponent) c).getClientProperty("PasswordField.view"));
    }
}
