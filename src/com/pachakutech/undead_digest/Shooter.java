package com.pachakutech.undead_digest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.nvidia.devtech.NvGLES2Activity;

public class Shooter extends NvGLES2Activity 
{
		
		private static List<GameKitListener> listener = new LinkedList<Shooter.GameKitListener>();
		private String sdcardPath;
		
		private static SensorManager mSensorManager;
		private static Sensor mRotationVector;
		private static final float accel_threshold = 0.5f;
		private static final float input_frame = 0.05f;
		private float inputFrameTimer  = input_frame;
		private static final StringBuffer stb = new StringBuffer();
		private static final int KILLSHOOTER = 11;
		private static final int QUAT_ARRAY_SIZE = 8;
		protected static final int SENDQUAT = 4;
		protected static final int SENSORTYPE = 11;
		private float[] smoothedQuaternion = new float[4];
		private float[] gravity = new float[3];
		private float[] linear_acceleration = new float[3];
		protected float[] newQuaternion = new float[4];
		protected int mSensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
		private Timer timer = new Timer();
		private boolean firstReading;
		  
		Handler shooterHandler = new Handler ()
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch(msg.what)
				{
				case 1:
					sensorEvent();
					//these two lines just for debugging, real line should look like above
					shooterHandler.sendEmptyMessageDelayed(1, 200);
					break;
				case 2:
					onBackPressed();
					break;
				}
			}
		};
		
		

	  /** Called when the activity is first created. */
	  @Override
	  public void onCreate(Bundle savedInstanceState) 
	  {
		  //may need to stop an infection alarm here
		  sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
	      
		  initArg = "/"+sdcardPath+"/pachakutech/gk_android.blend";

		  super.onCreate(savedInstanceState);
		  // copy all files from the assets folder to the specified folder (in this case /sdcard/gamekit
		  CopyAssets("pachakutech");
		  firstReading = true;
	
	  }

	  public void onResume()
	  {
		  super.onResume();
		  mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	      mRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
	      if(mSensorManager != null)
	      {
	        	mSensorManager.registerListener(this, mRotationVector, mSensorDelay);
	      }
	  }
	  
	  @Override
	  public void onBackPressed() 
	  {
			super.onBackPressed();

//			System.out.println("AND: BACK");
			
			sendMessage("android", "", "back", "");
			if(shooterHandler != null)
	        	shooterHandler.removeCallbacksAndMessages(SENDQUAT);
		    
			// kill the application after 1sec. 
			shooterHandler.postDelayed(new Runnable()
			{
				public void run() {
					System.exit(0);
				}
			},1000);
	}
	  
	  @Override
	  protected void onStop() 
	  {
	        if(mSensorManager != null)
	        	mSensorManager.unregisterListener(this);
	        super.onStop();
	  }
	  
	  @Override
	  public void onDestroy()
	  {
	        super.onDestroy();
	        //multiData = null;
	        mSensorManager.unregisterListener(this);	
	        systemCleanup();
	  }
	  
	  private void CopyAssets(String folderName) 
	  {
		    AssetManager assetManager = getAssets();
		    String[] files = null; 
		    try {
		        files = assetManager.list("");
		    } catch (IOException e) {
		        Log.e("tag", e.getMessage());
		    }
		    File f = new File(sdcardPath+"/"+folderName);
		    if (f.isFile()) {
		    	throw new RuntimeException("Folder "+sdcardPath+"/"+folderName+" is a file");
		    } else if (!f.isDirectory()){
		    	f.mkdir();
		    }
		    
		    for(String filename : files) {
		        InputStream in = null;
		        OutputStream out = null;
		        try {
		          in = assetManager.open(filename);
		          
		          f = new File(sdcardPath+"/"+folderName+"/" + filename);
		          if (f.exists()){
		        	  Log.i("gamekit", "fileexists!");
//		        	  continue;
		          }
		          
		          out = new FileOutputStream( sdcardPath+"/"+folderName+"/" + filename);
		          copyFile(in, out);
		          in.close();
		          in = null;
		          out.flush();
		          out.close();
		          out = null;
		        } catch(Exception e) {
		            Log.e("tag", e.getMessage());
		        }       
		    }
		}
	  
	  private void copyFile(InputStream in, OutputStream out) throws IOException 
	  {
		    byte[] buffer = new byte[1024];
		    int read;
		    while((read = in.read(buffer)) != -1){
		      out.write(buffer, 0, read);
		    }
		}
	  
	@Override
		public void onWindowFocusChanged(boolean hasFocus) 
		{
			int screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
			int viewHeight = surfaceView.getHeight();
			
			// Use the difference as the cursor offset
			setOffsets(0, viewHeight - screenHeight);
			
			super.onWindowFocusChanged(hasFocus);
		}

		public boolean sensorEvent() 
		{
				
				//if (sensorType==Sensor.TYPE_ACCELEROMETER)
				//if (sensorType==Sensor.TYPE_ROTATION_VECTOR)
					sendSensor(SENSORTYPE, this.smoothedQuaternion[0], this.smoothedQuaternion[1], this.smoothedQuaternion[2], this.smoothedQuaternion[3]);
				
				return true;
		}
		
		public void onSensorChanged(SensorEvent event) 
		{
		//	if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) 
			//	return;

			if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
			{
				SensorManager.getQuaternionFromVector(newQuaternion, event.values);
					//SmoothQuaternion smoothedQuaternion = (SmoothQuaternion) foo;
					
					//Android put Quaternion scalar last, Ogre takes it as the first argument;
					//I solve that in this smoothing operation
					smoothedQuaternion[0] = (smoothedQuaternion[0] * (QUAT_ARRAY_SIZE -1) + newQuaternion[3])/QUAT_ARRAY_SIZE;
					smoothedQuaternion[1] = (smoothedQuaternion[1] * (QUAT_ARRAY_SIZE -1) + newQuaternion[1])/QUAT_ARRAY_SIZE;
					smoothedQuaternion[2] = (smoothedQuaternion[2] * (QUAT_ARRAY_SIZE -1) + newQuaternion[2])/QUAT_ARRAY_SIZE;
					//Also, Ogre handles the inverse of the Android Quaternion Scalar 
					smoothedQuaternion[3] = (smoothedQuaternion[3] * (QUAT_ARRAY_SIZE -1) + (0-newQuaternion[0]))/QUAT_ARRAY_SIZE;
					
					if (firstReading)
					{
						  shooterHandler.sendEmptyMessageDelayed(1, 600);
					  	  shooterHandler.sendEmptyMessageDelayed(2, 30000);
					  	  firstReading = false;
					}
			}
		}
		
		 @Override
		public boolean onKeyUp(int keyCode, KeyEvent event) {
		    if(keyCode == KeyEvent.KEYCODE_MENU) { 
		    	// send menu-message to gkMessageManager. e.g. use message-sensor with "menu" as subject to react on this
		    	sendMessage("android", "", "menu", "");
		    	return true;
		    } else if (keyCode == KeyEvent.KEYCODE_BACK){
		    	// send menu-message to gkMessageManager. e.g. use message-sensor with "back" as subject to react on this
		    	// for now this will still kill the game. but you can just repalce onBackPressed() and call it whenever you
		    	// want. (see GameKitJNI.java for for to handle messages that comes from gamekit)
		    	sendMessage("android", "", "back", "");
		    	onBackPressed();
		    	return true;
		    }
		    return false;
		 }
		
		@Override 
		public native boolean render(int drawWidth, int drawHeight, boolean forceRedraw);

		@Override
		public native void cleanup();

		@Override
		public native boolean init(String initArg);

		@Override
		public native boolean inputEvent(int action, float x, float y, MotionEvent event);

		@Override
		public native boolean keyEvent(int action, int unicodeChar, int keyCode, KeyEvent event);
		
		public native void setOffsets(int x, int y);
		
		// needs to be implemented yet and called at onPause()-Event
		public native void pauseSound();
		// needs to be implemented yet and called at onResume()-Event
		public native void restartSound();
		
		//use data buffer here; need to change int to float buffer variable float buffer 
		public native void sendSensor(int sensorType, float w, float x, float y, float z);
		public native void sendMessage(String from,String to,String topic,String body);
		
		static
		{	
			// Uncomment if you use OpenAL
//			 System.loadLibrary("OpenAL");
			System.loadLibrary("ogrekit");
			addListener(new GameKitJNI());
		}
		
		public static void fireTypeIntMessage(int type,int value)
		{
			for (int i=0;i<listener.size();i++)
			{
				listener.get(i).onMessage(type, value);
			}
		}
		
		public static void fireStringMessage(String from,String to,String subject,String body)
		{
			for (int i=0;i<listener.size();i++)
			{
				listener.get(i).onMessage(from,to,subject,body);
			}
		}
		
		public static void addListener(GameKitListener gkListener)
		{
			listener.add(gkListener);
		}
		
		public static interface GameKitListener
		{
			void onMessage(int type,int value); 
			void onMessage(String from,String to,String subject, String body);
		}
	
		private void stopInfectAlarm() 
		{
	    	//sniff out the broadcast with the ID number
	    	Intent newIntent = new Intent(this, InfectionController.class);
	    	PendingIntent sender = PendingIntent.getService(this, 952855, newIntent, 0);
	    	AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	    			
	    	//cancel the alarm
	    	alarmManager.cancel(sender);
	    			
		}

		/*
		public void ZombieHit(View view) {
	    	//cancelNotification();
	    	Intent intent = getIntent();
	    	Bundle bundle = intent.getExtras();
	    	Intent newIntent = new Intent(getBaseContext(), InfectionController.class);
	    	newIntent.putExtras(bundle);
	    	newIntent.putExtra("hit", true);
	    	
	    	getBaseContext().startService(newIntent);
	    	
	    	Toast.makeText(getBaseContext(), "good_shot", Toast.LENGTH_SHORT).show();
	    	finish();
	    }
*/

	/*  
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
        setContentView(R.layout.activity_shooter);
        Intent intent = getIntent();
        Toast.makeText(getBaseContext(), intent.getStringExtra("weapon"), Toast.LENGTH_SHORT).show();
    	
        //stop InfectAlarm
        stopInfectAlarm();
        
        //start ByteAlarm
    }
    
    
  */  
}
