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
//import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Permission;
import java.util.concurrent.CompletableFuture;

public class came2 extends AppCompatActivity {
    private static final String TAG = came2.class.getSimpleName();

    private ModelRenderable andyRenderable1;
    private ModelRenderable andyRenderable2;
    private ModelRenderable andyRenderable3;
    private ModelRenderable andyRenderable4;


    private ArFragment arFragment1;
    static projects PR_code ;
float x=0,y=0,z=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
String code = PR_code.code.getText().toString();

        setContentView(R.layout.activity_came2);

        arFragment1 = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        if (code.equals("E205")) {
            setUpModel1();
            setUpModel2();
            setUpModel3();
y=-1;
        }
        else   if (code.equals("E206")) {
            setUpModel10();

        }


//        setUpModel4();
        setUpPlane();



    }


    private void setUpPlane() {


        arFragment1.setOnTapArPlaneListener((
                HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
            if (andyRenderable1 == null) {
                Toast.makeText(this, "null reneder", Toast.LENGTH_LONG);

                return;
            }
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment1.getArSceneView().getScene());
            createAnchor(anchorNode, andyRenderable1);

        });
    }

    private void createAnchor(  AnchorNode anchor,ModelRenderable andyRenderable) {
        TransformableNode node1 = new TransformableNode(arFragment1.getTransformationSystem());
        node1.setParent(anchor);
        node1.setRenderable(andyRenderable2);

        TransformableNode node12 = new TransformableNode(arFragment1.getTransformationSystem());
        node12.setParent(anchor);
        node12.setRenderable(andyRenderable3);

        TransformableNode node123 = new TransformableNode(arFragment1.getTransformationSystem());
        node123.setParent(anchor);
        node123.setRenderable(andyRenderable4);

        TransformableNode node = new TransformableNode(arFragment1.getTransformationSystem());
        node.setParent(anchor);
        node.setRenderable(andyRenderable);
        Vector3 direction = new Vector3(x, y, z);
        Quaternion lookRotation = Quaternion.lookRotation(direction, Vector3.up());


        Vector3 direction1 = new Vector3(0.004f/*x*/, 0.003f/*y*/, 0.0014f /*z*/);
        Vector3 direction12 = new Vector3(-0.001f/*x*/, 0.005f/*y*/, -.04f /*z*/);
        Vector3 direction123 = new Vector3(-0.11f/*x*/, 0.025f/*y*/, -.068f /*z*/);



        node.setWorldRotation(lookRotation);
        node.getScaleController().setEnabled(true);
        node.getRotationController().setEnabled(true);

        node1.setLocalPosition(direction1);
        node12.setLocalPosition(direction12);
        node123.setLocalPosition(direction123);
//        node.setLocalPosition(direction1);
        Vector3 zero = new Vector3(0, -1, 0);



        node.addChild(node1);
        node.addChild(node12);
        node.addChild(node123);

        node1.onDeactivate();
        node12.onDeactivate();
        node123.onDeactivate();
        node.select();

    }
    private void setUpModel10() {

        ModelRenderable.builder()
                .setSource(this,  R.raw.uderobj5)
                .build()
                .thenAccept(renderable -> andyRenderable1 = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

    }


    private void setUpModel1() {

        ModelRenderable.builder()
                .setSource(this,  R.raw.tryg)
                .build()
                .thenAccept(renderable -> andyRenderable1 = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

    }



    private void setUpModel2() {

        ModelRenderable.builder()
                .setSource(this,  R.raw.tryg3)
                .build()
                .thenAccept(renderable -> andyRenderable2 = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

    }

    private void setUpModel3() {

        ModelRenderable.builder()
                .setSource(this,  R.raw.tryg4)
                .build()
                .thenAccept(renderable -> andyRenderable3 = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

    }



    private void setUpModel4() {

        ModelRenderable.builder()
                .setSource(this,  R.raw.bolt1)
                .build()
                .thenAccept(renderable -> andyRenderable4 = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

    }



    public void back(View view) {
        onBackPressed();
    }
}
