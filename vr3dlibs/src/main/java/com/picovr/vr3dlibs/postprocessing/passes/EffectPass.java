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

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import com.picovr.vr3dlibs.materials.Material;
import com.picovr.vr3dlibs.materials.shaders.FragmentShader;
import com.picovr.vr3dlibs.materials.shaders.VertexShader;
import com.picovr.vr3dlibs.postprocessing.APass;
import com.picovr.vr3dlibs.primitives.ScreenQuad;
import com.picovr.vr3dlibs.renderer.RenderTarget;
import com.picovr.vr3dlibs.renderer.Renderer;
import com.picovr.vr3dlibs.scene.Scene;

public class EffectPass extends APass {
    protected final String PARAM_OPACITY       = "uOpacity";
    protected final String PARAM_TEXTURE       = "uTexture";
    protected final String PARAM_DEPTH_TEXTURE = "uDepthTexture";
    protected final String PARAM_BLEND_TEXTURE = "uBlendTexture";

    protected VertexShader mVertexShader;
    protected FragmentShader mFragmentShader;
    protected RenderTarget mReadTarget;
    protected RenderTarget   mWriteTarget;

    @FloatRange(from = 0d, to = 1d) protected float mOpacity = 1.0f;

    public EffectPass() {
        mPassType = PassType.EFFECT;
        mNeedsSwap = true;
        mClear = false;
        mEnabled = true;
        mRenderToScreen = false;
    }

    public EffectPass(@NonNull Material material) {
        this();
        setMaterial(material);
    }

    protected void createMaterial(@NonNull VertexShader vertexShader, @NonNull FragmentShader fragmentShader) {
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
        mVertexShader.setNeedsBuild(false);
        mFragmentShader.setNeedsBuild(false);
        setMaterial(new Material(mVertexShader, mFragmentShader));
    }

    protected void createMaterial(@RawRes int vertexShaderResourceId, @RawRes int fragmentShaderResourceId) {
        createMaterial(new VertexShader(vertexShaderResourceId), new FragmentShader(fragmentShaderResourceId));
    }


    public void setShaderParams() {
        mFragmentShader.setUniform1f(PARAM_OPACITY, mOpacity);
        mMaterial.bindTextureByName(PARAM_TEXTURE, 0, mReadTarget.getTexture());
    }

    public void render(@NonNull Scene scene, @NonNull Renderer renderer, @NonNull ScreenQuad screenQuad,
                       @NonNull RenderTarget writeTarget, @NonNull RenderTarget readTarget,
                       @IntRange(from = 0) long ellapsedTime, @FloatRange(from = 0d) double deltaTime) {
        mReadTarget = readTarget;
        mWriteTarget = writeTarget;
        screenQuad.setMaterial(mMaterial);
        screenQuad.setEffectPass(this);

        if (mRenderToScreen) {
            scene.render(ellapsedTime, deltaTime, null);
        } else {
            scene.render(ellapsedTime, deltaTime, writeTarget);
        }
    }

    public void setOpacity(@FloatRange(from = 0d, to = 1d) float value) {
        mOpacity = value;
    }
}
