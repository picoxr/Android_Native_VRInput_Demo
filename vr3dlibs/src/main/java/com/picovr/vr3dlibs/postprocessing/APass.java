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
package com.picovr.vr3dlibs.postprocessing;


import com.picovr.vr3dlibs.materials.Material;
import com.picovr.vr3dlibs.materials.MaterialManager;
import com.picovr.vr3dlibs.primitives.ScreenQuad;
import com.picovr.vr3dlibs.renderer.RenderTarget;
import com.picovr.vr3dlibs.renderer.Renderer;
import com.picovr.vr3dlibs.scene.Scene;

/**
 * Defines a rendering pass which is needed for multiple rendering passes.
 *
 * @author Andrew Jo
 * @author dennis.ippel
 */
public abstract class APass implements IPass {

	protected boolean mEnabled;
	protected boolean mClear;
	protected boolean mNeedsSwap;
	protected PassType mPassType;
	protected Material mMaterial;
	protected boolean mRenderToScreen;
	protected int mWidth = -1;
	protected int mHeight = -1;

	/**
	 * Returns whether this pass is to be rendered. If false, renderer skips this pass.
	 */
	public boolean isEnabled() {
		return mEnabled;
	}

	/**
	 * Returns whether the framebuffer should be cleared before rendering this pass.
	 */
	public boolean isClear() {
		return mClear;
	}

	/**
	 * Returns whether the write buffer and the read buffer needs to be swapped afterwards.
	 */
	public boolean needsSwap() {
		return mNeedsSwap;
	}

	public abstract void render(Scene scene, Renderer renderer, ScreenQuad screenQuad,
								RenderTarget writeTarget, RenderTarget readTarget, long ellapsedTime, double deltaTime);

	public PassType getPassType() {
		return mPassType;
	}

	public PostProcessingComponentType getType() {
		return PostProcessingComponentType.PASS;
	}

	public void setMaterial(Material material) {
		mMaterial = material;
		MaterialManager.getInstance().addMaterial(material);
	}

	public void setRenderToScreen(boolean renderToScreen) {
		mRenderToScreen = renderToScreen;
	}

	public boolean getRenderToScreen() {
		return mRenderToScreen;
	}

	public void setWidth(int width) {
		mWidth = width;
	}

	public int getWidth() {
		return mWidth;
	}

	public void setHeight(int height) {
		mHeight = height;
	}

	public int getHeight() {
		return mHeight;
	}

	public void setSize(int width, int height) {
		mWidth = width;
		mHeight = height;
	}
}
