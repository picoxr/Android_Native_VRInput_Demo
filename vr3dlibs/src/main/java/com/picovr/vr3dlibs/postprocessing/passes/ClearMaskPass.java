/**
 * Copyright 2013 Dennis Ippel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.picovr.vr3dlibs.postprocessing.passes;

import android.opengl.GLES20;

import com.picovr.vr3dlibs.postprocessing.APass;
import com.picovr.vr3dlibs.primitives.ScreenQuad;
import com.picovr.vr3dlibs.renderer.RenderTarget;
import com.picovr.vr3dlibs.renderer.Renderer;
import com.picovr.vr3dlibs.scene.Scene;

/**
 * Disables stencil test for previously masked rendering passes so that
 * next render passes are not masked.
 * @author andrewjo
 */
public class ClearMaskPass extends APass {
	public ClearMaskPass() {
		mEnabled = true;
	}

	@Override
	public void render(Scene scene, Renderer renderer, ScreenQuad screenQuad, RenderTarget writeBuffer, RenderTarget readBuffer, long ellapsedTime, double deltaTime) {
		// Disable stencil test so next rendering pass won't be masked.
		GLES20.glDisable(GLES20.GL_STENCIL_TEST);
	}
}
