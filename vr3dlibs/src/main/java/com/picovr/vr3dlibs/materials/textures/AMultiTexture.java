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
package com.picovr.vr3dlibs.materials.textures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;


public abstract class AMultiTexture extends ATexture {
    protected Bitmap[] mBitmaps;
    protected ByteBuffer[] mByteBuffers;
    protected ACompressedTexture[] mCompressedTextures;
    protected int[] mResourceIds;

    protected AMultiTexture() {
        super();
    }

    public AMultiTexture(TextureType textureType, String textureName) {
        super(textureType, textureName);
    }

    public AMultiTexture(TextureType textureType, String textureName, int[] resourceIds) {
        super(textureType, textureName);
        setResourceIds(resourceIds);
    }

    public AMultiTexture(TextureType textureType, String textureName, Bitmap[] bitmaps) {
        super(textureType, textureName);
        setBitmaps(bitmaps);
    }

    public AMultiTexture(TextureType textureType, String textureName, ByteBuffer[] byteBuffers) {
        super(textureType, textureName);
        setByteBuffers(byteBuffers);
    }

    public AMultiTexture(TextureType textureType, String textureName, ACompressedTexture[] compressedTextures) {
        super(textureType, textureName);
        setCompressedTextures(compressedTextures);
    }

    public AMultiTexture(AMultiTexture other) {
        super(other);
        setFrom(other);
    }

    /**
     * Copies every property from another AMultiTexture object
     *
     * @param other another AMultiTexture object to copy from
     */
    public void setFrom(AMultiTexture other) {
        super.setFrom(other);
        setBitmaps(mBitmaps);
        setResourceIds(mResourceIds);
        setByteBuffers(mByteBuffers);
    }

    public int[] getResourceIds() {
        return mResourceIds;
    }

    public void setResourceIds(int[] resourceIds) {
        mResourceIds = resourceIds;
        int numResources = resourceIds.length;
        mBitmaps = new Bitmap[numResources];
        Context context = TextureManager.getInstance().getContext();

        for (int i = 0; i < numResources; i++) {
            mBitmaps[i] = BitmapFactory.decodeResource(context.getResources(), resourceIds[i]);
        }
    }

    public Bitmap[] getBitmaps() {
        return mBitmaps;
    }

    public void setBitmaps(Bitmap[] bitmaps) {
        mBitmaps = bitmaps;
    }

    public ByteBuffer[] getByteBuffers() {
        return mByteBuffers;
    }

    public void setByteBuffers(ByteBuffer[] byteBuffers) {
        mByteBuffers = byteBuffers;
    }

    public ACompressedTexture[] getCompressedTextures() {
        return mCompressedTextures;
    }

    public void setCompressedTextures(ACompressedTexture[] compressedTextures) {
        mCompressedTextures = compressedTextures;
    }

    @Override
    public void remove() throws TextureException {
        if (mCompressedTextures != null) {
            int count = mCompressedTextures.length;
            for (int i = 0; i < count; i++) {
                ACompressedTexture texture = mCompressedTextures[i];
                texture.remove();
                mCompressedTextures[i] = null;
            }
        } else {
            GLES20.glDeleteTextures(1, new int[]{mTextureId}, 0);
        }
    }

    @Override
    public void replace() throws TextureException {
        if (mCompressedTextures != null) {
            for (ACompressedTexture compressedTexture : mCompressedTextures) {
                compressedTexture.replace();
                setWidth(compressedTexture.getWidth());
                setHeight(compressedTexture.getHeight());
                setTextureId(compressedTexture.getTextureId());
            }
            return;
        }

        if (mBitmaps == null && (mByteBuffers == null || mByteBuffers.length == 0))
            throw new TextureException("Texture could not be replaced because there is no Bitmap or ByteBuffer set.");

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);

        if (mBitmaps != null) {
            for (Bitmap mBitmap : mBitmaps) {
//                int bitmapFormat = mBitmap.getConfig() == Bitmap.Config.ARGB_8888 ? GLES20.GL_RGBA : GLES20.GL_RGB;
//                if (mBitmap.getWidth() != mWidth || mBitmap.getHeight() != mHeight)
//                    throw new TextureException("Texture could not be updated because the texture size is different from the original.");
//                if (bitmapFormat != mBitmapFormat)
//                    throw new TextureException("Texture could not be updated because the bitmap format is different from the original");

                GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, mBitmap, mBitmapFormat, GLES20.GL_UNSIGNED_BYTE);
            }

        } else if (mByteBuffers != null) {
            for (ByteBuffer mByteBuffer : mByteBuffers) {
//                if (mWidth == 0 || mHeight == 0 || mBitmapFormat == 0)
//                    throw new TextureException(
//                            "Could not update ByteBuffer texture. One or more of the following properties haven't been set: width, height or bitmap format");
                GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, mWidth, mHeight, mBitmapFormat, GLES20.GL_UNSIGNED_BYTE, mByteBuffer);
            }
        }

        if (mMipmap)
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    public void reset() throws TextureException {
        if (mBitmaps != null) {
            int count = mBitmaps.length;
            for (int i = 0; i < count; i++) {
                Bitmap bitmap = mBitmaps[i];
                bitmap.recycle();
                mBitmaps[i] = null;
            }
        }
        if (mByteBuffers != null) {
            int count = mByteBuffers.length;
            for (int i = 0; i < count; i++) {
                ByteBuffer byteBuffer = mByteBuffers[i];
                byteBuffer.clear();
                mByteBuffers[i] = null;
            }
        }
        if (mCompressedTextures != null) {
            int count = mCompressedTextures.length;
            for (int i = 0; i < count; i++) {
                ACompressedTexture texture = mCompressedTextures[i];
                texture.remove();
                mCompressedTextures[i] = null;
            }
        }
    }
}
