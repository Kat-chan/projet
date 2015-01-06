package com.example.androidafro;

import java.util.ArrayList;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Activity - Affichage et ListView
 */
public class PaviusActivity extends ActionBarActivity {
	RenseignementReceiver receiver;
	public static final int XACTIVITY_INTENT = 1;

  	// Gestion des elements (item) du contenu.
  	ArrayAdapter<String> adapter;
  	ArrayList<String> arraylist = new ArrayList<String>();

  	/**
  	 * Classe Broadcast Receiver.
  	 */
  	public class RenseignementReceiver extends BroadcastReceiver {
  		@Override
    	public void onReceive(Context context, Intent intent) 
    	{
  	      // Ecriture des donnees de la BDD vers le layout.
  			loadFromBDD();
    	}
  	}

    @Override
  	public void onCreate(Bundle icicle) 
  	{
  		super.onCreate(icicle);

      // Configuration affichage (layout xml)
  		configurationLayout();

      // Lance la recherche du service Internet
	  	launchHTTPToDB();

	  	// Ecriture des donnees de la BDD vers le layout.
	  	loadFromBDD();
  	}

    @Override
    public void onResume() 
    {
    	// Gestion des nouvelles donnees en cours de traitement. (???)
    	//notificationManager.cancel(XService.NOTIFICATION_ID);

    	receiver = new RenseignementReceiver();
    	registerReceiver(receiver, new IntentFilter(PaviusService.NOUVEAU_RENSEIGNEMENT_INSERE));

      // Ecriture des donnees de la BDD vers le layout.
    	loadFromBDD();

    	super.onResume();
    }

    /**
    *  Creation du menu 'overflow'.
    */
    public boolean onCreateOptionsMenu(Menu menu) 
    {
      super.onCreateOptionsMenu(menu);
      // Insertion du menu overflow
    getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    /**
    *  Gestion du menu 'overflow'.
    */
    public boolean onOptionsItemSelected(MenuItem item) 
    {
      super.onOptionsItemSelected(item);

      int itemId = item.getItemId();
      if(itemId == R.id.action_settings) {        
          Intent intent = new Intent(this, PaviusActivity.class);
          startActivityForResult(intent, XACTIVITY_INTENT);
          return true;
      }

      return false;
    }

    @Override
    public void onPause() 
    {
    	unregisterReceiver(receiver);
    	super.onPause();
    }

	/**
	 * Lance la recherche du service Internet
   * Recupere les donnees du serveur.
	 */
  	private void launchHTTPToDB() 
  	{
  		startService(new Intent(this, PaviusService.class));
  	}

  	/**
  	 * Ajout d'un element.
  	 */
	public void ajoutElement(String _element) 
	{
		arraylist.add(_element);
		adapter.notifyDataSetChanged();
	}

	/**
   * Ecriture des donnees de la BDD vers le layout.
	 */
	public void loadFromBDD() 
	{
		arraylist.clear();

		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(PaviusProvider.CONTENT_RENSEIGNEMENT_URI, null, null, null, null);

		if(cursor.moveToFirst()) {
			do {
				String nom = 			cursor.getString(PaviusProvider.NOM_COLUMN);
				String type = 			cursor.getString(PaviusProvider.TYPE_COLUMN);
				String pays = 			cursor.getString(PaviusProvider.PAYS_COLUMN);
				String annee = 			cursor.getString(PaviusProvider.ANNEE_COLUMN);
				String inspiration =    cursor.getString(PaviusProvider.INSPIRATION_COLUMN);

				if(nom.equalsIgnoreCase(" ")) ;
				else ajoutElement("Nom : " + nom);
				
				if(type.equalsIgnoreCase(" ")) ;
				else ajoutElement("Type : " + type);
        
				if(pays.equalsIgnoreCase(" ")) ;
				else ajoutElement("Continent : " + pays);
				
				if(annee.equalsIgnoreCase(" ")) ;
				else ajoutElement("Année : " + annee);
        
				if(inspiration.equalsIgnoreCase(" ")) ;
        		else ajoutElement("Inspiration : " + inspiration);

			} while(cursor.moveToNext());
		}
  }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
      super.onActivityResult(requestCode, resultCode, data);

      //if(requestCode == SHOW_PREFERENCES && resultCode == Activity.RESULT_OK) {
    	if(resultCode == Activity.RESULT_OK) {
        // Lance la recherche du service Internet
    		launchHTTPToDB();
    	}
    }

    /**
     * Configuration du layout par défaut.
     */
    public void configurationLayout() 
    {
  		setContentView(R.layout.renseignements);

  		// Configuration de la ListView des éléments.
  		ListView listview = (ListView) this.findViewById(R.id.listview_);
  		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arraylist);
  		listview.setAdapter(adapter);
    }
}