package com.example.aic.activitiesCode;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.aic.R;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

public class came2 extends AppCompatActivity {
    private ModelRenderable andyRenderable;
    private ArFragment arFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_came2);

        arFragment=(ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        setUpModel();
    }

    private void setUpModel() {
//        ModelRenderable.builder()
//                .setSource(this,R.raw.)
    }
}