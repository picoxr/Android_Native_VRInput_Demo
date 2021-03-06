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
package com.picovr.vr3dlibs.materials.plugins;

import com.picovr.vr3dlibs.materials.Material;
import com.picovr.vr3dlibs.materials.shaders.AShaderBase;
import com.picovr.vr3dlibs.materials.shaders.IShaderFragment;
import com.picovr.vr3dlibs.materials.shaders.fragments.animation.SkeletalAnimationVertexShaderFragment;

/**
 * <p>
 * The material plugin for skeletal animation. This is the container for
 * skeletal animation shaders and should be used as the access point to 
 * skeletal animation properties. 
 * </p>
 * <p>
 * Skeletal animation on mobile devices is limited. It is recommended to 
 * use as few bones as possible. The number of vertex weights per bone
 * can't be more than 8.  
 * </p>
 * 
 * Example usage:
 * 
 * <pre><code>
 * ...
 * mMaterialPlugin = new SkeletalAnimationMaterialPlugin(numJoints, numVertexWeights);
 * ...
 * 
 * public void setShaderParams(Camera camera) {
 * 		super.setShaderParams(camera);
 * 		mMaterialPlugin.setBone1Indices(mboneIndexes1BufferInfo.bufferHandle);
 * 		mMaterialPlugin.setBone1Weights(mboneWeights1BufferInfo.bufferHandle);
 * 		if (mMaxBoneWeightsPerVertex > 4) {
 * 			mMaterialPlugin.setBone2Indices(mboneIndexes2BufferInfo.bufferHandle);
 * 			mMaterialPlugin.setBone2Weights(mboneWeights2BufferInfo.bufferHandle);
 * 		}
 * 		mMaterialPlugin.setBoneMatrix(mSkeleton.uBoneMatrix);
 * 	}
 * </code></pre>
 * 
 * @author dennis.ippel
 *
 */
public class SkeletalAnimationMaterialPlugin implements IMaterialPlugin {
	public static enum SkeletalAnimationShaderVar implements AShaderBase.IGlobalShaderVar {
		U_BONE_MATRIX("uBoneMatrix", AShaderBase.DataType.MAT4),
		A_BONE_INDEX1("aBoneIndex1", AShaderBase.DataType.VEC4),
		A_BONE_INDEX2("aBoneIndex2", AShaderBase.DataType.VEC4),
		A_BONE_WEIGHT1("aBoneWeight1", AShaderBase.DataType.VEC4),
		A_BONE_WEIGHT2("aBoneWeight2", AShaderBase.DataType.VEC4),
		G_BONE_TRANSF_MATRIX("gBoneTransfMatrix", AShaderBase.DataType.MAT4);
		
		private String mVarString;
		private AShaderBase.DataType mDataType;

		SkeletalAnimationShaderVar(String varString, AShaderBase.DataType dataType) {
			mVarString = varString;
			mDataType = dataType;
		}

		public String getVarString() {
			return mVarString;
		}

		public AShaderBase.DataType getDataType() {
			return mDataType;
		}
	}	

	private SkeletalAnimationVertexShaderFragment mVertexShader;
	
	public SkeletalAnimationMaterialPlugin(int numJoints, int numVertexWeights)
	{
		mVertexShader = new SkeletalAnimationVertexShaderFragment(numJoints, numVertexWeights);
	}

	@Override
	public IShaderFragment getVertexShaderFragment() {
		return mVertexShader;
	}

	@Override
	public IShaderFragment getFragmentShaderFragment() {
		return null;
	}

	@Override
	public Material.PluginInsertLocation getInsertLocation() {
		return Material.PluginInsertLocation.PRE_LIGHTING;
	}
	
	public void setBone1Indices(final int boneIndex1BufferHandle) {
		mVertexShader.setBone1Indices(boneIndex1BufferHandle);
	}

	public void setBone2Indices(final int boneIndex2BufferHandle) {
		mVertexShader.setBone2Indices(boneIndex2BufferHandle);
	}

	public void setBone1Weights(final int boneWeights1BufferHandle) {
		mVertexShader.setBone1Weights(boneWeights1BufferHandle);
	}

	public void setBone2Weights(final int boneWeights2BufferHandle) {
		mVertexShader.setBone2Weights(boneWeights2BufferHandle);
	}

	public void setBoneMatrix(double[] boneMatrix) {
		mVertexShader.setBoneMatrix(boneMatrix);
	}
	
	@Override
	public void bindTextures(int nextIndex) {}
	@Override
	public void unbindTextures() {}
}
