package com.example.aic.activitiesCode;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.aic.R;

public class projects extends AppCompatActivity {
ProgressBar bar;
ImageView projects ;
EditText  code;
Button bt_3d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
projects = findViewById(R.id.imageProjects);
        bar =findViewById(R.id.progressBar1);
        bar.setVisibility(View.INVISIBLE);
        code =findViewById(R.id.tx_code);
        bt_3d =findViewById(R.id.bt_3D);

        _3D_objectViewer.PR_code = this;
        came2.PR_code = this;
//
    }

    public void AR(View view) {
        Intent in = new Intent(this, came2.class);
        startActivity(in);

    }

    public void _3d(View view) {
       bar.setVisibility(View.VISIBLE);

        Intent in = new Intent(this, _3D_objectViewer.class);
        startActivity(in);
//        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();
        bar.setVisibility(View.INVISIBLE);

    }


    public void searchResult(View view) {
        if (code.getText().toString().equals("E205"))
        {
            projects.setImageResource(R.drawable.large);
            bt_3d.setVisibility(View.VISIBLE);
        }
        else  if (code.getText().toString().equals("E206"))
        {
            projects.setImageResource(R.drawable.smal);
            bt_3d.setVisibility(View.VISIBLE);

        }
    }
}
