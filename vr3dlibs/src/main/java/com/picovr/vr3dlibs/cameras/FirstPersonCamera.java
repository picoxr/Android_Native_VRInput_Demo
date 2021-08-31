package com.picovr.vr3dlibs.cameras;

import com.picovr.vr3dlibs.Object3D;
import com.picovr.vr3dlibs.math.Matrix4;
import com.picovr.vr3dlibs.math.vector.Vector3;

/**
 * @author Jared Woolston (jwoolston@tenkiv.com)
 */
public class FirstPersonCamera extends AObjectCamera {

    public FirstPersonCamera() {
        super();
    }

    public FirstPersonCamera(Vector3 cameraOffset) {
        this(cameraOffset, null);
    }

    public FirstPersonCamera(Vector3 cameraOffset, Object3D object) {
        super(cameraOffset, object);
    }

    @Override
    public Matrix4 getViewMatrix() {
        mPosition.addAndSet(mLinkedObject.getWorldPosition(), mCameraOffset);
        mLinkedObject.getOrientation(mOrientation);
        onRecalculateModelMatrix(null);
        return super.getViewMatrix();
    }
}
