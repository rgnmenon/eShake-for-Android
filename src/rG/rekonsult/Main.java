package rG.rekonsult;
/*
 * Author - Remesh Govind N M
 *  Please maintian copy right notice of the developers
 *  Release under Apache License 
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import android.view.LayoutInflater;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;


public class Main extends Activity implements SensorEventListener,OnLongClickListener {
	
	private SensorManager sensorManager;
	private List<Sensor> sensors;
	private Sensor sensor;
	private long lastUpdate = -1;
	private long currentTime = -1;
	private int inCall =0;
	
	private float last_x, last_y, last_z;
	private float current_x, current_y, current_z, currenForce;
	private static final int FORCE_THRESHOLD = 900;
	private final int DATA_X = SensorManager.DATA_X;
	private final int DATA_Y = SensorManager.DATA_Y;
	private final int DATA_Z = SensorManager.DATA_Z;
	private ImageButton btnSave;
	private EditText edit1;
	SharedPreferences sp;
	private String SHARED_PARAM_NAME = "ESHAKE";
    public boolean onLongClick(View v) {
	      // do something when the button is clicked
    	java.util.Date d = new java.util.Date();
    	String year = " " + d.getYear();
    	Toast.makeText(this, "(c) 2009-"+ year +" Remesh Govind" , 20).show();
    	return true;
	    }
	
	public void onCreate(Bundle savedInstanceState) {
		try{
			
			this.setContentView(R.layout.main);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	Date date1 = new Date();
        	Date date2 = sdf.parse("2012-12-10");
        	// PUT date Limits for new version check here
			boolean comp = date1.after(date2);
			sp = getSharedPreferences(SHARED_PARAM_NAME, 0);
			btnSave = (ImageButton) findViewById(R.id.btnSave);
			super.onCreate(savedInstanceState);
			edit1 = (EditText) findViewById(R.id.number);
			btnSave.setOnClickListener(new OnClickListener() {
				
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					SharedPreferences.Editor ed =  sp.edit();
					 ed.putString("Number",  edit1.getText().toString()  );
					ed.commit();
				}
			});
		

		  //RelativeLayout layout = (RelativeLayout) findViewById(R.id.gorgeous);
		  	setContentView(R.layout.main);
		  	getWindow().getDecorView().findViewById(android.R.id.content).getRootView().setOnLongClickListener(this);
			SensorManager sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			this.sensorManager =  (SensorManager)getSystemService(SENSOR_SERVICE);
			//this.subscriber = subscriber;
			this.sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
			//sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
			if (sensors.size() > 0) {
			sensor = sensors.get(0);
			}
		}catch(Exception e){
			System.out.print("###");
			e.printStackTrace();
		}
		}
	 protected void onResume() {
         super.onResume();
         if(sensorManager!=null)
         sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
     }

	public void onStart () {
		super.onStart();
		if (sensor!=null)  {
			sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
		}
	}
	
	public void onStop () {
		super.onStop();
		sensorManager.unregisterListener(this);
	}
	
	public void onAccuracyChanged(Sensor s, int valu) {
		
		
	}
	protected Object getITelephony(final Context context)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, InvocationTargetException,
			IllegalAccessException
		{
			final TelephonyManager tm = (TelephonyManager)
					context.getSystemService(Context.TELEPHONY_SERVICE);
			final Method getITelephony
					= tm.getClass().getDeclaredMethod("getITelephony");
			if (!getITelephony.isAccessible()) {
				getITelephony.setAccessible(true);
			}
			return getITelephony.invoke(tm);
		}
	
	public void onSensorChanged(SensorEvent event) {
		
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER || event.values.length < 2)
		      return;
		
		currentTime = System.currentTimeMillis();
		
		if ((currentTime - lastUpdate) > 100) {
			long diffTime = (currentTime - lastUpdate);
			lastUpdate = currentTime;
			
			current_x = event.values[DATA_X];
			current_y = event.values[DATA_Y];
			current_z = event.values[DATA_Z];
			
			currenForce = Math.abs(current_x+current_y+current_z - last_x - last_y - last_z) / diffTime * 10000;
			
			if (currenForce > FORCE_THRESHOLD && inCall ==0) {
			
				
				if (sp.contains("number") && sp.getString("number","").length() > 5 ){
				
				// Device has been shaken now go on and do something
				// you could now inform the parent activity ...
				String phoneNumber = sp.getString("Number", "1800");
				android.net.Uri uri = android.net.Uri.fromParts("tel", phoneNumber, null);
				
				
				try{
				inCall =1;	
				android.content.Intent callIntent = new android.content.Intent(android.content.Intent.ACTION_CALL, uri);
				callIntent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(callIntent);
				}catch(Exception e){
					inCall =0;
					e.printStackTrace();
				}

				}else{
					Toast.makeText(this, "Please Configure number first", Toast.LENGTH_LONG);
				}
				
				
					
			}else{
				
				 try{ 	
					 Toast.makeText(getBaseContext(), "Trying to End Call", Toast.LENGTH_LONG);
					 // Send Key For End Call
					 new KeyEvent(KeyEvent.ACTION_DOWN,
						   KeyEvent.KEYCODE_ENDCALL );
				  
					 inCall =0;
				 }catch(Exception e){
					 
					 e.printStackTrace();
				 }
				
			}
			
			last_x = current_x;
			last_y = current_y;
			last_z = current_z;

		}
	}
	


}
