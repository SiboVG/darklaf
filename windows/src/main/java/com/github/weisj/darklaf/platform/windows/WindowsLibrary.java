/*
 * MIT License
 *
 * Copyright (c) 2019-2022 Jannis Weis
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
package com.github.weisj.darklaf.platform.windows;

import com.github.weisj.darklaf.nativeutil.AbstractLibrary;
import com.github.weisj.darklaf.platform.SystemInfo;
import com.github.weisj.darklaf.util.LogUtil;

public class WindowsLibrary extends AbstractLibrary {

    private static final String PATH = "/com/github/weisj/darklaf/platform/darklaf-windows";
    private static final String x86_PATH = PATH + "/darklaf-windows-x86.dll";
    private static final String x86_64_PATH = PATH + "/darklaf-windows-x86-64.dll";
    private static final WindowsLibrary library = new WindowsLibrary();

    public static WindowsLibrary get() {
        return library;
    }

    protected WindowsLibrary() {
        super("darklaf-windows", LogUtil.getLogger(WindowsLibrary.class));
    }

    @Override
    protected void afterLoad() {
        JNIDecorationsWindows.init();
        SystemInfo.setWindows11State(JNIDecorationsWindows.isWindows11());
    }

    @Override
    final protected Class<?> getLoaderClass() {
        return WindowsLibrary.class;
    }

    protected String getX86Path() {
        return x86_PATH;
    }

    protected String getX64Path() {
        return x86_64_PATH;
    }

    @Override
    public String getLibraryPath() {
        if (SystemInfo.isX86) {
            return getX86Path();
        } else if (SystemInfo.isX64) {
            return getX64Path();
        } else {
            throw new IllegalStateException("Unsupported arch");
        }
    }

    @Override
    protected boolean canLoad() {
        return !SystemInfo.undefinedArchitecture && SystemInfo.isX86Compatible && SystemInfo.isWindows10OrGreater;
    }
}
