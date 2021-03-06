package com.picovr.vr3dlibs.loader.awd;

import com.picovr.vr3dlibs.animation.mesh.SkeletalAnimationFrame;
import com.picovr.vr3dlibs.animation.mesh.SkeletalAnimationSequence;
import com.picovr.vr3dlibs.loader.LoaderAWD;
import com.picovr.vr3dlibs.loader.ParsingException;

/**
 * Groups a series of BlockSkeletonPose frames into an animation. This block
 * produces a single SkeletalAnimationSequence object.
 * 
 * @author Ian Thomas (toxicbakery@gmail.com)
 * @author Bernard Gorman (bernard.gorman@gmail.com)
 */
public class BlockSkeletonAnimation extends ABlockParser {

	protected SkeletalAnimationSequence mSkelAnim;

	protected String mLookupName;
	protected int mNumFrames;

	public void parseBlock(LoaderAWD.AWDLittleEndianDataInputStream dis, LoaderAWD.BlockHeader blockHeader) throws Exception {

		// Lookup name
		mLookupName = dis.readVarString();

		// Number of animation poses
		mNumFrames = dis.readUnsignedShort();

		// skip block properties
		dis.readProperties(null);

		SkeletalAnimationFrame[] frames = new SkeletalAnimationFrame[mNumFrames];
		double[] frameDurations = new double[mNumFrames];

		for(int i = 0; i < mNumFrames; i++)
		{
			long poseAddr = dis.readUnsignedInt();
			int duration = dis.readUnsignedShort();

			// TODO: can animation frames be shared between animations? Clone?
			SkeletalAnimationFrame frame = lookup(blockHeader, poseAddr);
			frame.setFrameIndex(i);

			frameDurations[i] = duration;
			frames[i] = frame;
		}

		// skip user properties
		dis.readProperties(null);

		mSkelAnim = new SkeletalAnimationSequence(mLookupName);
		mSkelAnim.setFrameData(frameDurations);
		mSkelAnim.setFrames(frames);
	}

	private SkeletalAnimationFrame lookup(LoaderAWD.BlockHeader blockHeader, long addr) throws ParsingException
	{
		final LoaderAWD.BlockHeader lookupHeader = blockHeader.blockHeaders.get((int) addr);

		if (lookupHeader == null || lookupHeader.parser == null
				|| !(lookupHeader.parser instanceof BlockSkeletonPose))
			throw new ParsingException("Invalid block reference.");

		return ((BlockSkeletonPose) lookupHeader.parser).mPose;
	}

	public SkeletalAnimationSequence getAnimation()
	{
		return mSkelAnim;
	}
}
