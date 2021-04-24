package com.example.aic.activitiesCode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.aic.R;
import com.example.aic.helper.CameraPermissionHelper;
import com.example.aic.helper.SnackbarHelper;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.CameraConfig;
import com.google.ar.core.CameraConfigFilter;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.core.SharedCamera;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.lang.reflect.Array;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;

public class MycamActivity extends AppCompatActivity {

    private Session session;
    private boolean installRequested;
    private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();
    private static final String TAG = MycamActivity.class.getSimpleName();
    private CameraManager cameraManager;
    private SharedCamera sharedCamera;
    private android.os.Handler backgroundHandler=new android.os.Handler();
    private CameraDevice.StateCallback cameraDeviceCallback;
    private String CameraID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycam);

        isARCoreSupportedAndUpToDate();

        // Create an ARCore session.
        Session session = null;
        try {
            session = new Session(this);
        } catch (UnavailableArcoreNotInstalledException e) {
            e.printStackTrace();
        } catch (UnavailableApkTooOldException e) {
            e.printStackTrace();
        } catch (UnavailableSdkTooOldException e) {
            e.printStackTrace();
        } catch (UnavailableDeviceNotCompatibleException e) {
            e.printStackTrace();
        }

// Create a camera config filter for the session.
        CameraConfigFilter filter = new CameraConfigFilter(session);

// Return only camera configs that target 30 fps camera capture frame rate.
        filter.setTargetFps(EnumSet.of(CameraConfig.TargetFps.TARGET_FPS_30));

// Return only camera configs that will not use the depth sensor.
        filter.setDepthSensorUsage(EnumSet.of(CameraConfig.DepthSensorUsage.DO_NOT_USE));

// Get list of configs that match filter settings.
// In this case, this list is guaranteed to contain at least one element,
// because both TargetFps.TARGET_FPS_30 and DepthSensorUsage.DO_NOT_USE
// are supported on all ARCore supported devices.
        List<CameraConfig> cameraConfigList = session.getSupportedCameraConfigs(filter);

// Use element 0 from the list of returned camera configs. This is because
// it contains the camera config that best matches the specified filter
// settings.
        session.setCameraConfig(cameraConfigList.get(0));
        try {
            session = new Session(MycamActivity.this, EnumSet.of(Session.Feature.SHARED_CAMERA));
        } catch (UnavailableArcoreNotInstalledException e) {
            e.printStackTrace();
        } catch (UnavailableApkTooOldException e) {
            e.printStackTrace();
        } catch (UnavailableSdkTooOldException e) {
            e.printStackTrace();
        } catch (UnavailableDeviceNotCompatibleException e) {
            e.printStackTrace();
        }
        sharedCamera = session.getSharedCamera();
        // Wrap the callback in a shared camera callback.
        CameraDevice.StateCallback wrappedCallback =

                sharedCamera.createARDeviceStateCallback(cameraDeviceCallback, backgroundHandler);

// Store a reference to the camera system service.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
        }

// Open the camera device using the ARCore wrapped callback.
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
             String [] set = cameraManager.getCameraIdList();
             //
//                      Object[] sets  = ids.toArray();
//                Object[] set = (Object[]) sets[0];
//                CameraID= (String) set[0];
                cameraManager.openCamera(set[0], wrappedCallback, backgroundHandler);
            } catch (@SuppressLint("NewApi") CameraAccessException e) {
                e.printStackTrace();
            }
        }


    }


    @Override
    protected void onDestroy() {
        if (session != null) {
            // Explicitly close ARCore Session to release native resources.
            // Review the API reference for important considerations before calling close() in apps with
            // more complicated lifecycle requirements:
            // https://developers.google.com/ar/reference/java/arcore/reference/com/google/ar/core/Session#close()
            session.close();
            session = null;
        }

        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (session == null) {
            Exception exception = null;
            String message = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    CameraPermissionHelper.requestCameraPermission(this);
                    return;
                }

                // Create the session.
                session = new Session(/* context= */ this);
            } catch (UnavailableArcoreNotInstalledException
                    | UnavailableUserDeclinedInstallationException e) {
                message = "Please install ARCore";
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                message = "Please update ARCore";
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                message = "Please update this app";
                exception = e;
            } catch (UnavailableDeviceNotCompatibleException e) {
                message = "This device does not support AR";
                exception = e;
            } catch (Exception e) {
                message = "Failed to create AR session";
                exception = e;
            }

            if (message != null) {
                messageSnackbarHelper.showError(this, message);
                Log.e(TAG, "Exception creating session", exception);
                return;
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            // Use toast instead of snackbar here since the activity will exit.
            Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                    .show();
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
    }

    // Verify that ARCore is installed and using the current version.
    private boolean isARCoreSupportedAndUpToDate() {
       boolean res=false;
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        switch (availability) {
            case SUPPORTED_INSTALLED:
                res= true;
//                Toast.makeText(this, "supported", Toast.LENGTH_SHORT).show();

            case SUPPORTED_APK_TOO_OLD:
            case SUPPORTED_NOT_INSTALLED:
                try {
                    // Request ARCore installation or update if needed.
                    ArCoreApk.InstallStatus installStatus = ArCoreApk.getInstance().requestInstall(this, true);
                    switch (installStatus) {
                        case INSTALL_REQUESTED:
                            Log.i(TAG, "ARCore installation requested.");
                            res= false;
                        case INSTALLED:
                            res= true;
                    }
                } catch (UnavailableException e) {
                    Log.e(TAG, "ARCore not installed", e);
                }
                res= false;

            case UNSUPPORTED_DEVICE_NOT_CAPABLE:
                // This device is not supported for AR.
                res= false;

            case UNKNOWN_CHECKING:
                // ARCore is checking the availability with a remote query.
                // This function should be called again after waiting 200 ms to determine the query result.
            case UNKNOWN_ERROR:
            case UNKNOWN_TIMED_OUT:
                // There was an error checking for AR availability. This may be due to the device being offline.
                // Handle the error appropriately.
        }
        return res;
    }

//
//    public void createSession() throws UnavailableSdkTooOldException, UnavailableDeviceNotCompatibleException, UnavailableArcoreNotInstalledException, UnavailableApkTooOldException {
//        // Create a new ARCore session.
//        session = new Session(this);
//
//        // Create a session config.
//        Config config = new Config(session);
//
//        // Do feature-specific operations here, such as enabling depth or turning on
//        // support for Augmented Faces.
//
//        // Configure the session.
//        session.configure(config);
//    }

}