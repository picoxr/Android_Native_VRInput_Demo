package com.picovr.vr3dlibs.postprocessing.passes;

import android.opengl.GLES20;

import com.picovr.vr3dlibs.cameras.Camera;
import com.picovr.vr3dlibs.materials.Material;
import com.picovr.vr3dlibs.materials.plugins.DepthMaterialPlugin;
import com.picovr.vr3dlibs.postprocessing.APass;
import com.picovr.vr3dlibs.primitives.ScreenQuad;
import com.picovr.vr3dlibs.renderer.RenderTarget;
import com.picovr.vr3dlibs.renderer.Renderer;
import com.picovr.vr3dlibs.scene.Scene;

public class DepthPass extends APass {
	protected Scene               mScene;
	protected Camera mCamera;
	protected Camera              mOldCamera;
	protected DepthMaterialPlugin mDepthPlugin;

	public DepthPass(Scene scene, Camera camera) {
		mPassType = PassType.DEPTH;
		mScene = scene;
		mCamera = camera;

		mEnabled = true;
		mClear = true;
		mNeedsSwap = true;

		Material mat = new Material();
		mDepthPlugin = new DepthMaterialPlugin();
		mat.addPlugin(mDepthPlugin);
		setMaterial(mat);
	}

	@Override
	public void render(Scene scene, Renderer renderer, ScreenQuad screenQuad, RenderTarget writeTarget,
					   RenderTarget readTarget, long ellapsedTime, double deltaTime) {
		GLES20.glClearColor(0, 0, 0, 1);
		mDepthPlugin.setFarPlane((float)mCamera.getFarPlane());
		mOldCamera = mScene.getCamera();
		mScene.switchCamera(mCamera);
		mScene.render(ellapsedTime, deltaTime, writeTarget, mMaterial);
		mScene.switchCamera(mOldCamera);
	}
}
