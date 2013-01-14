package com.pachakutech.undead_digest;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

public class GameSave extends IntentService {
	
	public GameSave() {
		super("GameSave");
	}
	
	protected void onHandleIntent(Intent intent) {
		
		SharedPreferences gamestate = getSharedPreferences(getString(R.string.prefs_name), 0);
	    SharedPreferences.Editor editor = gamestate.edit();
	    
	    //save game variables
	  //  editor.putBoolean("zombie", true);
	    editor.putBoolean("zombie", intent.getBooleanExtra("zombie", false));
	    editor.putBoolean("pistol", intent.getBooleanExtra("pistol", true));
	    editor.putBoolean("ax", intent.getBooleanExtra("ax", false));
	    editor.putBoolean("chainsaw", intent.getBooleanExtra("chainsaw", false));
	    editor.putBoolean("messenger_bag", intent.getBooleanExtra("messenger_bag", false));
	    editor.putBoolean("crossbow", intent.getBooleanExtra("crossbow", false));
	    //editor.putBoolean("newgame", false);
	    editor.putInt("molotovs", intent.getIntExtra("molotovs", 0));
	    editor.putInt("garlic_cloves", intent.getIntExtra("garlic_cloves", 0));
	    editor.putInt("bottles", intent.getIntExtra("bottles", 0));
	    editor.putString("weapon", intent.getStringExtra("weapon"));
	    editor.putString("lastkill", intent.getStringExtra("lastkill"));
	    editor.putBoolean("hit", false);
	    
		editor.commit();
		stopSelf();
	}

}
