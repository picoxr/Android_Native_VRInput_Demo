package com.picovr.vr3dlibs.loader.awd;

import com.picovr.vr3dlibs.animation.mesh.IAnimationSequence;
import com.picovr.vr3dlibs.animation.mesh.SkeletalAnimationSequence;
import com.picovr.vr3dlibs.loader.LoaderAWD;
import com.picovr.vr3dlibs.loader.ParsingException;

/**
 * Groups a number of BlockSkeletonAnimations or BlockMeshPoseAnimations
 * together. The BlockAnimator which actually binds the various animation
 * components together only references BlockAnimationSets, rather than
 * individual animation sequences or frames.
 * 
 * @author Ian Thomas (toxicbakery@gmail.com)
 * @author Bernard Gorman (bernard.gorman@gmail.com)
 * 
 */
public class BlockAnimationSet extends ABlockParser {

	protected IAnimationSequence[] mAnimSet;

	protected String mLookupName;
	protected int mNumAnims;

	public void parseBlock(LoaderAWD.AWDLittleEndianDataInputStream dis, LoaderAWD.BlockHeader blockHeader) throws Exception {

		// Lookup name
		mLookupName = dis.readVarString();

		// number of animations in the set
		mNumAnims = dis.readUnsignedShort();

		// skip block properties
		dis.readProperties(null);

		// NOTE: this block is for generic animations, not only skeletal
		mAnimSet = new IAnimationSequence[mNumAnims];

		for(int i = 0; i < mNumAnims; i++)
		{
			long animaddr = dis.readUnsignedInt();

			mAnimSet[i] = lookup(blockHeader, animaddr);
		}

		// skip user properties
		dis.readProperties(null);
	}

	private SkeletalAnimationSequence lookup(LoaderAWD.BlockHeader blockHeader, long addr) throws ParsingException
	{
		final LoaderAWD.BlockHeader lookupHeader = blockHeader.blockHeaders.get((int) addr);

		if (lookupHeader == null || lookupHeader.parser == null
			|| (!(lookupHeader.parser instanceof BlockSkeletonAnimation)
				&& !(lookupHeader.parser instanceof BlockMeshPoseAnimation)))
			throw new ParsingException("Invalid block reference.");

		if(lookupHeader.parser instanceof BlockSkeletonAnimation)
			return ((BlockSkeletonAnimation) lookupHeader.parser).mSkelAnim;
		else
			return null; // BlockMeshPoseAnimation NYI
	}
}
