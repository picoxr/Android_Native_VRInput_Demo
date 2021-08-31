package com.picovr.vr3dlibs.debug;

import com.picovr.vr3dlibs.Object3D;
import com.picovr.vr3dlibs.renderer.Renderer;

/**
 * @author dennis.ippel
 */
public class DebugVisualizer extends Object3D {
    private Renderer mRenderer;

    public DebugVisualizer(Renderer renderer) {
        mRenderer = renderer;
    }

    public void addChild(DebugObject3D child) {
        super.addChild(child);
        child.setRenderer(mRenderer);
    }
}
