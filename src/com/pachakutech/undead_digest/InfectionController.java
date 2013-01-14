package com.pachakutech.undead_digest;

import java.util.Random;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class InfectionController extends IntentService {
	
	public InfectionController() {
		super("InfectionController");
	
	}
	
	@Override

	protected void onHandleIntent(Intent intent) {
		
		if (intent.getBooleanExtra("hit", false)) NonZombieUser(intent);
		
		else PossibleZombieUser(intent);
		
		cancelNotification();
		stopSelf();
	}

	private void PossibleZombieUser(Intent intent) {
		
		Random random = new Random();
		
		if (!useMolotov(intent)){
			if (random.nextInt(10) <= 10 ) { //then user is a zombie
			
			Intent newIntent = new Intent(getBaseContext(), GameSave.class);
		    newIntent.putExtras(intent.getExtras());
		    newIntent.putExtra("zombie", true);
			startService (newIntent);

			cancelAlarm();
			}
		} 
	}
	
	private boolean useMolotov(Intent intent) {
		
		int molotovs = intent.getIntExtra("molotovs", 0);
		if (molotovs == 0) return false;
		else {
	
			//savegame
	   	    Intent saveIntent = new Intent(getBaseContext(), GameSave.class);
		    saveIntent.putExtras(intent.getExtras());
		    saveIntent.putExtra("molotovs", (molotovs -1 ));
		    startService (saveIntent);
		    
		    //start new alarm!!
		    Intent alarmIntent = new Intent(getBaseContext(), SetAlarmFromSaveGame.class);
		    startService(alarmIntent);
		    
		return true; 
		}
	}
	
	private void NonZombieUser(Intent intent) {
		
		//savegame
		Intent newIntent = new Intent(getBaseContext(), GameSave.class);
	    newIntent.putExtras(intent.getExtras());
	    newIntent.putExtra("hit", false);
	    newIntent.putExtra("lastkill", intent.getStringExtra("current_pylon"));
		newIntent.putExtra("zombie", false);
		startService (newIntent);
	    
		GoToMainScreen();
	}

	private void GoToMainScreen() {
		
		//taking this out by having this only if zombie. can call cancel notificton then, too.
		//really shouldn't, as this is all keeping high-priority space from 
		//the onReceive() command in PylonService. It might have a smotth life transistion, too. 
		//Maybe only if the user turns into a zombie? 
		//Go back to Main screen
		Intent newIntent = new Intent(getBaseContext(), Undead_Digest.class);
		newIntent
			.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		getApplication().startActivity(newIntent);
	}

	private void cancelAlarm() {
		
		//sniff out the broadcast with the ID number
		Intent newIntent = new Intent(this, PylonService.class);
		PendingIntent sender = PendingIntent.getBroadcast(this, 952857, newIntent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		
		//cancel the alarm
		alarmManager.cancel(sender);
		
	}

	private void cancelNotification() {
			final int HELLO_ID = 1;
			
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);
			nMgr.cancel(HELLO_ID);
			
	 }

}
