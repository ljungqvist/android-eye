package teaonly.droideye;

import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraView implements SurfaceHolder.Callback{
    public static interface CameraReadyCallback { 
        public void onCameraReady(); 
    }  

    private Camera camera_ = null;
    private SurfaceHolder surfaceHolder_ = null;
    private SurfaceView	  surfaceView_;
    CameraReadyCallback cameraReadyCb_ = null;
 
    private List<Camera.Size> supportedSizes; 
    private Camera.Size procSize_;
    private boolean inProcessing_ = false;
    
    Context context;

    public CameraView(SurfaceView sv, Context context){
        surfaceView_ = sv;
        this.context = context;

        surfaceHolder_ = surfaceView_.getHolder();
        surfaceHolder_.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder_.addCallback(this); 
    }

    public List<Camera.Size> getSupportedPreviewSize() {
        return supportedSizes;
    }

    public int Width() {
        return procSize_.width;
    }

    public int Height() {
        return procSize_.height;
    }

    public void setCameraReadyCallback(CameraReadyCallback cb) {
        cameraReadyCb_ = cb;
    }

    public void StartPreview(){
        if ( camera_ == null)
            return;
        camera_.startPreview();
    }
    
    public void StopPreview(){
        if ( camera_ == null)
            return;
        camera_.stopPreview();
    }

    public void AutoFocus() {
        camera_.autoFocus(afcb);
    }

    public void Release() {
        if ( camera_ != null) {
            camera_.stopPreview();
            camera_.release();
            camera_ = null;
        }
    }
    
    public void setupCamera(int wid, int hei, PreviewCallback cb) {
        procSize_.width = wid;
        procSize_.height = hei;
        
        Camera.Parameters p = camera_.getParameters();        
        p.setPreviewSize(procSize_.width, procSize_.height);
        camera_.setParameters(p);
        
        camera_.setPreviewCallback(cb);
    }

    private void setupCamera() {
    	try {
    		camera_ = Camera.open(0);
    	} catch (Exception e) {
    		Log.e("android-eye", "Camera not free", e);
    		Toast.makeText(context, R.string.no_camera, Toast.LENGTH_LONG).show();
    		return;
    	}
        procSize_ = camera_.new Size(0, 0);
        Camera.Parameters p = camera_.getParameters();        
       
        supportedSizes = p.getSupportedPreviewSizes();
        procSize_ = supportedSizes.get( supportedSizes.size()/2 );
        p.setPreviewSize(procSize_.width, procSize_.height);
        
        camera_.setParameters(p);
        //camera_.setDisplayOrientation(90);
        try {
            camera_.setPreviewDisplay(surfaceHolder_);
        } catch ( Exception ex) {
            ex.printStackTrace(); 
        }
        camera_.startPreview();    
    }  
    
    private Camera.AutoFocusCallback afcb = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
        }
    };

    @Override
    public void surfaceChanged(SurfaceHolder sh, int format, int w, int h){
    }
    
	@Override
    public void surfaceCreated(SurfaceHolder sh){        
        setupCamera();        
        if ( cameraReadyCb_ != null)
            cameraReadyCb_.onCameraReady();
    }
    
	@Override
    public void surfaceDestroyed(SurfaceHolder sh){
        Release();
    }
}
