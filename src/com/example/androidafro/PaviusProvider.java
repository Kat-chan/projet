package com.example.androidafro;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Configuration database SQLite - myDB
 */
public class PaviusProvider extends ContentProvider {

	public static final Uri CONTENT_RENSEIGNEMENT_URI = Uri.parse("content://com.example.provider.coiffure/coiffure");

  	@Override
  	public boolean onCreate() 
  	{
  		Context context = getContext();

  		DatabaseHelper dbHelper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
  		database = dbHelper.getWritableDatabase();

    	return (database == null) ? false : true;
  	}

  	// Fonction de requete.
  	@Override
  	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) 
  	{
  		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

  		qb.setTables(TABLE);

  		// S'il s'agit d'une requete sur une ligne, on limite le resultat.
  		switch(uriMatcher.match(uri)) {
      		case OBJET_ID: qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1)); break;
      		default: break;
  		}
  		
  		// Applique la requete a la base.
  		Cursor cursor = qb.query(database, projection, selection, selectionArgs, null, null, null);

  		// Enregistre le ContextResolver pour qu'il soit averti si le resultat change. 
  		cursor.setNotificationUri(getContext().getContentResolver(), uri);

  		// Renvoie un curseur.
  		return cursor;
  	}

  	// Insertion des donnees.
  	@Override
  	public Uri insert(Uri _uri, ContentValues _initialValues) 
  	{
  		// Insere la nouvelle ligne. Renvoie son numero en cas de succes
  		long rowID = database.insert(TABLE, "nullColumnHack", _initialValues);

  		// Renvoie l'URI de la nouvelle ligne.
  		if(rowID > 0) {
  		  	// **
  			Uri uri = ContentUris.withAppendedId(CONTENT_RENSEIGNEMENT_URI, rowID);
  			getContext().getContentResolver().notifyChange(uri, null);

  			return uri;
  		}

  		throw new SQLException("Echec de l'ajout d'une ligne dans " + _uri);
  	}

  	// Suppression des donnees.
  	@Override
  	public int delete(Uri uri, String where, String[] whereArgs) 
  	{
  		int count;

  		switch(uriMatcher.match(uri)) {
  			case OBJETS: count = database.delete(TABLE, where, whereArgs); break;
  			case OBJET_ID: {
  				String segment = uri.getPathSegments().get(1);
  				count = database.delete(TABLE, KEY_ID + "=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
  			} break;

  			default: throw new IllegalArgumentException("URI non supportée : " + uri);
  		}

  		getContext().getContentResolver().notifyChange(uri, null);

  		return count;
  	}
  	
  	// Mise a jour des donnees.
  	@Override
  	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) 
  	{
  		int count;

  		switch(uriMatcher.match(uri)) {
      		case OBJETS: count = database.update(TABLE, values, where, whereArgs); break;
      		case OBJET_ID: {
      			String segment = uri.getPathSegments().get(1);
                count = database.update(TABLE, values, KEY_ID 
                             				+ "=" + segment 
                             				+ (!TextUtils.isEmpty(where) ? " AND (" 
                             				+ where + ')' : ""), whereArgs);
      		} break;

      		default: throw new IllegalArgumentException("URI inconnue " + uri);
  		}

  		getContext().getContentResolver().notifyChange(uri, null);

    	return count;
  	}

  	// Retourne les donnees sous le format specifie.
  	@Override
  	public String getType(Uri uri) 
  	{
  		switch(uriMatcher.match(uri)) {
  			case OBJETS: 	return "vnd.android.cursor.dir/vnd.example.projetesiea";
  			case OBJET_ID: 	return "vnd.android.cursor.item/vnd.example.projetesiea";
  			default: throw new IllegalArgumentException("URI non supportee : " + uri);
  		}
  	}

  	// Cree les constantes utilisees pour differencier les requetes URI
  	private static final int OBJETS = 1;
  	private static final int OBJET_ID = 2;

  	private static final UriMatcher uriMatcher;

  	// Alloue l'objet UriMatcher. Une URI terminee par 'coiffure' correspondra a une requete sur tous les renseignements.
  	// Une URI terminee par'/[rowID]'  correspondra a une ligne unique. **
  	static {
  		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
  		uriMatcher.addURI("com.example.provider.coiffure", "coiffure", OBJETS);
  		uriMatcher.addURI("com.example.provider.coiffure", "coiffure/#", OBJET_ID);
  	}

  	// La base de donnees
  	private SQLiteDatabase database;
  	// A changer lors d'une copie du provider **
  	private static final String TAG = "PaviusProvider";
  	// **
  	private static final String DATABASE_NAME = "pavius_db_corporate.db";
  	private static final int DATABASE_VERSION = 1;
  	// **
  	private static final String TABLE = "coiffure";

  	// Noms de colonnes **
  	public static final String KEY_ID = "_id";
  	public static final String KEY_NOM = "nom";
    public static final String KEY_TYPE = "type";
  	public static final String KEY_PAYS = "pays";
  	public static final String KEY_ANNEE = "annee";
    public static final String KEY_INSPIRATION = "inspiration";

  	// Indexes **
  	public static final int NOM_COLUMN = 1;
    public static final int TYPE_COLUMN = 2;
  	public static final int PAYS_COLUMN = 3;
    public static final int ANNEE_COLUMN = 4;
  	public static final int INSPIRATION_COLUMN = 5;

  	// Classe helper pour ouvrir, creer et gerer le controle de version de la base.
  	private static class DatabaseHelper extends SQLiteOpenHelper {

      private static final String DB_CREATE = "CREATE TABLE " + TABLE 
                                + " (" 
                                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + KEY_NOM + " TEXT, "
                                + KEY_TYPE + " TEXT, "
                                + KEY_PAYS + " TEXT, "
                                + KEY_ANNEE + " TEXT, "
                                + KEY_INSPIRATION + " TEXT);";

  		public DatabaseHelper(Context context, String name, CursorFactory factory, int version) 
  		{
  			super(context, name, factory, version);
  		}

  		@Override
  		public void onCreate(SQLiteDatabase db) 
  		{
  			db.execSQL(DB_CREATE);
  		}

  		// Initialisee lors d'un changement de version.
  		@Override
  		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
  		{
  			Log.w(TAG, "Mise a jour de la version " + oldVersion + " vers la version " + newVersion 
  					+ ", les anciennes donnees seront detruites ");
  			db.execSQL("DROP TABLE IF EXISTS " + TABLE);
  			onCreate(db);
  		}
  	}
}