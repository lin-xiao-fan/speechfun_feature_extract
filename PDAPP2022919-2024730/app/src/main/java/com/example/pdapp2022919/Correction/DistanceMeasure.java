package com.example.pdapp2022919.Correction;

import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pdapp2022919.Database.Correction.Correction;
import com.example.pdapp2022919.Database.Correction.CorrectionDao;
import com.example.pdapp2022919.ListPage;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.DatabaseManager;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import java.io.IOException;

public class DistanceMeasure extends ScreenSetting {
    private float distance;
    private CameraSource cameraSource;
    private Button finishButton;
    private ImageView demoPicture;
    private TextView disHint;
    static final int IMAGE_WIDTH = 1024;
    static final int IMAGE_HEIGHT = 1024;

    static final int RIGHT_EYE = 0;
    static final int LEFT_EYE = 1;

    static final int AVERAGE_EYE_DISTANCE = 63; // in mm

    private TextView textView;

    float F = 1f;           //focal length
    float sensorX, sensorY; //camera sensor dimensions
    float angleX, angleY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_measure);
        hideSystemUI();
        demoPicture = findViewById(R.id.demoPicture);
        finishButton = findViewById(R.id.finishButton);
        disHint = findViewById(R.id.DistanceHint);
        Correction correction = getIntent().getParcelableExtra(NameManager.CORRECTION_OBJ);
        finishButton.setOnClickListener(view -> {

            startActivity(new Intent(this, ListPage.class));
            new Thread(() -> {
                correction.Distance_cm = distance / 10;
                CorrectionDao dao = DatabaseManager.getInstance(this).correctionDao();
                dao.addCorrection(correction);
            }).start();
        });
        Camera camera = frontCam();
        Camera.Parameters campar = camera.getParameters();
        F = campar.getFocalLength();
        angleX = campar.getHorizontalViewAngle();
        angleY = campar.getVerticalViewAngle();
        sensorX = (float) (Math.tan(Math.toRadians(angleX / 2)) * 2 * F);
        sensorY = (float) (Math.tan(Math.toRadians(angleY / 2)) * 2 * F);
        camera.stopPreview();
        camera.release();
        textView = findViewById(R.id.text);
        createCameraSource();
    }


    private Camera frontCam() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            Log.v("CAMID", camIdx + "");
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("FAIL", "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }


    @SuppressLint("MissingPermission")
    public void createCameraSource() {
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        detector.setProcessor(new LargestFaceFocusingProcessor(detector, new FaceTracker()));

        cameraSource = new CameraSource.Builder(this, detector)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(100f)
                .build();
        System.out.println(cameraSource.getPreviewSize());

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraSource.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showStatus(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(message);
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        if (cameraSource == null) return;
        try {
            cameraSource.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.stop();
    }

    private class FaceTracker extends Tracker<Face> {

        private int delay = 0;

        private FaceTracker() {

        }

        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {
            if (delay++ % 5 != 0) return;
            PointF leftEyePos = face.getLandmarks().get(LEFT_EYE).getPosition();
            PointF rightEyePos = face.getLandmarks().get(RIGHT_EYE).getPosition();

            float deltaX = Math.abs(leftEyePos.x - rightEyePos.x);
            float deltaY = Math.abs(leftEyePos.y - rightEyePos.y);

            if (deltaX >= deltaY) {
                distance = F * (AVERAGE_EYE_DISTANCE / sensorX) * (IMAGE_WIDTH / deltaX);
            } else {
                distance = F * (AVERAGE_EYE_DISTANCE / sensorY) * (IMAGE_HEIGHT / deltaY);
            }

            showStatus("距離: " + String.format("%.0f", distance / 10) + "公分");

            if (distance >= 400) {
                runOnUiThread(() -> {
                    demoPicture.setImageResource(R.drawable.more_close);
                    disHint.setText("請拿近一些");
                    disHint.setTextColor(Color.parseColor("#FF0000"));
                });
            } else if (distance <= 350) {
                runOnUiThread(() -> {
                    demoPicture.setImageResource(R.drawable.more_far);
                    disHint.setText("請拿遠一些");
                    disHint.setTextColor(Color.parseColor("#FF0000"));
                });
            } else {
                runOnUiThread(() -> {
                    demoPicture.setImageResource(R.drawable.ok);
                    disHint.setText("很好~~!");
                    disHint.setTextColor(Color.parseColor("#008000"));
                });
            }
        }


        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);
            showStatus("未偵測到臉部");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    demoPicture.setImageResource(R.drawable.question);
                    disHint.setTextColor(Color.parseColor("#FFFFFF"));
                }
            });
        }

        @Override
        public void onDone() {
            super.onDone();
        }
    }

}