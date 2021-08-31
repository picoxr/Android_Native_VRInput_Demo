package com.picovr.android_native_vrinput_demo;

import static com.picovr.cvclient.ButtonNum.app;
import static com.picovr.cvclient.ButtonNum.buttonAX;
import static com.picovr.cvclient.ButtonNum.buttonBY;
import static com.picovr.cvclient.ButtonNum.buttonLG;
import static com.picovr.cvclient.ButtonNum.buttonRG;
import static com.picovr.cvclient.ButtonNum.click;
import static com.picovr.cvclient.ButtonNum.home;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;

import com.picovr.cvclient.CVController;
import com.picovr.vr3dlibs.Object3D;
import com.picovr.vr3dlibs.lights.DirectionalLight;
import com.picovr.vr3dlibs.loader.LoaderOBJ;
import com.picovr.vr3dlibs.materials.Material;
import com.picovr.vr3dlibs.materials.textures.Texture;
import com.picovr.vr3dlibs.math.Quaternion;
import com.picovr.vr3dlibs.math.vector.Vector3;
import com.picovr.vr3dlibs.terrain.SquareTerrain;
import com.picovr.vractivity.HmdState;
import com.picovr.vrlibs.renderer.VRRenderer;
import com.psmart.vrlib.PicovrSDK;

public class MainRender extends VRRenderer {
    private SquareTerrain terrain;
    private TextPlane plane;

    private Object3D model_cvRight, model_cvLeft;
    private Vector3 pose_cvRight, pose_cvLeft;
    private Quaternion quat_cvRight, quat_cvLeft;

    private CVController ctrCVRight, ctrCVLeft;

    public MainRender(Context context) {
        super(context);
    }

    @Override
    public void initScene() {
        pose_cvRight = new Vector3(0, 0, 0);
        pose_cvLeft = new Vector3(0, 0, 0);
        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setPosition(1.0, 0.0, 0.0);
        directionalLight.setPower(1.5f);
        directionalLight.setLookAt(Vector3.ZERO);
        directionalLight.enableLookAt();

        getCurrentScene().addLight(directionalLight);
        getCurrentScene().setBackgroundColor(0x000000);
        getCurrentCamera().setFarPlane(2000);

        createController();
        plane = new TextPlane(this.getCurrentScene());
    }

    private void createController() {
        try {
            LoaderOBJ model_r = new LoaderOBJ(getContext().getResources(), getTextureManager(), R.raw.controller_neo3_r);
            model_r.parse();

            Material cMaterial = new Material();
            cMaterial.setColorInfluence(0f);
            cMaterial.enableLighting(true);
            cMaterial.addTexture(new Texture("controllerTex", R.drawable.controller_neo3));

            model_cvRight = model_r.getParsedObject();
            model_cvRight.setScale(0.007f);
            model_cvRight.setZ(-1);
            model_cvRight.setY(-1);
            model_cvRight.setMaterial(cMaterial);

            LoaderOBJ model_l = new LoaderOBJ(getContext().getResources(), getTextureManager(), R.raw.controller_neo3_l);
            model_l.parse();

            model_cvLeft = model_l.getParsedObject();
            model_cvLeft.setScale(0.007f);
            model_cvLeft.setZ(-1);
            model_cvLeft.setY(-1);
            model_cvLeft.setMaterial(cMaterial);

        } catch (Exception e) {
            e.printStackTrace();

        }
        getCurrentScene().addChild(model_cvRight);
        getCurrentScene().addChild(model_cvLeft);
    }

