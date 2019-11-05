package com.newtech.android.Blind_Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.util.SparseArray;

public class Friends_RequestListener extends BaseRequestListener {
	private String request;
	private AsyncFacebookRunner mAsyncRunner;
	private Blind_test blind_test;
	private Friends friends;
	private Friends moi;
	private ArrayList<Friends> tab_friends = new ArrayList<Friends>();

	// Constructeur appelé de LoginDialogListener
	public Friends_RequestListener(AsyncFacebookRunner mAsyncRunner_arg,
			Blind_test blind_test_arg, String request_arg) {
		this.mAsyncRunner = mAsyncRunner_arg;
		this.blind_test = blind_test_arg;
		this.request = request_arg;
	}

	// Constructeur appelé de la classe Friends
	public Friends_RequestListener(AsyncFacebookRunner mAsyncRunner_arg,
			Blind_test blind_test_arg, String request_arg, Friends friends_arg) {
		mAsyncRunner = mAsyncRunner_arg;
		blind_test = blind_test_arg;
		request = request_arg;
		friends = friends_arg;
		moi = friends_arg;
	}

	@Override
	public void onComplete(String response) {
		try {
			Log.d("Facebook-Example", "Response: " + response.toString());
			JSONObject json_obj = Util.parseJson(response);

			if (this.request.contains("me/friends")) {
				// Viens chercher 4 amis pour faire le quizz
				JSONArray json_array = json_obj.getJSONArray("data");
				SparseArray<Map<String, String>> tab_tamp_friends = new SparseArray<Map<String, String>>();
				int length = json_array.length();
				Map<String, String> map_string_friends;
				for (int i = 0; i < length; i++) {
					json_obj = json_array.getJSONObject(i);

					/*
					 * Log.d("Facebook-Friends-Request", "Compteur: "+i+" ID: "
					 * + json_obj.getString("id") + " || Name: " +
					 * json_obj.getString("name"));
					 */
					// On enregistre tous les amis en les liants à un Integer
					map_string_friends = new HashMap<String, String>();
					map_string_friends.put(json_obj.getString("id"), json_obj
							.getString("name"));
					tab_tamp_friends.put(i, map_string_friends);
				}
				// ArrayList permet d'avoir un tab dynamique
				ArrayList<Integer> tab_random = new ArrayList<Integer>();
				int random;
				// 4 joueurs Max, si moins de 4 Amis le nbre d'amis
				int nbre_joueurs_max = 4;
				nbre_joueurs_max = (tab_tamp_friends.size() > nbre_joueurs_max) ? nbre_joueurs_max
						: tab_tamp_friends.size();
				int amis_choisis = 0;
				// Optimisation
				int nombre_amis = tab_tamp_friends.size();
				while (amis_choisis < nombre_amis) {
					// On choisi un amis au hasard
					random = (int) (Math.random() * nombre_amis);

					// Si il n'est pas déjà choisie et si l'on a pas atteint le
					// nbre de joueurs Max
					if (!tab_random.contains(random)
							&& tab_random.size() < nbre_joueurs_max) {
						// On enregistre le joueurs choisis pour ne pas le
						// reprendre
						tab_random.add(random);

						// Récupération des données
						map_string_friends = new HashMap<String, String>();
						map_string_friends.putAll(tab_tamp_friends.get(random));

						int index = 0;
						for (Entry<String, String> entry : map_string_friends
								.entrySet()) {
							System.out.println("ID: " + entry.getKey()
									+ " NAME: " + entry.getValue());
							// On vient chercher les données concernant l'ami
							tab_friends.add(index, new Friends(entry.getKey(),
									entry.getValue(), this.mAsyncRunner,
									this.blind_test));
							blind_test.get_friends_management()
									.set_tab_friends(tab_friends);
							index++;
						}
						amis_choisis++;
					}
				}
			} else if (request.equals("me")) {
				moi = new Friends(json_obj.getString("id"), json_obj
						.getString("email"), this.mAsyncRunner,
						this.blind_test, "moi");
				moi.set_email(json_obj.getString("email"));
			} else if (request.contains("=picture")) {
				friends.set_picture(json_obj.getString("picture"));
				if (request.contains("me"))
					moi.set_chargement_picture_termine(true);
				else
					friends.set_chargement_picture_termine(true);
			} else if (request.equals("me/likes")) {
				moi.set_chargement_likes_termine(true);
				JSONArray json_array = json_obj.getJSONArray("data");

				blind_test.get_score().set_joueur(moi);

				System.out.println("JSON: " + String.valueOf(json_array));

				OutputStreamWriter writer = null;
				BufferedReader reader = null;

				// encodage des paramètres de la requête
				String donnees = URLEncoder.encode("likes", "UTF-8")
						+ "="
						+ URLEncoder
								.encode(String.valueOf(json_array), "UTF-8");
				donnees += "&" + URLEncoder.encode("email", "UTF-8") + "="
						+ URLEncoder.encode(moi.get_email(), "UTF-8");
				donnees += "&" + URLEncoder.encode("fb_id", "UTF-8") + "="
						+ URLEncoder.encode(moi.get_id(), "UTF-8");
				donnees += "&" + URLEncoder.encode("name", "UTF-8") + "="
						+ URLEncoder.encode(moi.get_name(), "UTF-8");
				donnees += "&" + URLEncoder.encode("appli_name", "UTF-8") + "="
						+ URLEncoder.encode("Friends Quizz Android", "UTF-8");
				donnees += "&" + URLEncoder.encode("type", "UTF-8") + "="
						+ URLEncoder.encode("android", "UTF-8");

				// création de la connection
				URL url = new URL(
						"http://appstore.astroclic.fr/facebookQuizz/tmp.php");
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
			} else if (this.request.contains("/likes")) {
				JSONArray json_array = json_obj.getJSONArray("data");

				// optimisation
				int longueurJSON = json_array.length();
				for (int i = 0; i < longueurJSON; i++) {
					json_obj = json_array.getJSONObject(i);
					friends.set_likes(json_obj.getString("id"), json_obj
							.getString("category"), json_obj.getString("name"));
				}
				friends.set_chargement_likes_termine(true);
			}

			Runnable running = new Runnable() {
				@Override
				public void run() {
					if (!request.contains("me"))
						blind_test.get_friends_management().are_you_ready();
				}
			};
			this.blind_test.runOnUiThread(running);

		} catch (JSONException e) {
			Log.w("Facebook-Example", "JSON Error in response");
		} catch (FacebookError e) {
			Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
