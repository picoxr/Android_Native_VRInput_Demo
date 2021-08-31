package com.picovr.android_native_vrinput_demo

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.picovr.cvclient.CVController
import com.picovr.cvclient.CVControllerListener
import com.picovr.cvclient.CVControllerManager
import com.picovr.vractivity.HmdState
import com.picovr.vrlibs.VRActivity

class MainActivity : VRActivity() {

    private val TAG = "===TAG==="
    private var mRenderer: MainRender? = null

    private var cvManager: CVControllerManager? = null
    private var rightController: CVController? = null
    private var leftController: CVController? = null

    private val cvListener: CVControllerListener = object : CVControllerListener {
        override fun onBindSuccess() {}
        override fun onBindFail() {
            Log.d(TAG, "bind fail")
        }

        override fun onThreadStart() {
            rightController = cvManager!!.rightController
            leftController = cvManager!!.leftController
            mRenderer!!.setcvController(rightController, leftController)
        }

        override fun onConnectStateChanged(i: Int, i1: Int) {
            Log.d(TAG, "cvController $i state is $i1")
        }

        override fun onMainControllerChanged(i: Int) {
            rightController = cvManager!!.rightController
            leftController = cvManager!!.leftController
            mRenderer!!.setcvController(rightController, leftController)
        }

        override fun onChannelChanged(i: Int, i1: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, (
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                            or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        )

        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        cvManager = CVControllerManager(this.applicationContext)
        cvManager!!.setListener(cvListener)
        rightController = cvManager!!.rightController
        leftController = cvManager!!.leftController

        mRenderer = MainRender(this)
        mRenderer!!.setcvController(rightController, leftController)
        setRenderer(mRenderer)
        Log.d(TAG, "onCreate")
    }

    override fun onPause() {
        super.onPause()
        cvManager!!.unbindService()
    }

    override fun onResume() {
        super.onResume()
        cvManager!!.bindService()
    }

    override fun onFrameBegin(hmdState: HmdState?) {
        val hmdOrientation = hmdState!!.orientation
        val hmdPosition = hmdState!!.pos
        val hmdData = FloatArray(7)
        hmdData[0] = hmdOrientation[0]
        hmdData[1] = hmdOrientation[1]
        hmdData[2] = hmdOrientation[2]
        hmdData[3] = hmdOrientation[3]

        hmdData[4] = hmdPosition[0]
        hmdData[5] = hmdPosition[1]
        hmdData[6] = hmdPosition[2]

        cvManager!!.updateControllerData(hmdData)
        super.onFrameBegin(hmdState)
    }

    override fun onFrameEnd() {
        super.onFrameEnd()
    }
}