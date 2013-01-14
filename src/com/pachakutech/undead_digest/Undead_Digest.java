package com.pachakutech.undead_digest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Undead_Digest extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	//call to superclass
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);
    	
    	//set alarm
    	//Intent newIntent = new Intent(getBaseContext(),	SetAlarmFromSaveGame.class);
        //startService(newIntent);
        
    }
    
    public void Vaccinate(View view) {
    	
    	//didithappen
    	Toast.makeText(getBaseContext(), "Welcome back to the living!", Toast.LENGTH_SHORT).show();
    	
    	//get object for savegame
    	SharedPreferences gamestate = getSharedPreferences(getString(R.string.prefs_name), 0);
	    SharedPreferences.Editor editor = gamestate.edit();
	    
	    //make user not a zombie
	    editor.putBoolean("zombie", false);
	    
	    //save gameestate
	    editor.commit();
	    
	    Intent newIntent = new Intent(getBaseContext(), SetAlarmFromSaveGame.class);
	    startService(newIntent);
	    
    }
	
    public void ViewWeapons(View view) {
		
		SharedPreferences gamestate = getSharedPreferences(getString(R.string.prefs_name), 0);
        
		//this will be the intent for rest of the game
		Intent intent = new Intent(this, ViewWeapons.class);        
		
		Toast.makeText(getBaseContext(), "loadgame", Toast.LENGTH_SHORT).show();
        
		//define intent extras from saved gamestate
		
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
        
        startActivity(intent);
	}
    
}
