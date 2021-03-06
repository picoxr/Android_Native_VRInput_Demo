/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.picovr.vr3dlibs.renderer.pip;

import android.content.Context;
import android.view.MotionEvent;

import com.picovr.vr3dlibs.cameras.Camera;
import com.picovr.vr3dlibs.renderer.Renderer;
import com.picovr.vr3dlibs.scene.Scene;

/**
 * This class is intended to be used with <code>PipRenderer</code>.
 */
public abstract class SubRenderer {
    private static final String TAG = "SubRenderer";
    private Scene scene;
    private Renderer renderer;

    public SubRenderer(Renderer renderer) {
        scene = new Scene(renderer);
        renderer.addScene(scene);
        this.renderer = renderer;
    }

    public Scene getCurrentScene() {
        return scene;
    }

    public Camera getCurrentCamera() {
        return scene.getCamera();
    }

    protected Context getContext() {
        return renderer.getContext();
    }

    public void doRender() {
        onRender();
    }

    protected abstract void onRender();

    public abstract void initScene();

    public void onTouchEvent(MotionEvent event) {
    }
}
