package com.picovr.vr3dlibs.loader.awd;

import com.picovr.vr3dlibs.loader.LoaderAWD;
import com.picovr.vr3dlibs.loader.ParsingException;
import com.picovr.vr3dlibs.math.Matrix4;
import com.picovr.vr3dlibs.util.RajLog;

import java.io.IOException;

/**
 * @author Ian Thomas (toxicbakery@gmail.com)
 */
public class SceneGraphBlock {

    public final Matrix4 transformMatrix = new Matrix4();

    public int parentID;
    public String lookupName;

    public void readGraphData(LoaderAWD.BlockHeader blockHeader, LoaderAWD.AWDLittleEndianDataInputStream awddis) throws IOException,
            ParsingException {
        // parent id, reference to previously defined object
        parentID = awddis.readInt();

        // Transformation matrix
        awddis.readMatrix3D(transformMatrix, blockHeader.globalPrecisionMatrix, true);

        // Lookup name
        lookupName = awddis.readVarString();
        if (RajLog.isDebugEnabled())
            RajLog.d("  Lookup Name: " + lookupName);
    }

}
