/*
 * MIT License
 *
 * Copyright (c) 2019-2023 Jannis Weis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.weisj.darklaf.ui.list;

import java.awt.*;

import javax.swing.*;

import com.github.weisj.darklaf.graphics.PaintUtil;
import com.github.weisj.darklaf.ui.cell.DarkCellBorderUIResource;
import com.github.weisj.darklaf.ui.util.DarkUIUtil;

public class DarkListCellFocusBorder extends DarkCellBorderUIResource {

    protected final Color borderColor;

    public DarkListCellFocusBorder() {
        borderColor = UIManager.getColor("List.focusBorderColor");
    }

    @Override
    public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width,
            final int height) {
        JList<?> list = DarkUIUtil.getParentOfType(JList.class, c, DarkUIUtil.CELL_SEARCH_DEPTH);
        if (list == null) return;
        ListSelectionModel selectionModel = list.getSelectionModel();
        if (selectionModel.getMinSelectionIndex() != selectionModel.getMaxSelectionIndex()) {
            // Only paint if it is necessary to distinguish the lead selection cell
            g.setColor(borderColor);
            PaintUtil.drawCellBackgroundBorder((Graphics2D) g, c);
        }
    }
}
