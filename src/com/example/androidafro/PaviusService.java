package com.example.androidafro;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

/**
 * Classe Service - gestion reseau / Internet en tache de fond
 */
public class PaviusService extends Service {

	public static final String NOUVEAU_RENSEIGNEMENT_INSERE = "Nouveau renseignement enregistre";
	public static String INFORMATION_REFRESHED = "com.example.androidafro.INFORMATION_REFRESHED";

  // objet du service
	private myAsyncTask lastLookup = null;

	AlarmManager alarmManager;
	PendingIntent pendingIntent;

	@Override
	public void onCreate() 
	{
		// Recuperation du systeme d'alarmes.
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		// Diffusion de l'action RenseignementAlarmReceiver ?
		pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(PaviusBroadcastReceiver.ACTION_REFRESH_RENSEIGNEMENT_ALARM), 0);
	}

	  /**
	   * Etape 3 : Lancement le thread en tache de fond.
	   */
  	private class myAsyncTask extends AsyncTask<Void, Coiffure, Void> {

  		@Override
  		protected Void doInBackground(Void... params) 
  		{
  			// Recupere la ressource REST
  			URL url;

  			try {  				
  				String feed = "http://latransition.me/pm/pavius.php";

  				url = new URL(feed);

  				URLConnection connection;
  				connection = url.openConnection();

  				HttpURLConnection httpConnection = (HttpURLConnection) connection;
  				int responseCode = httpConnection.getResponseCode();

  				if(responseCode == HttpURLConnection.HTTP_OK) {
  					InputStream in = httpConnection.getInputStream();

  					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
  					DocumentBuilder db = dbf.newDocumentBuilder();

  					// Parse le flux.
  					Document dom = db.parse(in);
  					Element docEle = dom.getDocumentElement();

  					// Récupère une liste de chaque entrée.
  					NodeList nl = docEle.getElementsByTagName("coiffure");

  					if(nl != null && nl.getLength() > 0) {
  						for(int i=0; i<nl.getLength(); i++) {
  							Element entry = (Element) nl.item(i);
  							
  							String nom = entry.getElementsByTagName("nom").item(0).getFirstChild().getNodeValue();
                String type = entry.getElementsByTagName("type").item(0).getFirstChild().getNodeValue(); 
  							String pays = entry.getElementsByTagName("pays").item(0).getFirstChild().getNodeValue(); 
  							String annee = entry.getElementsByTagName("annee").item(0).getFirstChild().getNodeValue();
  							String inspiration = entry.getElementsByTagName("inspiration").item(0).getFirstChild().getNodeValue(); 							
  							
  							Coiffure coiffure = new Coiffure(nom, type, pays, annee, inspiration);

  							// Traite le nouveau element ajouté.
  							addToDB(coiffure);
  						}
  					}
  				}
  			} catch (MalformedURLException e) {
  				e.printStackTrace();
  			} catch (IOException e) {
  				e.printStackTrace();
  			} catch (ParserConfigurationException e) {
  				e.printStackTrace();
  			} catch (SAXException e) {
  				e.printStackTrace();
  			}
  			finally { }

  			return null;
  		}

    	@Override
    	protected void onPostExecute(Void result) 
    	{
    		// Appel des receivers.
  	  	sendBroadcast(new Intent(INFORMATION_REFRESHED));
  	  	stopSelf();
    	}
  	}

  	/**
  	 * Etape 1 : Lancement du service.
  	 */
  	@Override
  	public int onStartCommand(Intent intent, int flags, int startId) 
  	{
  		refreshRenseignements();

      return Service.START_NOT_STICKY;
    }

    /**
     * 
     */
    @Override
    public IBinder onBind(Intent intent) 
    {
    	return null;
    }

    /**
     * Etape 4 
     * Insertion / Mise a jour de la base de donnees.
     * @param _element
     */
    private void addToDB(Coiffure _element) 
    {
    	ContentResolver contentResolver = getContentResolver();

    	// Construit une clause where pour vérifier que cet element n'est pas déjà dans le provider.
    	String where = PaviusProvider.KEY_NOM + " = '" + _element.getNom() + "'";

    	// Si l' element est nouveau, on l'insère.
    	if(contentResolver.query(PaviusProvider.CONTENT_RENSEIGNEMENT_URI, null, where, null, null).getCount() == 0) {

    		ContentValues values = new ContentValues();

		    values.put(PaviusProvider.KEY_NOM, _element.getNom());
        values.put(PaviusProvider.KEY_TYPE, _element.getType());
		    values.put(PaviusProvider.KEY_PAYS, _element.getPays());
		    values.put(PaviusProvider.KEY_ANNEE,	_element.getAnnee());
        values.put(PaviusProvider.KEY_INSPIRATION, _element.getInspiration());

    		contentResolver.insert(PaviusProvider.CONTENT_RENSEIGNEMENT_URI, values);

    		detection(_element);
    	}
    }

    /**
     * Etape 5
     * @param _element
     */
    private void detection(Coiffure _element) 
    {
    	Intent intent = new Intent(NOUVEAU_RENSEIGNEMENT_INSERE);
    	intent.putExtra("nom", _element.getNom());

    	// Envoi le 'pending intent'
    	sendBroadcast(intent);
    }

    /**
     * Etape 2 : Lance le thread de connexion en tache de fond.
     */
    private void refreshRenseignements() 
    {
    	if(lastLookup == null || lastLookup.getStatus().equals(AsyncTask.Status.FINISHED)) {
    		lastLookup = new myAsyncTask();
    		lastLookup.execute();
    	}
    }

}