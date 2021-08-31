package com.picovr.vr3dlibs.loader.awd;

import android.util.SparseArray;

import com.picovr.vr3dlibs.Object3D;
import com.picovr.vr3dlibs.animation.mesh.IAnimationSequence;
import com.picovr.vr3dlibs.animation.mesh.SkeletalAnimationChildObject3D;
import com.picovr.vr3dlibs.animation.mesh.SkeletalAnimationFrame;
import com.picovr.vr3dlibs.animation.mesh.SkeletalAnimationObject3D;
import com.picovr.vr3dlibs.animation.mesh.SkeletalAnimationSequence;
import com.picovr.vr3dlibs.loader.LoaderAWD;
import com.picovr.vr3dlibs.loader.ParsingException;
import com.picovr.vr3dlibs.materials.plugins.SkeletalAnimationMaterialPlugin;
import com.picovr.vr3dlibs.math.Matrix4;

/**
 * This class is responsible for binding a BlockMeshInstance to a BlockSkeleton
 * and assigning it a single BlockAnimationSet. It is the final stage in
 * the AWD animation "pipeline".
 * 
 * @author Ian Thomas (toxicbakery@gmail.com)
 * @author Bernard Gorman (bernard.gorman@gmail.com)
 * 
 */
public class BlockAnimator extends ABlockParser {

	private final static int TYPE_SKELETAL_ANIM = 1;
	private final static int TYPE_VERTEX_ANIM = 2;

	protected String mLookupName;

	// may be skeletal or vertex, keep generic
	protected IAnimationSequence[] mAnimSet;
	protected Object3D[] mTargets;

	protected boolean mAutoPlay;
	protected int mActive;

	protected static final short PROP_SKELETON = 1;
	protected static final short PROP_CONDENSED = 701;

	private static final SparseArray<Short>
		EXPECTED_PROPS = new SparseArray<Short>();

	static
	{
		EXPECTED_PROPS.put(PROP_SKELETON, LoaderAWD.AWDLittleEndianDataInputStream.TYPE_BADDR);
		EXPECTED_PROPS.put(PROP_CONDENSED, LoaderAWD.AWDLittleEndianDataInputStream.TYPE_BOOL);
	}

	public void parseBlock(LoaderAWD.AWDLittleEndianDataInputStream dis, LoaderAWD.BlockHeader blockHeader) throws Exception {

		// Lookup name
		mLookupName = dis.readVarString();

		// Animation type (skeletal or vertex)
		int type = dis.readUnsignedShort(); // u16 type

		LoaderAWD.AwdProperties properties = null;

		switch(type)
		{
			case TYPE_SKELETAL_ANIM: // NumAttrList props

				properties = dis.readProperties(EXPECTED_PROPS);

				break;

			case TYPE_VERTEX_ANIM: // NumAttrList props

				dis.readProperties(null);
		}

		// the animation set to be assigned
		long animSetID = dis.readUnsignedInt();

		mAnimSet = lookupAnimationSet(blockHeader, animSetID);

		int numTargets = dis.readUnsignedShort();

		mTargets = new Object3D[numTargets];

		// the mesh animation targets
		for(int i = 0; i < numTargets; i++)
			mTargets[i] = lookupMesh(blockHeader, dis.readUnsignedInt());

		// the active animation & autoplay setting
		mActive = dis.readUnsignedShort();
		mAutoPlay = dis.readBoolean();

		//skip block & user properties
		dis.readProperties(null);
		dis.readProperties(null);

		if(type == TYPE_SKELETAL_ANIM)
		{
			buildSkeleton(blockHeader, (Long)
				properties.get(PROP_SKELETON, 0L));
		}

		// TYPE_VERTEX_ANIM NYI
	}

