package com.picovr.vr3dlibs.loader.awd;

import android.util.SparseArray;

import com.picovr.vr3dlibs.Object3D;
import com.picovr.vr3dlibs.loader.LoaderAWD;
import com.picovr.vr3dlibs.loader.ParsingException;
import com.picovr.vr3dlibs.materials.Material;
import com.picovr.vr3dlibs.math.Matrix4;
import com.picovr.vr3dlibs.math.Quaternion;
import com.picovr.vr3dlibs.math.vector.Vector3;
import com.picovr.vr3dlibs.util.RajLog;

/**
 * 
 * @author Ian Thomas (toxicbakery@gmail.com)
 */
public class BlockMeshInstance extends AExportableBlockParser {

	protected static final short PROP_CASTS_SHADOW = 5;

	protected Object3D mGeometry;
	protected SceneGraphBlock mSceneGraphBlock;
	protected boolean mCastsShadow;
	protected long mGeometryID;

	private static final SparseArray<Short>
		EXPECTED_PROPS = new SparseArray<Short>();

	static
	{
		EXPECTED_PROPS.put(PROP_CASTS_SHADOW, LoaderAWD.AWDLittleEndianDataInputStream.TYPE_BOOL);
	}

	@Override
	public Object3D getBaseObject3D() {
		return mGeometry;
	}

	public void parseBlock(LoaderAWD.AWDLittleEndianDataInputStream dis, LoaderAWD.BlockHeader blockHeader) throws Exception {

		// Parse scene block
        RajLog.d("Parsing SceneGraph Block at position: " + dis.getPosition());
		mSceneGraphBlock = new SceneGraphBlock();
		mSceneGraphBlock.readGraphData(blockHeader, dis);

		// Block id for geometry
		mGeometryID = dis.readUnsignedInt();

		// Lookup the geometry or create it if it does not exist.
		final LoaderAWD.BlockHeader geomHeader = blockHeader.blockHeaders.get((short) mGeometryID);
		if (geomHeader == null) {
			mGeometry = new Object3D(mSceneGraphBlock.lookupName);
		} else {
			if (geomHeader.parser == null
					|| !(geomHeader.parser instanceof ABaseObjectBlockParser))
				throw new ParsingException("Invalid block reference.");

			mGeometry = ((ABaseObjectBlockParser) geomHeader.parser).getBaseObject3D().clone(false, true);
			mGeometry.setName(mSceneGraphBlock.lookupName);
		}

		// Apply the materials
		final int materialCount = dis.readUnsignedShort();
		final Material[] materials = new Material[materialCount];
		for (int i = 0; i < materialCount; ++i) {
			final long materialID = dis.readUnsignedInt();
			if (materialID == 0) {
				materials[i] = getDefaultMaterial();
				materials[i].addTexture(getDefaultTexture());
			} else {
				final LoaderAWD.BlockHeader materialHeader = blockHeader.blockHeaders.get((short) materialID);
				if (materialHeader == null || materialHeader.parser == null
						|| !(materialHeader.parser instanceof ATextureBlockParser))
					throw new ParsingException("Invalid block reference " + materialID);

				materials[i] = ((ATextureBlockParser) materialHeader.parser).getMaterial();
			}
		}

		// mesh instance properties; does it cast a shadow?
		LoaderAWD.AwdProperties properties = dis.readProperties(EXPECTED_PROPS);
		mCastsShadow = (boolean)properties.get(PROP_CASTS_SHADOW, true);

		final Matrix4 matrix = new Matrix4(mSceneGraphBlock.transformMatrix);
		
		// Set translation
		mGeometry.setPosition(matrix.getTranslation());

		// Set scale
		final Vector3 scale = matrix.getScaling();
		mGeometry.setScale(scale.y, scale.x, scale.z);

		// Set rotation
		mGeometry.setOrientation(new Quaternion().fromMatrix(matrix));

		int m = 0;

		if(!mGeometry.isContainer())
			mGeometry.setMaterial(materials[m++]);

		for(int i = 0; i < mGeometry.getNumChildren(); i++)
			mGeometry.getChildAt(i).setMaterial(materials[Math.min(materials.length-1, m++)]);

		// ignore user properties, skip to end of block
		dis.skip(blockHeader.blockEnd - dis.getPosition());
	}
}
