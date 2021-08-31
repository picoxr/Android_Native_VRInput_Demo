package com.picovr.vr3dlibs.primitives;

import com.picovr.vr3dlibs.cameras.Camera;
import com.picovr.vr3dlibs.materials.Material;
import com.picovr.vr3dlibs.math.Matrix4;
import com.picovr.vr3dlibs.math.vector.Vector3;

public class PointSprite extends Plane {
	public PointSprite(float width, float height) {
		super(width, height, 1, 1, Vector3.Axis.Z);
	}

    public PointSprite(float width, float height, boolean createVBOs) {
        super(width, height, 1, 1, Vector3.Axis.Z, true, false, 1, createVBOs);
    }
	
	@Override
	public void render(Camera camera, final Matrix4 vpMatrix, final Matrix4 projMatrix, final Matrix4 vMatrix,
					   final Matrix4 parentMatrix, Material sceneMaterial) {
		setLookAt(camera.getPosition());		
		super.render(camera, vpMatrix, projMatrix, vMatrix, parentMatrix, sceneMaterial);
	}
}
