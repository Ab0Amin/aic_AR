package com.example.aic.activitiesCode;

import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.aic.R;

public class projects extends AppCompatActivity {
ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        bar =findViewById(R.id.progressBar1);
        bar.setVisibility(View.INVISIBLE);
    }

    public void AR(View view) {
        Intent in = new Intent(this, came2.class);
        startActivity(in);

    }

    public void _3d(View view) {
       bar.setVisibility(View.VISIBLE);
        Intent in = new Intent(this, _3D_objectViewer.class);
        startActivity(in);

    }

    @Override
    protected void onStart() {
        super.onStart();
        bar.setVisibility(View.INVISIBLE);

    }



}
