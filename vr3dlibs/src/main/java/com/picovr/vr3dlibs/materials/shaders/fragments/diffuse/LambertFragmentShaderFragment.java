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
package com.picovr.vr3dlibs.materials.shaders.fragments.diffuse;

import com.picovr.vr3dlibs.lights.ALight;
import com.picovr.vr3dlibs.materials.Material;
import com.picovr.vr3dlibs.materials.methods.DiffuseMethod;
import com.picovr.vr3dlibs.materials.shaders.AShader;
import com.picovr.vr3dlibs.materials.shaders.IShaderFragment;
import com.picovr.vr3dlibs.materials.shaders.fragments.LightsVertexShaderFragment;

import java.util.List;

public class LambertFragmentShaderFragment extends AShader implements IShaderFragment {
	public final static String SHADER_ID = "LAMBERT_FRAGMENT";
	
	private List<ALight> mLights;
	private RFloat[] mgNdotL;
	
	public LambertFragmentShaderFragment(List<ALight> lights) {
		super(ShaderType.FRAGMENT_SHADER_FRAGMENT);
		mLights = lights;
		initialize();
	}
	
	@Override
	public void initialize()
	{
		super.initialize();
		
		mgNdotL = new RFloat[mLights.size()];
		
		for (int i = 0; i < mLights.size(); i++)
		{
			mgNdotL[i] = (RFloat) addGlobal(DiffuseMethod.DiffuseShaderVar.L_NDOTL, i);
		}
	}
	
	public String getShaderId() {
		return SHADER_ID;
	}
	
	@Override
	public void main() {
		RVec3 diffuse = new RVec3("diffuse");
		diffuse.assign(0);
		RVec3 normal = (RVec3)getGlobal(DefaultShaderVar.G_NORMAL);
		RFloat power = new RFloat("power");
		power.assign(0.0f);
		
		for (int i = 0; i < mLights.size(); i++)
		{
			RFloat attenuation = (RFloat)getGlobal(LightsVertexShaderFragment.LightsShaderVar.V_LIGHT_ATTENUATION, i);
			RFloat lightPower = (RFloat)getGlobal(LightsVertexShaderFragment.LightsShaderVar.U_LIGHT_POWER, i);
			RVec3 lightColor = (RVec3)getGlobal(LightsVertexShaderFragment.LightsShaderVar.U_LIGHT_COLOR, i);
			
			RVec3 lightDir = new RVec3("lightDir" + i);
			//
			// -- NdotL = max(dot(vNormal, lightDir), 0.1);
			//
			RFloat nDotL = mgNdotL[i];
			nDotL.assign(max(dot(normal, lightDir), 0.1f));
			//
			// -- power = uLightPower * NdotL * vAttenuation;
			//
			power.assign(lightPower.multiply(nDotL).multiply(attenuation));
			//
			// -- diffuse.rgb += uLightColor * power;
			//
			diffuse.assignAdd(lightColor.multiply(power));
		}
		RVec4 color = (RVec4) getGlobal(DefaultShaderVar.G_COLOR);
		RVec3 ambientColor = (RVec3) getGlobal(LightsVertexShaderFragment.LightsShaderVar.V_AMBIENT_COLOR);
		color.rgb().assign(enclose(diffuse.multiply(color.rgb())).add(ambientColor));
		ShaderVar gShadowValue = getGlobal(DefaultShaderVar.G_SHADOW_VALUE);
		color.rgb().assign(
				color.rgb().multiply(enclose(new RFloat("1.0").subtract(gShadowValue))));
	}
	
	@Override
	public void bindTextures(int nextIndex) {}
	@Override
	public void unbindTextures() {}
	
	@Override
	public Material.PluginInsertLocation getInsertLocation() {
		return Material.PluginInsertLocation.IGNORE;
	}
}
