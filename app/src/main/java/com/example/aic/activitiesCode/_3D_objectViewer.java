package com.example.aic.activitiesCode;
import java.io.InputStream;
import java.lang.reflect.Field;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aic.R;
import com.threed.jpct.*;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

public class _3D_objectViewer extends AppCompatActivity {

    // يتم استخدام كائن HelloWorld للتعامل مع أساليب onPause و onResume للنشاط
    private static _3D_objectViewer master = null;
    // كائن GLSurfaceView
    private GLSurfaceView mGLView;
    // الطبقة MyRenderer الكائن
    private MyRenderer renderer = null;
    // عندما تعرض JPCT الخلفية ، توفر الفئة FrameBuffer مخزنًا مؤقتًا ، وتكون نتيجتها في الأساس صورة يمكن عرضها أو تعديلها ويمكن معالجتها أيضًا.
    private FrameBuffer fb = null;
    // الطبقة العالمية هي الفئة الأكثر أهمية في JPCT ، إنها أشياء "غراء" مثل الغراء. الكائنات والضوء الذي يحتويه يحددان مشهد JPCT
    private World world = null;
	 // تشبه فئة Color في java.awt. *
    private RGBColor back = new RGBColor(50, 50, 100);
    private float touchTurn = 0;
    private float touchTurnUp = 0;
    private float xpos = -1;
    private float ypos = -1;
    /*
     * فئة Object3D هي كائن ثلاثي الأبعاد ، لا تفكر في أنه يشبه java.lang.Object.
     * يتم إضافة كائن Object3D كمثيل لكائن World الذي يتم تقديمه. يضيف Object3D مثيل واحد في كل مرة في العالم
     * قد تكون مرتبطة كأطفال / الآباء لإنشاء نظام بينهم.
     * يمكن بالطبع تطبيق العارضات في القواعد أعلاه. غالبًا ما لا يتم إضافتها إلى مثيل عالمي ، ولكنها مرتبطة بأشياء أخرى (نماذج بشرية أو غير بشرية). بعض الطرق
     * يجب إضافة مثيل إلى مثيل عالمي في هذه الفئة (يمكن تحقيق ذلك باستخدام طريقة World.addObject ()).
     */
    private Object3D cube = null;
    // الإطارات في الثانية الواحدة
    private int fps = 0;
    // فئة الإضاءة
    private Light sun = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);


        // فئة المسجل: فئة السجل الشائعة في jPCT لطباعة وتخزين الرسائل والأخطاء والتحذيرات. ستتم إضافة كل رسالة تم إنشاؤها بواسطة JPCT إلى قائمة انتظار هذه الفئة
        Logger.log("onCreate");
		 // إذا لم يكن كائن هذه الفئة NULL ، فسيتم تحميل الفئة من جميع السمات في Object
        if (master != null) {
            copy(master);
        }
        super.onCreate(savedInstanceState);
        // إنشاء مثيل لـ GLSurfaceView
        mGLView = new GLSurfaceView(this);


        // استخدم تطبيقك الخاص بـ EGLConfigChooser ، يجب أن يكون التنفيذ قبل setRenderer (العارض)
//        إذا لم يتم استدعاء طريقة setLLConfigChooser ، بشكل افتراضي ، فإن العرض سيختار EGLConfig الذي يكون على الأقل 16 بت مخزن مؤقت عميق متوافق مع android.view.Surface الحالي.
        mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                // Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
                // back to Pixelflinger on some device (read: Samsung I7500)
                int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16,
                        EGL10.EGL_NONE };
                EGLConfig[] configs = new EGLConfig[1];
                int[] result = new int[1];
                egl.eglChooseConfig(display, attributes, configs, 1, result);
                return configs[0];
            }
        });
//		 / إنشاء مثيل MyRenderer
                renderer = new MyRenderer();
        // عيّن عارض العرض ، وابدأ سلسلة الرسائل لاستدعاء العرض ، وذلك لبدء العرض
        mGLView.setRenderer(renderer);
