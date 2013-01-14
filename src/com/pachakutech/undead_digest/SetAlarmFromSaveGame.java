package com.pachakutech.undead_digest;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;

public class SetAlarmFromSaveGame extends IntentService {
    int SECONDS_TO_SCAN = 5;
	
	public SetAlarmFromSaveGame() {
    	super("SetAlarmFromSaveGame");
    }
    
    protected void onHandleIntent(Intent throwaway) {
    	setAlarm(loadGame());
    	stopSelf();
    }
    
    private Intent loadGame() {
		
		SharedPreferences gamestate = getSharedPreferences(getString(R.string.prefs_name), 0);
        
		//this will be the intent for rest of the game
		Intent intent = new Intent(this, PylonService.class);        
		
		//Toast.makeText(getBaseContext(), "loadgame", Toast.LENGTH_SHORT).show();
        
		//define intent extras from saved gamestate
		
		intent.putExtra("newgame", gamestate.getBoolean("newgame", false));
		intent.putExtra("zombie", gamestate.getBoolean("zombie", false));
		intent.putExtra("pistol", gamestate.getBoolean("pistol", true));
		intent.putExtra("ax", gamestate.getBoolean("ax", false));
		intent.putExtra("messenger_bag", gamestate.getBoolean("messenger_bag", false));
		intent.putExtra("chainsaw", gamestate.getBoolean("chainsaw", false));
		intent.putExtra("crossbow", gamestate.getBoolean("crossbow", false));
		intent.putExtra("molotovs", gamestate.getInt("molotovs", 0));
		intent.putExtra("garlic_cloves", gamestate.getInt("garlic_cloves", 0));
		intent.putExtra("bottles", gamestate.getInt("bottles", 0));
		intent.putExtra("lastkill", gamestate.getString("lastkill", "0000000000000"));
		intent.putExtra("weapon", gamestate.getString("weapon", "pistol"));
		intent.putExtra("hit", false);
        
        return intent;
	}

    private void setAlarm(Intent intent) {
	
    	boolean zombie = intent.getBooleanExtra("zombie", true);
		if (!zombie){
			
		
    	//get a Calendar object with the current time
		Calendar cal = Calendar.getInstance();
    
		//wait 15 seconds until first instance
		cal.add(Calendar.SECOND, SECONDS_TO_SCAN);
	
		//create pending intent
		PendingIntent sender = PendingIntent.getBroadcast(getBaseContext(), 952857, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	    	
        //get the alarm manager service
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
//		am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), TimeUnit.SECONDS.toMillis(15), sender);
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
		}
		stopSelf();
	
    }

}