    @Override
    public void onRender(long elapsedTime, double deltaTime) {
        super.onRender(elapsedTime, deltaTime);

        StringBuilder textRight = new StringBuilder();
        StringBuilder textLeft = new StringBuilder();
        StringBuilder textShow = new StringBuilder();

        if (0 != ctrCVRight.getConnectState()) {
            if (ctrCVRight.getButtonState(app)) {
                textRight.append("app ");
            }
            if (ctrCVRight.getButtonState(click)) {
                textRight.append("click ");
            }
            if (ctrCVRight.getButtonState(home)) {
                textRight.append("home ");
            }
            if (ctrCVRight.getButtonState(buttonAX)) {
                textRight.append("A ");
            }
            if (ctrCVRight.getButtonState(buttonBY)) {
                textRight.append("B ");
            }
            if (ctrCVRight.getButtonState(buttonLG) || ctrCVRight.getButtonState(buttonRG)) {
                textRight.append("grip ");
            }
            if (ctrCVRight.getTriggerNum() > 1) {
                textRight.append("trigger ").append(ctrCVRight.getTriggerNum());
            }
            if ((ctrCVRight.getTouchPad()[0] != 128 || ctrCVRight.getTouchPad()[1] != 128)) {
                textRight.append("touch(").append(ctrCVRight.getTouchPad()[0]).append(",").append(ctrCVRight.getTouchPad()[1]).append(") ");
            }
            if (!TextUtils.isEmpty(textRight.toString())) {
                textShow.append("right -- ").append(textRight);
            }
        }

        if (0 != ctrCVLeft.getConnectState()) {
            if (ctrCVLeft.getButtonState(app)) {
                textLeft.append("app ");
            }
            if (ctrCVLeft.getButtonState(click)) {
                textLeft.append("click ");
            }
            if (ctrCVLeft.getButtonState(home)) {
                textLeft.append("home ");
            }
            if (ctrCVLeft.getButtonState(buttonAX)) {
                textLeft.append("X ");
            }
            if (ctrCVLeft.getButtonState(buttonBY)) {
                textLeft.append("Y ");
            }
            if (ctrCVLeft.getButtonState(buttonLG) || ctrCVLeft.getButtonState(buttonRG)) {
                textLeft.append("grip ");
            }
            if (ctrCVLeft.getTriggerNum() > 1) {
                textLeft.append("trigger ").append(ctrCVLeft.getTriggerNum());
            }
            if ((ctrCVLeft.getTouchPad()[0] != 128 || ctrCVLeft.getTouchPad()[1] != 128)) {
                textLeft.append("touch(").append(ctrCVLeft.getTouchPad()[0]).append(",").append(ctrCVLeft.getTouchPad()[1]).append(") ");
            }
            if (!TextUtils.isEmpty(textLeft.toString())) {
                textShow.append("left -- ").append(textLeft);
            }
        }
//        plane.setPosition(getCurrentScene().getCamera().getX(), getCurrentScene().getCamera().getY(),
//                getCurrentScene().getCamera().getZ()-3);
        plane.update(textShow.toString());
    }

    @Override
    public void onNewFrame(HmdState hmdState) {
        super.onNewFrame(hmdState);
        refreshModelCVRight();
        refreshModelCVLeft();
        PicovrSDK.resetSensor(0);
    }

    private void refreshModelCVRight() {
        if (ctrCVRight.getConnectState() != 1) {
            model_cvRight.setVisible(false);
            return;
        } else {
            model_cvRight.setVisible(true);
        }
        float[] ori1 = ctrCVRight.getOrientation();
        float[] pos1 = ctrCVRight.getPosition();
        pose_cvRight.x = pos1[0];
        pose_cvRight.y = pos1[1];
        pose_cvRight.z = pos1[2];

        quat_cvRight = new Quaternion(-ori1[3], ori1[0], ori1[1], -ori1[2]).inverse();
        model_cvRight.setPosition(pose_cvRight);
        model_cvRight.setOrientation(quat_cvRight);
    }

    private void refreshModelCVLeft() {
        if (ctrCVLeft.getConnectState() != 1) {
            model_cvLeft.setVisible(false);
            return;
        } else {
            model_cvLeft.setVisible(true);
        }
        float[] ori2 = ctrCVLeft.getOrientation();
        float[] pos2 = ctrCVLeft.getPosition();
        pose_cvLeft.x = pos2[0];
        pose_cvLeft.y = pos2[1];
        pose_cvLeft.z = pos2[2];
        quat_cvLeft = new Quaternion(-ori2[3], ori2[0], ori2[1], -ori2[2]).inverse();

        model_cvLeft.setPosition(pose_cvLeft);
        model_cvLeft.setOrientation(quat_cvLeft);
    }

    public void setcvController(CVController right, CVController left) {
        ctrCVRight = right;
        ctrCVLeft = left;
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }
}