	// apply joint hierarchy to joint pose frames
	private void buildSkeleton(LoaderAWD.BlockHeader blockHeader, long skelAddr) throws ParsingException
	{
		SkeletalAnimationFrame.SkeletonJoint[] joints = lookupSkeleton(blockHeader, skelAddr);

		SkeletalAnimationSequence[] skelAnims =
			new SkeletalAnimationSequence[mAnimSet.length];

		for(int i = 0; i < mAnimSet.length; i++)
			skelAnims[i] = (SkeletalAnimationSequence)mAnimSet[i];

		Matrix4 scratch1 = new Matrix4();
		Matrix4 scratch2 = new Matrix4();

		for(SkeletalAnimationSequence skelSeq : skelAnims)
		{
			for(SkeletalAnimationFrame frame : skelSeq.getFrames())
			{
				SkeletalAnimationFrame.SkeletonJoint[] poses = frame.getSkeleton().getJoints();

				// apply parent transforms
				for(int i = 0; i < poses.length; i++)
				{
					// matrix and index already set, need parent & other attribs
					poses[i].setParentIndex(joints[i].getParentIndex());

					if(poses[i].getParentIndex() >= 0) // has parent joint
			        {
			            SkeletalAnimationFrame.SkeletonJoint parentPose = poses[poses[i].getParentIndex()];

						scratch1.setAll(parentPose.getMatrix())
							.multiply(scratch2.setAll(poses[i].getMatrix()));

						poses[i].setMatrix(scratch1.getDoubleValues());
			        }
					else
						scratch1.setAll(poses[i].getMatrix());

					// assign pos + rot from final matrix
					scratch1.getTranslation(poses[i].getPosition());
					poses[i].getOrientation().fromMatrix(scratch1);
					poses[i].getOrientation().computeW();
				}
			}
		}

		for(int i = 0; i < mTargets.length; i++)
		{
			SkeletalAnimationObject3D obj =
				(SkeletalAnimationObject3D)mTargets[i];

			// assigns INVBP, builds BP, sets joints
			obj.setJointsWithInverseBindPoseMatrices(joints);

			for(int j = 0; j < obj.getNumChildren(); j++)
			{
				SkeletalAnimationChildObject3D child =
					(SkeletalAnimationChildObject3D)obj.getChildAt(j);

				SkeletalAnimationMaterialPlugin
					plugin = new SkeletalAnimationMaterialPlugin
						(child.getNumJoints(), child.getMaxBoneWeightsPerVertex());

				child.getMaterial().addPlugin(plugin);
			}

			obj.setAnimationSequences(skelAnims);
			obj.setAnimationSequence(mActive);

			if(mAutoPlay)
				obj.play(true);
		}
	}

	private SkeletalAnimationFrame.SkeletonJoint[] lookupSkeleton(LoaderAWD.BlockHeader blockHeader, long addr) throws ParsingException
	{
		final LoaderAWD.BlockHeader lookupHeader = blockHeader.blockHeaders.get((int) addr);

		if (lookupHeader == null || lookupHeader.parser == null
				|| !(lookupHeader.parser instanceof BlockSkeleton))
			throw new ParsingException("Invalid block reference.");

		return ((BlockSkeleton) lookupHeader.parser).mJoints;
	}

	private IAnimationSequence[] lookupAnimationSet(LoaderAWD.BlockHeader blockHeader, long addr) throws ParsingException
	{
		final LoaderAWD.BlockHeader lookupHeader = blockHeader.blockHeaders.get((int) addr);

		if (lookupHeader == null || lookupHeader.parser == null
				|| !(lookupHeader.parser instanceof BlockAnimationSet))
			throw new ParsingException("Invalid block reference.");

		return ((BlockAnimationSet) lookupHeader.parser).mAnimSet;
	}

	private Object3D lookupMesh(LoaderAWD.BlockHeader blockHeader, long addr) throws ParsingException
	{
		final LoaderAWD.BlockHeader lookupHeader = blockHeader.blockHeaders.get((int) addr);

		if (lookupHeader == null || lookupHeader.parser == null
				|| !(lookupHeader.parser instanceof BlockMeshInstance))
			throw new ParsingException("Invalid block reference.");

		return ((BlockMeshInstance) lookupHeader.parser).getBaseObject3D();
	}
}
