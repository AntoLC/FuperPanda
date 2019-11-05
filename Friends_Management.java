package com.newtech.android.Blind_Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;
import android.util.SparseArray;
import android.view.View;

public class Friends_Management {

	// Init
	private ArrayList<Friends> tab_friends_total = new ArrayList<Friends>();
	private Blind_test blind_test;
	private boolean quizz_pret = false;
	private ArrayList<Integer> tab_random = new ArrayList<Integer>();

	public Friends_Management(Blind_test blind_test_arg) {
		this.blind_test = blind_test_arg;
	}

	// Charge le tableau d'objet d'amis
	public void set_tab_friends(ArrayList<Friends> tab_friends_total_arg) {
		quizz_pret = false;
		tab_friends_total = tab_friends_total_arg;
	}

	public void set_tab_random() {
		tab_random = new ArrayList<Integer>();
	}

	public void are_you_ready() {
		if (!quizz_pret) {
			boolean chargement_termine_picture = false;
			boolean chargement_termine_likes = false;
			// Charge chaques amis
			Iterator<Friends> it = tab_friends_total.iterator();
			while (it.hasNext()) {
				Friends friend = it.next();
				chargement_termine_likes = friend
						.get_chargement_likes_termine();
				chargement_termine_picture = friend
						.get_chargement_picture_termine();

				if (!chargement_termine_likes || !chargement_termine_picture)
					break;
			}

			// Si chargement fini, on vient organiser les likes(supprimer
			// doublons//les ranger par catégories)
			if (chargement_termine_likes && chargement_termine_picture) {
				quizz_pret = true;
				init_likes();
			}
		}
	}

	// Are you ready est appelé via un thread(Friends_RequestListener), cela
	// regarde si le
	// chargement des informations Facebook est terminé.
	public void init_likes() {
		// Init
		ArrayList<String> array_id_likes = new ArrayList<String>();
		ArrayList<String> array_id_likes_mutiple = new ArrayList<String>();
		int compteur = 0;

		// Charge chaques amis
		Iterator<Friends> iterator = this.tab_friends_total.iterator();
		while (iterator.hasNext()) {
			Friends friend = iterator.next();
			Map<String, Map<String, String>> map_friend_likes = friend.get_likes();

			for (Entry<String, Map<String, String>> entry : map_friend_likes.entrySet()) {
				// Viens voir les likes doublons pour les supprimer
				if (array_id_likes.contains(entry.getKey()))
					array_id_likes_mutiple.add(entry.getKey());

				array_id_likes.add(entry.getKey());
			}
		}
		// On supprime les id_likes multiples (Unicite likes/amis)
		array_id_likes.removeAll(array_id_likes_mutiple);

		// On re-scan les amis, cela fait répétition de code à première vue mais
		// c'est une étape obligé
		// car nous ne pouvons savoir si il y a doublons que après avoir sondé
		// TOUS les amis.
		Iterator<Friends> it = this.tab_friends_total.iterator();
		SparseArray<Likes> map_likes = new SparseArray<Likes>();
		while (it.hasNext()) {
			// Init
			Friends friend = it.next();
			Map<String, Map<String, String>> map_friend_likes = friend.get_likes();
			Map<String, String> map_cat_likes;
			String id;
			String category = null;
			String name_likes = null;

			// Parcours de get_likes
			for (Entry<String, Map<String, String>> entry : map_friend_likes.entrySet()) {
				id = entry.getKey();
				if (array_id_likes.contains(id)) // Si pas doublons
				{
					Likes likes;
					map_cat_likes = entry.getValue();

					// Parcours de la map_like<category, name>
					for (Entry<String, String> entry2 : map_cat_likes.entrySet()) {
						category = entry2.getKey();
						name_likes = entry2.getValue();

						// On enregistre tous les likes avec l'amis associé
						// Petit objet de likes pour faciliter
						// l'accès au données(moins compliqué qu'un tableau)
						likes = new Likes(id, category, name_likes, friend);
						map_likes.put(compteur, likes);
						compteur++;
					}
				}
			}
		}
		winner_is(map_likes);
	}

	// Choisi une question et un ami gagnant
	public void winner_is(SparseArray<Likes> map_likes) {
		// Choix d'un like au hasard qui ne soit pas déjà passé
		int random = -1;
		int compteur = 0;

		compteur = map_likes.size();

		do {
			random = (int) (Math.random() * compteur);
			if (!tab_random.contains(random))
				tab_random.add(random);
			else
				random = -1;
		} while (random == -1);

		Likes likes_gagnant = map_likes.get(random);

		blind_test.chargement_dialog.dismiss();
		blind_test.fond_resultat.setVisibility(View.INVISIBLE);
		Log.d("Attention!!!", "PRET");
		blind_test.init_joueur(likes_gagnant, tab_friends_total);
	}

}
