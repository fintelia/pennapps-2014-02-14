package com.jumpshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG;
import org.opencv.video.BackgroundSubtractorMOG2;

import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class CameraActivity extends Activity implements CvCameraViewListener2, OnTouchListener {
    private static final String TAG = "OCVSample::Activity";

    private CameraView mOpenCvCameraView;
    private List<Size> mResolutionList;
    private MenuItem[] mEffectMenuItems;
    private SubMenu mColorEffectsMenu;
    private MenuItem[] mResolutionMenuItems;
    private SubMenu mResolutionMenu;
    private Mat frame;
   // private Mat smallFrame;
    private Mat fore;
    private BackgroundSubtractorMOG bg;
    private List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    
    private int coolDown = 0;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    
                    bg = new BackgroundSubtractorMOG(64, 3, 0.2);
                    frame = new Mat();
                    //smallFrame = new Mat(480,320, org.opencv.core.CvType.CV_8UC3);
                    fore = new Mat();
                    
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(CameraActivity.this);
                    
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public CameraActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera);

        mOpenCvCameraView = (CameraView) findViewById(R.id.camera_activity_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
        
       
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }
    int numsnaps = 0;
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	contours.clear();
    	Imgproc.cvtColor(inputFrame.rgba(), frame, Imgproc.COLOR_RGBA2RGB);
    	
    	Mat smallFrame = new Mat();
    	Imgproc.resize(frame, smallFrame, new org.opencv.core.Size(), 0.25,0.25, Imgproc.INTER_AREA);
    	
    	
    	bg.apply(smallFrame, fore, 0.25);
    	Imgproc.erode(fore, fore, new Mat());
    	Imgproc.dilate(fore, fore, new Mat());
  
    	Mat left = fore.colRange(0, fore.cols()/3);
    	Mat center = fore.colRange(fore.cols()/3, fore.cols() *2/3);
    	Mat right = fore.colRange(fore.cols()*2/3, fore.cols());
    	int leftCount = Core.countNonZero(left);
    	int centerCount = Core.countNonZero(center);
    	int rightCount = Core.countNonZero(right);
    	//Imgproc.findContours(fore, contours, new Mat() , Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
    	//Imgproc.drawContours(frame, contours, -1, new Scalar(255,0,0));
    	//Imgproc.cvtColor(fore,fore,Imgproc.COLOR_GRAY2BGRA);
    	
    	Imgproc.cvtColor(fore, fore, Imgproc.COLOR_GRAY2RGB);
    	
    	//Core.putText(frame, "" + leftCount, new Point(10,40), Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0,0,0));    	
    	//Core.putText(frame, "" + centerCount, new Point(10,120), Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0,0,0));
    	//Core.putText(frame, "" + rightCount, new Point(10,200), Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0,0,0));
    	if(coolDown > 0){
       		Core.rectangle(frame, new Point(0, 0), new Point(frame.cols(), frame.rows()), new Scalar(0,255, 0), (int)(32.0 * coolDown / 16));
       		coolDown--;
    	}
    	else if(leftCount + rightCount > 0.35*fore.cols()*fore.rows() * 2/ 3){
       		Core.rectangle(frame, new Point(0, 0), new Point(frame.cols(), frame.rows()), new Scalar(255,0, 0), 32);  		
    	}
    	else if(centerCount > 1.15*leftCount && centerCount > 1.15*rightCount && centerCount > 0.2*fore.cols()*fore.rows() / 3){
            coolDown = 15;
    		Core.rectangle(frame, new Point(0, 0), new Point(frame.cols(), frame.rows()), new Scalar(0, 255, 0), 32);  		
      		 
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String currentDateandTime = sdf.format(new Date());
            String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() +
                                   "/jumpshot_" + currentDateandTime + ".jpg";
            
            try{
            mOpenCvCameraView.takePicture(fileName);
            numsnaps++;
            }catch(Exception e){}
            
            //Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show(); 
    	}
      	Core.putText(frame, "" + leftCount, new Point(10,40), Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0,0,0));    	
    	  
    		
    	//Imgproc.resize(f, fore, frame.size());
        return frame;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        List<String> effects = mOpenCvCameraView.getEffectList();

        if (effects == null) {
            Log.e(TAG, "Color effects are not supported by device!");
            return true;
        }

        mColorEffectsMenu = menu.addSubMenu("Color Effect");
        mEffectMenuItems = new MenuItem[effects.size()];

        int idx = 0;
        ListIterator<String> effectItr = effects.listIterator();
        while(effectItr.hasNext()) {
           String element = effectItr.next();
           mEffectMenuItems[idx] = mColorEffectsMenu.add(1, idx, Menu.NONE, element);
           idx++;
        }

        mResolutionMenu = menu.addSubMenu("Resolution");
        mResolutionList = mOpenCvCameraView.getResolutionList();
        mResolutionMenuItems = new MenuItem[mResolutionList.size()];

        ListIterator<Size> resolutionItr = mResolutionList.listIterator();
        idx = 0;
        while(resolutionItr.hasNext()) {
            Size element = resolutionItr.next();
            mResolutionMenuItems[idx] = mResolutionMenu.add(2, idx, Menu.NONE,
                    Integer.valueOf(element.width).toString() + "x" + Integer.valueOf(element.height).toString());
            idx++;
         }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item.getGroupId() == 1)
        {
            mOpenCvCameraView.setEffect((String) item.getTitle());
            Toast.makeText(this, mOpenCvCameraView.getEffect(), Toast.LENGTH_SHORT).show();
        }
        else if (item.getGroupId() == 2)
        {
            int id = item.getItemId();
            Size resolution = mResolutionList.get(id);
            mOpenCvCameraView.setResolution(resolution);
            resolution = mOpenCvCameraView.getResolution();
            String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
            Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    
    
    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG,"onTouch event");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String fileName = Environment.getExternalStorageDirectory().getPath() +
                               "/jumpshot_" + currentDateandTime + ".jpg";
        mOpenCvCameraView.takePicture(fileName);
        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
        return false;
    }
}