/**
 * Copyright 2015 Dennis Ippel
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.picovr.vrlibs.materials;

import com.picovr.vr3dlibs.materials.Material;
import com.picovr.vr3dlibs.materials.shaders.VertexShader;
import com.picovr.vr3dlibs.math.vector.Vector2;
import com.picovr.vrlibs.R;
import com.picovr.vrlibs.materials.shaders.HotspotFragmentShader;

/**
 * @author dennis.ippel
 */
public class HotspotMaterial extends Material {
    private HotspotFragmentShader mHotspotShader;

    public HotspotMaterial(boolean useTexture) {
        this(useTexture, false);
    }

    public HotspotMaterial(boolean useTexture, boolean discardAlpha) {
        super(new VertexShader(R.raw.minimal_vertex_shader),
                new HotspotFragmentShader(useTexture, discardAlpha));
        mHotspotShader = (HotspotFragmentShader) mCustomFragmentShader;
    }

    public void setCircleCenter(Vector2 center) {
        mHotspotShader.setCircleCenter(center);
    }

    public void setTrackColor(int color) {
        mHotspotShader.setTrackColor(color);
    }

    public void setProgressColor(int color) {
        mHotspotShader.setProgressColor(color);
    }

    public void setCircleRadius(float circleRadius) {
        mHotspotShader.setCircleRadius(circleRadius);
    }

    public void setBorderThickness(float borderThickness) {
        mHotspotShader.setBorderThickness(borderThickness);
    }

    public void setTextureRotationSpeed(float textureRotationSpeed) {
        mHotspotShader.setTextureRotationSpeed(textureRotationSpeed);
    }

    public float getProgress() {
        return mHotspotShader.getProgress();
    }

    public void setProgress(float progress) {
        mHotspotShader.setProgress(progress);
    }
}
