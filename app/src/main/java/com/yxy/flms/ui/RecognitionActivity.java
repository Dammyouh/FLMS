/* Copyright 2016 Michael Sladoje and Mike Schälchli. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

/**
 * Created by mrh on 2017/11/3.
 */

package com.yxy.flms.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.yxy.flms.R;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.File;
import java.util.List;

import ch.zhaw.facerecognitionlibrary.Helpers.CustomCameraView;
import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognitionlibrary.Helpers.MatOperation;
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory;
import ch.zhaw.facerecognitionlibrary.Recognition.Recognition;
import ch.zhaw.facerecognitionlibrary.Recognition.RecognitionFactory;


public class RecognitionActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private CustomCameraView mRecognitionView;
    private static final String TAG = "Recognition";
    private FileHelper fh;
    private Recognition rec;
    private PreProcessorFactory ppF;
    private ProgressBar progressBar;
    private  boolean front_camera;
    private boolean night_portrait;
    private int exposure_compensation;
    private Button camera_Revert;
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recognition);

        camera_Revert = (Button) findViewById(R.id.camera_Revert);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        fh = new FileHelper();
        File folder = new File(fh.getFolderPath());
        if(folder.mkdir() || folder.isDirectory()){
            Log.i(TAG,"New directory for photos created");
        } else {
            Log.i(TAG,"Photos directory already existing");
        }
        mRecognitionView = (CustomCameraView) findViewById(R.id.RecognitionView);
        // Use camera which is selected in settings
        /*SharedPreferences sharedPref = getSharedPreferences("CameraSetting", Context.MODE_PRIVATE);
        front_camera = sharedPref.getBoolean("key_front_camera", true);
        night_portrait = sharedPref.getBoolean("key_night_portrait", false);
        exposure_compensation = Integer.valueOf(sharedPref.getString("key_exposure_compensation", "20"));*/

        front_camera = true;
        night_portrait = false;
        exposure_compensation = Integer.valueOf("20");

        camera_Revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                front_camera = !front_camera;
                if ( front_camera ) {
                    mRecognitionView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
                } else {
                    mRecognitionView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
                }
                mRecognitionView.invalidate();
                mRecognitionView.disableView();
                mRecognitionView.enableView();
                  }
            });
            mRecognitionView.setVisibility(SurfaceView.VISIBLE);
            mRecognitionView.setCvCameraViewListener(this);
            int maxCameraViewWidth = Integer.parseInt("640");//320
            int maxCameraViewHeight = Integer.parseInt("480");//240

            mRecognitionView.setMaxFrameSize(maxCameraViewWidth, maxCameraViewHeight);
        /*int maxCameraViewWidth = Integer.parseInt(sharedPref.getString("key_maximum_camera_view_width", "640"));
        int maxCameraViewHeight = Integer.parseInt(sharedPref.getString("key_maximum_camera_view_height", "480"));*/

    }


    @Override
    public void onPause()
    {
        super.onPause();
        if (mRecognitionView != null)
            mRecognitionView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mRecognitionView != null)
            mRecognitionView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {

        if (night_portrait) {
            mRecognitionView.setNightPortrait();
        }

        if (exposure_compensation != 50 && 0 <= exposure_compensation && exposure_compensation <= 100)
            mRecognitionView.setExposure(exposure_compensation);
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat imgRgba = inputFrame.rgba();
        Mat img = new Mat();
        imgRgba.copyTo(img);
        List<Mat> images = ppF.getProcessedImage(img, PreProcessorFactory.PreprocessingMode.RECOGNITION);
        Rect[] faces = ppF.getFacesForRecognition();

        // Selfie / Mirror mode
        if(front_camera){
            Core.flip(imgRgba,imgRgba,1);
        }
        if(images == null || images.size() == 0 || faces == null || faces.length == 0 || ! (images.size() == faces.length)){
            // skip
            return imgRgba;
        } else {
            faces = MatOperation.rotateFaces(imgRgba, faces, ppF.getAngleForRecognition());
            for(int i = 0; i<faces.length; i++){
                MatOperation.drawRectangleAndLabelOnPreview(imgRgba, faces[i], rec.recognize(images.get(i), ""), front_camera);
            }
            return imgRgba;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        ppF = new PreProcessorFactory(getApplicationContext());

        final Handler handler = new Handler(Looper.getMainLooper());
        Thread t = new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
                /*SharedPreferences sharedPref = getSharedPreferences("CameraSetting", Context.MODE_PRIVATE);
                String algorithm = sharedPref.getString("key_classification_method", getResources().getString(R.string.eigenfaces));*/

                String algorithm = getResources().getString(R.string.tensorflow);
                rec = RecognitionFactory.getRecognitionAlgorithm(getApplicationContext(), Recognition.RECOGNITION, algorithm);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        t.start();

        // Wait until Eigenfaces loading thread has finished
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mRecognitionView.enableView();
        if ( front_camera ) {
            mRecognitionView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        } else {
            mRecognitionView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
        }
    }
}
