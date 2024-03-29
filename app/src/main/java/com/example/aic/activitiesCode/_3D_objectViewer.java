package com.example.aic.activitiesCode;
import java.io.InputStream;
import java.lang.reflect.Field;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aic.R;
import com.threed.jpct.*;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

public class _3D_objectViewer extends AppCompatActivity {

    private static _3D_objectViewer master = null;
    private GLSurfaceView mGLView;
    private MyRenderer renderer = null;
    private FrameBuffer fb = null;
    private World world = null;
    private RGBColor back = new RGBColor(50, 50, 100);
    private float touchTurn = 0;
    private float touchTurnUp = 0;
    private float xpos = -1;
    private float ypos = -1;

    private Object3D cube = null;
    private int fps = 0;
    private Light sun = null;
    InputStream streamobj;
    InputStream streamMTL ;
   static projects PR_code ;
   String code ;
float scale =0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        code = PR_code.code.getText().toString();
        if (code.equals("E205") ) {
            streamobj = getResources().openRawResource(R.raw.large_building_obj);
            streamMTL = getResources().openRawResource(R.raw.large_building_mtl);
scale = 4f;
        }
        else  if (code.equals("E206") ) {
            streamobj = getResources().openRawResource(R.raw.uderobj3);
            streamMTL = getResources().openRawResource(R.raw.uderobj);
            scale = .5f;

        }

        Logger.log("onCreate");
        if (master != null) {
            copy(master);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__3_d_object_viewer);

        mGLView=  findViewById(R.id.glview);
//        mGLView = new GLSurfaceView(this);


        mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16,
                        EGL10.EGL_NONE };
                EGLConfig[] configs = new EGLConfig[1];
                int[] result = new int[1];
                egl.eglChooseConfig(display, attributes, configs, 1, result);
                return configs[0];

            }
        });

        renderer = new MyRenderer();
        mGLView.setRenderer(renderer);
        GLSurfaceView v = findViewById(R.id.glview);



    }


    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void copy(Object src) {
        try {
            Logger.log("Copying data from master Activity!");
            Field[] fs = src.getClass().getDeclaredFields();
            for (Field f : fs) {
                f.setAccessible(true);
                f.set(this, f.get(src));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean onTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            xpos = me.getX();
            ypos = me.getY();
            return true;
        }
        if (me.getAction() == MotionEvent.ACTION_UP) {
            xpos = -1;
            ypos = -1;
            touchTurn = 0;
            touchTurnUp = 0;
            return true;
        }


        if (me.getAction() == MotionEvent.ACTION_MOVE) {
            float xd = me.getX() - xpos;
            float yd = me.getY() - ypos;
            xpos = me.getX();
            ypos = me.getY();
            Logger.log("xpos------------>>" + xpos);
            touchTurn = xd / -100f;
            touchTurnUp = yd / -100f;
            Logger.log("touchTurn------------>>" + touchTurn);
            return true;
        }



        try {
            Thread.sleep(15);
        } catch (Exception e) {
        }
        return super.onTouchEvent(me);
    }

    public void ARshow(View view) {
        Intent in = new Intent(this, came2.class);
        startActivity(in);
    }

    public void zoomOut(View view) {
        cube.setScale(cube.getScale()/1.2f);
    }

    public void zoomIn(View view) {
        cube.setScale(cube.getScale()*1.2f);

    }

    class MyRenderer implements GLSurfaceView.Renderer {
        private long time = System.currentTimeMillis();
        private boolean stop = false;

        public void stop() {
            stop = true;
        }


        @Override
        public void onSurfaceChanged(GL10 gl, int w, int h)
        {
            if (fb != null) {
                fb.dispose();
            }
            fb = new FrameBuffer(gl, w, h);
            Logger.log(master + "");
            if (master == null) {
                world = new World();
                world.setAmbientLight(20, 20, 20);
                sun = new Light(world);
                sun.setIntensity(250, 250, 250);
                Texture texture = new Texture(BitmapHelper.rescale(
                        BitmapHelper.convert(getResources().getDrawable(


                                R.drawable.ic_launcher_background)), 64, 64));
//                TextureManager.getInstance().addTexture("texture", texture);


                Object3D[] object3DS = Loader.loadOBJ(streamobj,streamMTL, .1f);
                cube = Object3D.mergeAll(object3DS);

                cube.calcTextureWrapSpherical();
//                cube.setTexture("texture");
                cube.strip();
                if (code.equals("E205") ) {
                    cube.rotateZ(12.2f);
                    cube.rotateY(12.5f);
                    cube.rotateX(10.9f);
                }
                else  if (code.equals("E206") ) {
                    cube.rotateZ(10f);
//                    cube.rotateY(12.5f);
//                    cube.rotateX(10.9f);

                }

                cube.setScale((cube.getScale()*scale));
                cube.build();
                Mesh mesh = cube.getMesh();
                world.addObject(cube);


                Camera cam = world.getCamera();
                cam.moveCamera(Camera.CAMERA_MOVEOUT, 10);
                cam.lookAt(cube.getTransformedCenter());
                SimpleVector sv = new SimpleVector();
                sv.set(cube.getTransformedCenter());
                sv.y -= 100;
                sv.z -= 100;
                sun.setPosition(sv);
                MemoryHelper.compact();
                if (master == null) {
                    Logger.log("Saving master Activity!");
                    master = _3D_objectViewer.this;
                }
            }
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        }

        public void onDrawFrame(GL10 gl) {
            try {
                if (!stop) {
                    if (touchTurn != 0) {
                        cube.rotateY(touchTurn);
//                        cube.setScale(cube.getScale() + .05f);
                        touchTurn = 0;
                    }
                    if (touchTurnUp != 0) {
//                        cube.setScale(cube.getScale() - .05f);
//
                        cube.rotateX(touchTurnUp);
                        touchTurnUp = 0;
                    }
//                    if (touchTurnUp != 0) {
//                       cube.scale(cube.getScale()+.01f);
//                        touchTurnUp = 0;
//                    }
//                    if (touch != 0) {
//                        cube.scale(cube.getScale()+.01f);
//                        touchTurnUp = 0;
//                    }
                    fb.clear(back);
                    world.renderScene(fb);
                    world.draw(fb);
                    fb.display();
                    if (System.currentTimeMillis() - time >= 1000) {
                        fps = 0;
                        time = System.currentTimeMillis();
                    }
                    fps++;
                } else {
                    if (fb != null) {
                        fb.dispose();
                        fb = null;
                    }
                }
            } catch (Exception e) {
                Logger.log(e, Logger.MESSAGE);
            }
        }




    }




    @Override
    public void onBackPressed() {
//        super.onBackPressed();
          master = null;

          renderer = null;
         FrameBuffer fb = null;
          world = null;
          back = new RGBColor(50, 50, 100);
          touchTurn = 0;
          touchTurnUp = 0;
        xpos = -1;
        ypos = -1;

      cube = null;
      fps = 0;
       sun = null;

    finish();
    }
}
