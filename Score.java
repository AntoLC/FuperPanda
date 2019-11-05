package com.newtech.android.Blind_Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Score {

	private int score = 0;
	private String temps = "";
	private int score_combo;
	private Friends joueur = null;

	public Score() {

	}

	public void init_score(int score_arg) {
		score = score_arg;
	}

	public void set_score(int score_arg) {
		score += score_arg;
	}

	public int get_score() {
		return score;
	}

	public void set_temps(String temps_arg) {
		temps = temps_arg;
	}

	public String get_temps() {
		return temps;
	}

	public void set_combo(boolean combo_arg) {
		score_combo = (combo_arg) ? score_combo += 5 : 0;
	}

	public int get_score_combo() {
		return score_combo;
	}

	public void set_joueur(Friends joueur_arg) {
		joueur = joueur_arg;
	}

	public Friends get_joueur() {
		return joueur;
	}

	public void publication_score() {
		OutputStreamWriter writer = null;
		BufferedReader reader = null;
		try {
			// encodage des paramètres de la requête
			String donnees = "&" + URLEncoder.encode("user", "UTF-8") + "="
					+ URLEncoder.encode(joueur.get_id(), "UTF-8");

			donnees += "&" + URLEncoder.encode("score", "UTF-8") + "="
					+ URLEncoder.encode(String.valueOf(score), "UTF-8");

			// création de la connection
			URL url = new URL(
					"http://appstore.astroclic.fr/facebookQuizz/tmpScore.php");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			// envoi de la requête
			writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(donnees);
			writer.flush();

			// lecture de la réponse
			reader = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			String ligne;
			while ((ligne = reader.readLine()) != null) {
				System.out.println(ligne);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<String> get_meilleur_score() {
		// TRISTAN -> ID_JOUEUR: 686724628
		ArrayList<String> array_score = new ArrayList<String>();
		try {
			URL url = new URL(
					"http://appstore.astroclic.fr/facebookQuizz/getTenBestScores_android.php?fbid="
							+ joueur.get_id());
			InputStream stream = url.openStream();
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(stream);
			Element racine = document.getDocumentElement();
			NodeList score = racine.getElementsByTagName("score");

			int longueur = score.getLength();
			for (int i = 0; i < longueur; i++)
				array_score.add(getTextContent(score.item(i)));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return array_score;
	}

	public ArrayList<Friends> get_classement() {
		// TRISTAN -> ID_JOUEUR: 686724628
		ArrayList<Friends> array_friends = new ArrayList<Friends>();
		try {
			URL url = new URL(
					"http://appstore.astroclic.fr/facebookQuizz/getClassement_android.php?fbid="
							+ joueur.get_id());
			InputStream stream = url.openStream();
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(stream);
			Element racine = document.getDocumentElement();
			NodeList id = racine.getElementsByTagName("id");
			NodeList name = racine.getElementsByTagName("name");
			NodeList score = racine.getElementsByTagName("score");

			int longueur = score.getLength();
			for (int i = 0; i < longueur; i++)
				array_friends.add(new Friends(getTextContent(id.item(i)),
						getTextContent(name.item(i)), Integer
								.valueOf(getTextContent(score.item(i)))));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return array_friends;
	}

	private String getTextContent(Node item) {
		NodeList nodeList = item.getChildNodes();
		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.TEXT_NODE) {
					return node.getNodeValue();
				}
			}
		}
		return null;
	}
}
