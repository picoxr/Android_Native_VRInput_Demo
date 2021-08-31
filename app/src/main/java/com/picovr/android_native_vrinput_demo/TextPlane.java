package com.picovr.android_native_vrinput_demo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;

import com.picovr.vr3dlibs.materials.Material;
import com.picovr.vr3dlibs.materials.textures.Texture;
import com.picovr.vr3dlibs.primitives.Plane;
import com.picovr.vr3dlibs.scene.Scene;

public class TextPlane {
    private Plane mPlane;
    private String mText;
    private Scene mScene;
    private Bitmap bitmap;
    private Texture texture;
    private Material material;

    public TextPlane(Scene parent) {
        mScene = parent;

        mPlane = new Plane(3f, 1.5f, 2, 1);
        mPlane.setTransparent(true);
        mPlane.setPosition(1.2, 0, -3);
        mPlane.setLookAt(0, 0, 0);
        mPlane.setVisible(true);

        material = new Material();
        material.setColorInfluence(0);
        bitmap = createTextView("idle ", 100, 2000, 1000, Color.BLUE, 30);
        texture = new Texture("textTex", bitmap);
        try {
            material.addTexture(texture);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPlane.setMaterial(material);
        mScene.addChild(mPlane);
    }

    private Bitmap createTextView(String title, float textsize, int width, int height,
                                  int color, int textAlign) {
        TextPaint paint = new TextPaint();
        paint.setTextSize(textsize);
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setDither(false);
        paint.setFakeBoldText(true);
        paint.setLinearText(false);
        paint.setShadowLayer(0.05f, 0, 0, color);

        Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(width, height, bitmapConfig);

        float textLength = paint.measureText(title);
        Paint.FontMetrics fm = paint.getFontMetrics();
        float textHeight = (float) (Math.ceil(fm.descent - fm.ascent));
        Canvas bitmapCanvas = new Canvas(bitmap);
        float x = 0;
        if (textAlign == 0) {
            x = 0;
        } else if (textAlign == 1) {
            x = (width - textLength) / 2;
        } else if (textAlign == 2) {
            x = width - textLength;
        }
        float y = (height - textHeight) / 2;
        bitmapCanvas.drawText(title, x, y, paint);
        return bitmap;
    }

    public void update(String content) {
//        mPlane.setPosition(mScene.getCamera().getLookAt().x, mScene.getCamera().getLookAt().y, mScene.getCamera().getLookAt().z - 3);
        mText = content;

        bitmap.recycle();

        bitmap = createTextView(mText, 100, 2000, 1000,
                Color.BLUE, 30);

        try {
            texture.setBitmap(bitmap);
            texture.replace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPosition(double x, double y, double z) {
        mPlane.setPosition(x, y, z);
    }

}
