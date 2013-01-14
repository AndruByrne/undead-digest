package com.pachakutech.undead_digest;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ViewWeapons extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
//		final Intent intent = getIntent();
		super.onCreate(savedInstanceState);
        //this might go into the fcn as well
		setContentView(R.layout.activity_view_weapons);
    
        setArray();
	}
	
    private void setArray() {     
		
    	final Intent intent = getIntent();
    	ArrayList<SearchResults> searchResults = GetSearchResults(intent);
        
        final ListView lv1 = (ListView) findViewById(R.id.ListView01);
        lv1.setAdapter(new MyCustomBaseAdapter(this, searchResults));
        
        lv1.setOnItemClickListener(new OnItemClickListener() {
        	
        	public void onItemClick(AdapterView<?> a, View v, int position, 
        			long id) {
        		Object o = lv1.getItemAtPosition(position);
        		SearchResults fullObject = (SearchResults)o;
        		if (fullObject.getName() == "# of Molotovs") {
        			
        			//up tha molotovs
        			int molotovs = intent.getIntExtra("molotovs", 0);
        			intent.putExtra("molotovs", (molotovs + 5));
        			
        			//savegame
        			Intent saveIntent = new Intent(getBaseContext(), GameSave.class);
        			saveIntent.putExtras(intent.getExtras());
        			startService(saveIntent);
        			
        			//startnewAlarm (in order to transfer the molotovs)
        			Intent alarmIntent = new Intent(getBaseContext(), SetAlarmFromSaveGame.class);
        			startService(alarmIntent);
        			
        			//refresh list
        			setArray();
        			
        			//Toast.makeText(ViewWeapons.this, "You have chosen: " + " " + fullObject.getName(), Toast.LENGTH_LONG).show();
        		}
        	}
        });
    }
    
	private ArrayList<SearchResults> GetSearchResults(Intent intent){
    	
		ArrayList<SearchResults> results = new ArrayList<SearchResults>();
    	
    	SearchResults sr1 = new SearchResults();
    	sr1.setName("Ranged Weapon");
    	sr1.setCityState(intent.getStringExtra("weapon"));
    	sr1.setPhone("Shotgun not available");
    	results.add(sr1);
    	
    	sr1 = new SearchResults();
    	sr1.setName("# of Molotovs");
    	sr1.setCityState(String.valueOf(intent.getIntExtra("molotovs", 0)));
    	sr1.setPhone("Click to buy more");
    	results.add(sr1);
    	
    	sr1 = new SearchResults();
    	sr1.setName("Melee weapon");
    	sr1.setCityState("Chainsaw");
    	sr1.setPhone("Switch to Ax");
    	results.add(sr1);
    	
    	return results;
    }

}