//		 / / تعيين وجهة نظر واضحة
        setContentView(mGLView);

    }

//	 / / تجاوز onPause ()
    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

//	 / / تجاوز onResume ()
    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

//	 / / تجاوز onStop ()
    @Override
    protected void onStop() {
        super.onStop();
    }

    private void copy(Object src) {
        try {
            // سجل الطباعة
            Logger.log("Copying data from master Activity!");
//			 / إرجاع صفيف يحتوي على الكائنات المحفوظة في جميع حقول هذه الفئة
            Field[] fs = src.getClass().getDeclaredFields();
            // اجتياز مجموعة fs
            for (Field f : fs) {
//				 / / حاول تعيين قيمة علامة إمكانية الوصول. سيؤدي تعيين العلامة إلى "خطأ" إلى تمكين التحقق من الوصول ، وتعيينها على "صحيح" سيعطلها.
                        f.setAccessible(true);
                // قم بتحميل جميع القيم التي تم جلبها إلى الفئة الحالية
                f.set(this, f.get(src));
            }
        } catch (Exception e) {
            // رمي استثناء وقت التشغيل
            throw new RuntimeException(e);
        }
    }

    public boolean onTouchEvent(MotionEvent me) {
        // اضغط المفتاح
        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            // احفظ موضع x الأولي الأولي المضغوط في xpos ، ypos
            xpos = me.getX();
            ypos = me.getY();
            return true;
        }
        // نهاية المفتاح
        if (me.getAction() == MotionEvent.ACTION_UP) {
            // اضبط x و y وزاوية الدوران على القيم الأولية
            xpos = -1;
            ypos = -1;
            touchTurn = 0;
            touchTurnUp = 0;
            return true;
        }
        if (me.getAction() == MotionEvent.ACTION_MOVE) {
//			 / / احسب x ، y موقع الإزاحة وزاوية الدوران على المحور x ، y
            float xd = me.getX() - xpos;
            float yd = me.getY() - ypos;
            // Logger.log("me.getX() - xpos----------->>"
            // + (me.getX() - xpos));
            xpos = me.getX();
            ypos = me.getY();
            Logger.log("xpos------------>>" + xpos);
            // Logger.log("ypos------------>>" + ypos);
            // خذ المحور س كمثال ، يتم سحب الماوس من اليسار إلى اليمين كإيجابي ، وسحب من اليمين إلى اليسار بالسالب
            touchTurn = xd / -100f;
            touchTurnUp = yd / -100f;
            Logger.log("touchTurn------------>>" + touchTurn);
            // Logger.log("touchTurnUp------------>>" + touchTurnUp);
            return true;
        }

