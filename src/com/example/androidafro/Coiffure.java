package com.example.androidafro;

import java.util.ArrayList;

public class Coiffure {
	private String nom;
	private String type;
	private String pays;
	private String annee;
	private String inspiration;

	public Coiffure(String _nom, String _type, String _pays, String _annee, String _inspiration) 
	{
		this.nom = _nom;
		this.type = _type;
		this.pays = _pays;
		this.annee = _annee;
		this.inspiration = _inspiration;
	}

	public String getNom() { return this.nom; }
	public String getType() { return this.type; }
	public String getPays() { return this.pays; }
	public String getAnnee() { return this.annee; }
	public String getInspiration() { return this.inspiration; }

}