package com.pachakutech.undead_digest;

import java.util.Calendar;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

//eventually, this should be a registered receiver
public class PylonService extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			
			String fsVenue = "0fs0erwejv3kj33dd";
			//debugging, but will \the persistent notification will appear after the gps 
			//Toast.makeText(context, "ping", Toast.LENGTH_SHORT).show();
			
			intent.putExtra("current_pylon", fsVenue);
			
			//check for gps; if (near an infected venue) then
		    //check if venue is one the user just killed
			NotifyUser(context, intent);
			
			//create another alarm that weapons cancel tht will
			//StartInfectionController(context, intent);
			setInfectAlarm(context, intent);
			
			
			
		}catch (Exception e) {
			Toast.makeText(context, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT).show();
		     e.printStackTrace();
		}
		
	}

	private void setInfectAlarm(Context context, Intent intent) {
		//Toast.makeText(context, "infectset", Toast.LENGTH_SHORT).show();
		
    	//get a Calendar object with the current time
        Calendar cal = Calendar.getInstance();
        
        //wait 5 seconds until starting infection controller
        cal.add(Calendar.SECOND, 10);
    	
        Bundle bundle = intent.getExtras();
	Intent newIntent = new Intent(context, InfectionController.class);
	newIntent.putExtras(bundle);
	newIntent.putExtra("hit", false);
//		context.startService(newIntent);
    	
        //create pending intent
    	PendingIntent sender = PendingIntent.getService(context, 952855, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	    	
               	//get the alarm manager service
    	AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    	am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
		
    	
    	
	}	
	
	private void NotifyUser(Context context, Intent intent) {
		
		NotificationManager mgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = "Hellp";
		long when = System.currentTimeMillis();
		
		//make notification details
		Notification noti = new Notification(icon, tickerText, when);
		Intent notificationIntent = new Intent(context, Shooter.class); 
		CharSequence contentTitle = "UnDead Digest";
		CharSequence contentText = "Use Pistol";
		
		//need to add extras!!
		Bundle bundle = intent.getExtras();
		
		notificationIntent.putExtras(bundle);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		noti.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		long[] vibrate = {0, 100, 100, 100, 100, 100, 100, 100, 350, 100, 100, 100, 100, 100, 100, 100};
		noti.vibrate = vibrate;
//		noti.defaults = Notification.DEFAULT_VIBRATE; //need permissions
		//to have countdown, call NotificationManager::notify with the same ID
		
		final int HELLO_ID = 1;
		
		mgr.notify(HELLO_ID, noti);
	}

}
