package com.picovr.vr3dlibs.animation;

import com.picovr.vr3dlibs.math.Quaternion;
import com.picovr.vr3dlibs.math.vector.Vector3;

public class RotateOnAxisAnimation extends Animation3D {

	protected final Quaternion mQuat;
	protected final Quaternion mQuatFrom;
	protected final Vector3 mRotationAxis;

	protected double mDegreesToRotate;
	protected double mRotateFrom;
	protected double mRotationAngle;

	protected RotateOnAxisAnimation(Vector3 axis) {
		super();

		mRotationAxis = axis;
		mQuat = new Quaternion();
		mQuatFrom = new Quaternion();
	}

	public RotateOnAxisAnimation(Vector3.Axis axis, double degreesToRotate) {
		this(axis, 0, degreesToRotate);
	}

	public RotateOnAxisAnimation(Vector3.Axis axis, double rotateFrom, double degreesToRotate) {
		this(Vector3.getAxisVector(axis), rotateFrom, degreesToRotate);
	}

	public RotateOnAxisAnimation(Vector3 axis, double degreesToRotate) {
		this(axis, 0, degreesToRotate);
	}

	public RotateOnAxisAnimation(Vector3 axis, double rotateFrom, double degreesToRotate) {
		this(axis);

		mQuatFrom.fromAngleAxis(axis, rotateFrom);
		mRotateFrom = rotateFrom;
		mDegreesToRotate = degreesToRotate;
	}

	@Override
	public void eventStart() {
		if (isFirstStart())
			mTransformable3D.getOrientation(mQuatFrom);
		
		super.eventStart();
	}

	@Override
	protected void applyTransformation() {
		// Rotation around an axis by amount of degrees.
		mRotationAngle = mRotateFrom + (mInterpolatedTime * mDegreesToRotate);
		mQuat.fromAngleAxis(mRotationAxis, mRotationAngle);
		mQuat.multiply(mQuatFrom);
		mTransformable3D.setOrientation(mQuat);
	}

}
