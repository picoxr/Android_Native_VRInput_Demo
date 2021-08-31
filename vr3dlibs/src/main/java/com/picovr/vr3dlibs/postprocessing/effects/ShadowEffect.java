package com.picovr.vr3dlibs.postprocessing.effects;

import android.graphics.Bitmap.Config;
import android.opengl.GLES20;

import com.picovr.vr3dlibs.cameras.Camera;
import com.picovr.vr3dlibs.lights.DirectionalLight;
import com.picovr.vr3dlibs.materials.textures.ATexture;
import com.picovr.vr3dlibs.postprocessing.APostProcessingEffect;
import com.picovr.vr3dlibs.postprocessing.materials.ShadowMapMaterial;
import com.picovr.vr3dlibs.postprocessing.passes.ShadowPass;
import com.picovr.vr3dlibs.renderer.RenderTarget;
import com.picovr.vr3dlibs.renderer.Renderer;
import com.picovr.vr3dlibs.scene.Scene;


public class ShadowEffect extends APostProcessingEffect {
	private Scene mScene;
	private Camera mCamera;
	private DirectionalLight mLight;
	private int               mShadowMapSize;
	private RenderTarget mShadowRenderTarget;
	private float             mShadowInfluence;
	private ShadowMapMaterial mShadowMapMaterial;

	public ShadowEffect(Scene scene, Camera camera, DirectionalLight light, int shadowMapSize) {
		super();
		mScene = scene;
		mCamera = camera;
		mLight = light;
		mShadowMapSize = shadowMapSize;
	}

	public void setShadowInfluence(float influence) {
		mShadowInfluence = influence;
		if(mShadowMapMaterial != null)
			mShadowMapMaterial.setShadowInfluence(influence);
	}

	@Override
	public void initialize(Renderer renderer) {
		mShadowRenderTarget = new RenderTarget("shadowRT" + hashCode(), mShadowMapSize, mShadowMapSize, 0, 0,
				false, false, GLES20.GL_TEXTURE_2D, Config.ARGB_8888,
				ATexture.FilterType.LINEAR, ATexture.WrapType.CLAMP);
		renderer.addRenderTarget(mShadowRenderTarget);

		ShadowPass pass1 = new ShadowPass(ShadowPass.ShadowPassType.CREATE_SHADOW_MAP, mScene, mCamera, mLight, mShadowRenderTarget);
		addPass(pass1);
		ShadowPass pass2 = new ShadowPass(ShadowPass.ShadowPassType.APPLY_SHADOW_MAP, mScene, mCamera, mLight, mShadowRenderTarget);
		mShadowMapMaterial = pass1.getShadowMapMaterial();
		mShadowMapMaterial.setShadowInfluence(mShadowInfluence);
		pass2.setShadowMapMaterial(pass1.getShadowMapMaterial());
		addPass(pass2);
	}
}
