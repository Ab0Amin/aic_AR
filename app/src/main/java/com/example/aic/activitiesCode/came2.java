package com.example.aic.activitiesCode;

import android.Manifest;
import android.app.*;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.aic.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.File;
import java.io.IOException;
import java.security.Permission;
import java.util.concurrent.CompletableFuture;

public class came2 extends AppCompatActivity {
    private static final String TAG = came2.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ModelRenderable andyRenderable;
    private ArFragment arFragment;
private  byte counter =0;
    private CompletableFuture<ModelRenderable> dowenloadedModel;
//    String GLTF_ASSET = "https://backendlessappcontent.com/B71822DD-2FD2-4D4A-8938-A9C8B75CA29F/2678B09E-FA4E-4AD9-A917-70129248F552/files/model+files/out.glb";
    String uri = "https://backendlessappcontent.com/B71822DD-2FD2-4D4A-8938-A9C8B75CA29F/2678B09E-FA4E-4AD9-A917-70129248F552/files/model+files/out.glb";
    private static final int  WRITE_PERMISION=1001;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }
        setContentView(R.layout.activity_came2);

        arFragment=(ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
      download();
        setUpModel();
        setUpPlane();

    }

    private void setUpPlane() {
        if (counter <1) {


            arFragment.setOnTapArPlaneListener((
                    HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                if (andyRenderable == null) {
                    Toast.makeText(this, "null reneder", Toast.LENGTH_LONG);

                    return;
                }

                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());
                TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
                node.setParent(anchorNode);
                node.setRenderable(andyRenderable);

                Vector3 direction = new Vector3(0, -1, 0);
                Quaternion lookRotation = Quaternion.lookRotation(direction, Vector3.down());

                node.setWorldRotation(lookRotation);
                node.getScaleController().setEnabled(true);
                node.getRotationController().setEnabled(true);
                node.select();
                counter++;

            });
        }
    }



    private void setUpModel() {
        if (counter >=1) {
return;
        }



        RenderableSource.Builder bul = RenderableSource.builder().setSource(
                this,
//                Uri.parse(file.getPath()),
                Uri.parse(Environment.DIRECTORY_DOWNLOADS),
                RenderableSource.SourceType.GLB);

        RenderableSource h = bul.setScale(5f)  // Scale the original model to 50%.
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .build();

        dowenloadedModel = ModelRenderable.builder()
                .setSource(this,h).setRegistryId(Environment.DIRECTORY_DOWNLOADS)
                .build();
        ;


        if (dowenloadedModel.isDone()) {
            dowenloadedModel.thenAccept(renderable -> andyRenderable = renderable)
                    .exceptionally(
                            throwable -> {
                                Toast toast =
                                        Toast.makeText(this, "Unable to load renderable ", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return null;
                            });
        }


//            ModelRenderable.builder()
//                .setSource(this, R.raw.building)
//                .build()
//                .thenAccept(renderable -> andyRenderable = renderable)
//                .exceptionally(
//                        throwable -> {
//                            Toast toast =
//                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
//                            toast.setGravity(Gravity.CENTER, 0, 0);
//                            toast.show();
//                            return null;
//                        });

    }

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    private void download(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
             String fileName ="out";
             dowenloadFile(fileName,uri);
            }
            else {
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE },WRITE_PERMISION);
            }

        }
    }

    private void dowenloadFile(String fileName, String uri) {
        Uri dowelodUri = Uri.parse(uri);
        DownloadManager manager =(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        try {

            if (manager != null) {
                 file =File.createTempFile("out1","glb");


                DownloadManager.Request request=new DownloadManager.Request(dowelodUri);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_MOBILE)
                        .setTitle(fileName)
                        .setDescription("Dowenloading "+fileName)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                        .setDestinationInExternalPublicDir(file.getPath(),fileName)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName)
                .setMimeType(getMimeType(dowelodUri));
                manager.enqueue(request);
                Toast.makeText(this, "start Dowenloading", Toast.LENGTH_SHORT).show();

        }else {
                Intent intent =new Intent(Intent.ACTION_VIEW,dowelodUri);
                startActivity(intent);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String  getMimeType(Uri uri){
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap =MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_PERMISION) {
            if (grantResults.length >0&& grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                String fileName ="out";
                dowenloadFile(fileName,uri);
            }
            else Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadbu(View view) {
        download();
    }
}