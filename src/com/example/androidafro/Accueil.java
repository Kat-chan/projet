package com.example.androidafro;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * Activité 'Accueil'
 * Vignettes cliquables
 * Actionbar disponible
 */
public class Accueil extends ActionBarActivity {
	private static int XACTIVITY_INTENT = 1;
  	// Stocke l'activite voulue pendant l'initialisation.
	private static int RENSEIGNEMENT_ECHEC = 2;

  	/**
  	 * Lancement de l'activite Accueil
  	 */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.accueil);
		setTitleColor(Color.WHITE);

		// Configuration des vignettes sur l'accueil.
		configurationAccueil();
	}
	
	@Override
	protected void onResume() 
	{	
		super.onResume();
		// Configuration des vignettes sur l'accueil.
		configurationAccueil();
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
			{
				Intent intent = new Intent(this, PaviusActivity.class);
				startActivityForResult(intent, XACTIVITY_INTENT);
			}
			return true;
		}

    	return false;
    }

    /**
     * Configuration des items vignettes
     */
    public void configurationAccueil() 
    {
		ImageView imageviewCoiffure = (ImageView) findViewById(R.id.vignette_renseignements);
		ImageView imageviewLesCoiffures = (ImageView) findViewById(R.id.vignette_correspondants);

		// Configuration des vignettes.
		imageviewCoiffure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View _view) {
				Intent intent = new Intent(Accueil.this, PaviusActivity.class);
				startActivity(intent);
			}
		});

		// Configuration des vignettes.
		imageviewLesCoiffures.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View _view) {
				Intent intent = new Intent(Accueil.this, PaviusViewPager.class);
				startActivity(intent);
			}
		});		
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	// X_ACTIVITY_ECHEC ou RENSEIGNEMENT_ECHEC
    	if(requestCode == RENSEIGNEMENT_ECHEC && resultCode == Activity.RESULT_OK) {
			Intent intent = new Intent(Accueil.this, PaviusActivity.class);
			startActivity(intent);
    	}
    }
}