//        if (me.getAction() == MotionEvent.AC) {
//            // اضبط x و y وزاوية الدوران على القيم الأولية
//
//            xpos = -1;
//            ypos = -1;
//            touchTurn = 0;
//            touchTurnUp = 0;
//            return true;
//        }
        // النوم كل ميلي ثانية واحدة
        try {
            Thread.sleep(15);
        } catch (Exception e) {
            // No need for this...
        }
        return super.onTouchEvent(me);
    }

    // الطبقة MyRenderer تنفذ واجهة GLSurfaceView.Renderer
    class MyRenderer implements GLSurfaceView.Renderer {
        // مللي ثانية من النظام الحالي
        private long time = System.currentTimeMillis();
        // سواء للتوقف
        private boolean stop = false;

        // توقف
        public void stop() {
            stop = true;
        }

        // عندما تتغير الشاشة
        public void onSurfaceChanged(GL10 gl, int w, int h) {
            // إذا لم يكن FrameBuffer NULL ، فافرج عن الموارد التي تشغلها fb
            if (fb != null) {
                fb.dispose();
            }
//			 / / إنشاء FrameBuffer مع عرض ث والارتفاع ح
                    fb = new FrameBuffer(gl, w, h);
            Logger.log(master + "");
//			 / / إذا كان السيد فارغ
            if (master == null) {
                // إنشاء كائن العالم
                world = new World();
                // يتم ضبط شدة الضوء المحيط. سيؤدي تعيين هذه القيمة إلى القيمة السلبية إلى تعتيم المشهد بأكمله ، بينما تؤدي الإضاءة الإيجابية إلى إضاءة كل شيء.
                world.setAmbientLight(20, 20, 20);
//				 / / قم بإنشاء مصدر ضوء جديد في العالم
                        sun = new Light(world);
                // ضبط شدة الضوء
                sun.setIntensity(250, 250, 250);
//				 / / خلق نسيج
                // منشئ نسيج (صورة نقطية)
                // static Bitmap rescale(Bitmap bitmap, int width, int height)
                // static Bitmap convert(Drawable drawable)
                Texture texture = new Texture(BitmapHelper.rescale(
                        BitmapHelper.convert(getResources().getDrawable(


                                R.drawable.ic_launcher_background)), 64, 64));
                // TextureManager.getInstance () يحصل على كائن Texturemanager
                // addTexture ("texture"، texture) يضيف نسيجًا
                TextureManager.getInstance().addTexture("texture", texture);
                // كائن Object3D بدأ :-)
//                يوفر / / Primitives بعض الكائنات ثلاثية الأبعاد الأساسية.إذا قمت بإنشاء بعض الكائنات للاختبار أو
                        // سيكون من الحكمة استخدام هذه الفئات لأغراض أخرى ، لأنها سريعة وبسيطة ولا تتطلب التحميل والتحرير.
                        // استدعاء get3Cube (مقياس التعويم) ثابت ثابت Object3D العامة: زاوية
//                        / / إرجاع مكعب

                InputStream streamobj = getResources().openRawResource(R.raw.uderobj3);
                InputStream streamMTL = getResources().openRawResource(R.raw.uderobj);

                Object3D[] object3DS = Loader.loadOBJ(streamobj,streamMTL, 0.5f);
                cube = Object3D.mergeAll(object3DS);

//                try {
//                    cube = Object3D.mergeAll(Loader.load3DS(getResources().getAssets().open("mesh_cat"), 1));
//                } catch (IOException e) {
//
//                    e.printStackTrace();
//                }
                // الملمس جميع أسطح الكائن مع الملمس
                cube.calcTextureWrapSpherical();
                // نسيج الكائن
                cube.setTexture("texture");
                // ما لم ترغب في تعديله باستخدام PolygonManager بعد ذلك ، فافتح الذاكرة التي لم تعد بحاجة إلى بيانات
                cube.strip();
                cube.rotateZ(10);
//				 / / تهيئة بعض الكائنات الأساسية هو تقريبا كل العملية المطلوبة لمزيد من المعالجة.
                // إذا كان الكائن "جاهزًا للتقديم" (إعدادات التحميل ، تخصيص الملمس ، الموضع ، إعدادات وضع العرض ،
//				 / / الرسوم المتحركة ومهمة تحكم قمة الرأس) ، ثم يجب أن يسمى build () ،
                cube.build();
                Mesh mesh = cube.getMesh();
                // إضافة كائن Object3D إلى مجموعة العالم
                world.addObject(cube);

                // تمثل الكاميرا موضع واتجاه الكاميرا / العارض في المشهد الحالي ، كما تحتوي على معلومات حول مجال العرض الحالي
                // يجب أن تتذكر أن مصفوفة دوران الكاميرا هي في الواقع مصفوفة دوران للكائنات المطبقة في العالم.
                // هذا مهم للغاية ، عند تحديد زاوية دوران الكاميرا ، تدور الكاميرا (الافتراضية) حول ث وتدور حول
//                سيكون لها نفس التأثير ، لذلك إذا أخذنا بعين الاعتبار زاوية الدوران ، عندما يكون العالم حول الكاميرا ، فإن منظور الكاميرا ثابت. إذا كنت لا تحب ذلك
                // هذه العادة ، يمكنك استخدام طريقة rotateCamera ()
                Camera cam = world.getCamera();
                // حرك الكاميرا للخلف بسرعة 50 (نسبة إلى الاتجاه الحالي)
                cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
                // cub.getTransformedCenter () بإرجاع مركز الكائن
                // cam.lookAt(SimpleVector lookAt))
//				 / قم بتدوير هذه الكاميرا بحيث تنظر إلى موضع مساحة العالم المحدد
                cam.lookAt(cube.getTransformedCenter());
                // SimpleVector هي فئة أساسية تمثل متجهات ثلاثية الأبعاد ، وكل متجه تقريبًا هو
//                يتم إنشاء // من SimpleVector أو على الأقل متغير SimpleVector (أحيانًا بسبب
                // لبعض الأسباب مثل الأداء (float x ، float y ، float z) ، إلخ).
                SimpleVector sv = new SimpleVector();
                // عيّن قيم x و y و z الخاصة بـ SimpleVector الحالية على قيمة SimpleVector المحددة (cube.getTransformedCenter ())
                sv.set(cube.getTransformedCenter());
                // اطرح 100 في اتجاه Y
                sv.y -= 100;
                // اطرح 100 في اتجاه Z
                sv.z -= 100;
                // قم بتعيين موضع مصدر الضوء
                sun.setPosition(sv);
                // فرض GC والانتهاء لمحاولة إطلاق بعض الذاكرة وكتابة الذاكرة الحالية إلى السجل ،
                // هذا يتجنب التناقض في الرسوم المتحركة ، ومع ذلك ، فهو يقلل فقط من فرصة حدوث ذلك
                MemoryHelper.compact();
                // إذا كان السيد فارغًا ، فاستخدم التسجيل وعيّن الرئيسي على HelloWorld نفسه
                if (master == null) {
                    Logger.log("Saving master Activity!");
                    master = _3D_objectViewer.this;
                }
            }
        }

        // onSurfaceCreated (GL10 gl، EGLConfig config) ليتم تنفيذها
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        }

        // رسم على الشاشة الحالية: -D
        public void onDrawFrame(GL10 gl) {
            try {
                // إذا توقف صحيح
                if (!stop) {
                    // إذا لم يكن touchTurn 0 ، فقم بتدوير زاوية touchTure إلى المحور Y
                    if (touchTurn != 0) {
                        // يتم إعطاء دوران الكائن الدوار حول Y بواسطة زاوية المحور W للمصفوفة المحددة (تكون الراديان موجبة في اتجاه عقارب الساعة) ويتم تطبيقها على التقديم التالي للكائن.
                        cube.rotateY(touchTurn);
                        // set touchTurn إلى 0
                        touchTurn = 0;
                    }
                    if (touchTurnUp != 0) {
                        // يتم إعطاء دوران الكائن الدوار حول x بواسطة مصفوفة محور زاوية معينة (راديان ، عكس اتجاه عقارب الساعة) ، مطبقة على العرض التالي للكائن.
                        cube.rotateX(touchTurnUp);
                        // set touchTureUp إلى 0
                        touchTurnUp = 0;
                    }
                    // Clear FrameBuffer بلون معين (للخلف)
                    fb.clear(back);
//					 / / تحويل والضوء جميع المضلعات
                    world.renderScene(fb);
                    // التعادل
                    world.draw(fb);
                    // تقديم عرض الصورة
                    fb.display();
                    // سجل FPS
                    if (System.currentTimeMillis() - time >= 1000) {
                        // Logger.log(fps + "fps");
                        fps = 0;
                        time = System.currentTimeMillis();
                    }
                    fps++;
                    // إذا كانت الإيقاف خاطئة ، فافتح إطار FrameBuffer
                } else {
                    if (fb != null) {
                        fb.dispose();
                        fb = null;
                    }
                }
                // عند حدوث استثناء ، اطبع معلومات الاستثناء
            } catch (Exception e) {
                Logger.log(e, Logger.MESSAGE);
            }
        }
    }
}
