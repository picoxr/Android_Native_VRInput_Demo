package com.picovr.vr3dlibs.loader.awd;

import android.util.SparseArray;

import com.picovr.vr3dlibs.loader.LoaderAWD;
import com.picovr.vr3dlibs.util.RajLog;

/**
 * 
 * @author Ian Thomas (toxicbakery@gmail.com)
 * 
 */
public class BlockMetaData extends ABlockParser {

	private static final short PROP_TIMESTAMP = 1;
	private static final short PROP_ENCODER_NAME = 2;
	private static final short PROP_ENCODER_VERSION = 3;
	private static final short PROP_GENERATOR_NAME = 4;
	private static final short PROP_GENERATOR_VERSION = 5;

	private static final SparseArray<Short> EXPECTED_PROPS;

	static {
		EXPECTED_PROPS = new SparseArray<Short>();
		EXPECTED_PROPS.put(PROP_TIMESTAMP, LoaderAWD.AWDLittleEndianDataInputStream.TYPE_UINT32);
		EXPECTED_PROPS.put(PROP_ENCODER_NAME, LoaderAWD.AWDLittleEndianDataInputStream.TYPE_AWDSTRING);
		EXPECTED_PROPS.put(PROP_ENCODER_VERSION, LoaderAWD.AWDLittleEndianDataInputStream.TYPE_AWDSTRING);
		EXPECTED_PROPS.put(PROP_GENERATOR_NAME, LoaderAWD.AWDLittleEndianDataInputStream.TYPE_AWDSTRING);
		EXPECTED_PROPS.put(PROP_GENERATOR_VERSION, LoaderAWD.AWDLittleEndianDataInputStream.TYPE_AWDSTRING);
	}

	private long mTimeStamp;
	private String mEncoderName;
	private String mEncoderVersion;
	private String mGeneratorName;
	private String mGeneratorVersion;

	public void parseBlock(LoaderAWD.AWDLittleEndianDataInputStream dis, LoaderAWD.BlockHeader blockHeader) throws Exception {

		final LoaderAWD.AwdProperties properties = dis.readProperties(EXPECTED_PROPS);
		mTimeStamp = (Long) properties.get(PROP_TIMESTAMP);
		mEncoderName = properties.get(PROP_ENCODER_NAME).toString();
		mEncoderVersion = properties.get(PROP_ENCODER_VERSION).toString();
		mGeneratorName = properties.get(PROP_GENERATOR_NAME).toString();
		mGeneratorVersion = properties.get(PROP_GENERATOR_VERSION).toString();

		if (RajLog.isDebugEnabled()) {
			RajLog.d("  Timestamp: " + mTimeStamp);
			RajLog.d("  Encoder Name: " + mEncoderName);
			RajLog.d("  Encoder Version: " + mEncoderVersion);
			RajLog.d("  Generator Name: " + mGeneratorName);
			RajLog.d("  Generator Version: " + mGeneratorVersion);
		}
	}

}
