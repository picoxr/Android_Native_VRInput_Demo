/**
 * Copyright 2013 Dennis Ippel
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
package com.picovr.vr3dlibs.materials.shaders.fragments.texture;

import com.picovr.vr3dlibs.materials.Material;
import com.picovr.vr3dlibs.materials.textures.ATexture;

import java.util.List;

public class DiffuseTextureFragmentShaderFragment extends ATextureFragmentShaderFragment {
    public final static String SHADER_ID = "DIFFUSE_TEXTURE_FRAGMENT";

    public DiffuseTextureFragmentShaderFragment(List<ATexture> textures) {
        super(textures);
    }

    public String getShaderId() {
        return SHADER_ID;
    }

    @Override
    public void main() {
        super.main();
        RVec4 color = (RVec4) getGlobal(DefaultShaderVar.G_COLOR);
        RVec2 textureCoord = (RVec2) getGlobal(DefaultShaderVar.G_TEXTURE_COORD);
        RVec4 texColor = new RVec4("texColor");

        for (int i = 0; i < mTextures.size(); i++) {
            ATexture texture = mTextures.get(i);
            if (texture.offsetEnabled())
                textureCoord.assignAdd(getGlobal(DefaultShaderVar.U_OFFSET, i));
            if (texture.getWrapType() == ATexture.WrapType.REPEAT)
                textureCoord.assignMultiply(getGlobal(DefaultShaderVar.U_REPEAT, i));

            if (texture.getTextureType() == ATexture.TextureType.VIDEO_TEXTURE)
                texColor.assign(texture2D(muVideoTextures[i], textureCoord));
            else
                texColor.assign(texture2D(muTextures[i], textureCoord));
            texColor.assignMultiply(muInfluence[i]);
            color.assignAdd(texColor);
        }
    }

    public void bindTextures(int nextIndex) {
    }

    public void unbindTextures() {
    }

    @Override
    public Material.PluginInsertLocation getInsertLocation() {
        return Material.PluginInsertLocation.IGNORE;
    }
}
