package com.newtech.android.Blind_Test;

public class Likes {

	private String id_likes;
	private String category;
	private String name;
	private Friends friends;
	private boolean bool_category_musique = false;
	private boolean bool_category_film = false;
	private boolean bool_category_livre = false;
	private boolean bool_category_divers = false;

	public Likes(String id_likes_arg, String category_arg, String name_arg,
			Friends friends_arg) {
		id_likes = id_likes_arg;
		set_category(category_arg);
		name = name_arg;
		friends = friends_arg;
	}

	public String get_id_likes() {
		return id_likes;
	}

	public void set_category(String category_arg) {
		category = category_arg;

		if (category.contains("Musi"))
			bool_category_musique = true;
		else if (category.contains("Film") || category.contains("Movies"))
			bool_category_film = true;
		else if (category.contains("Livre") || category.contains("Book"))
			bool_category_livre = true;
		else
			bool_category_divers = true;
	}

	public boolean get_category_musique() {
		return bool_category_musique;
	}

	public boolean get_category_livre() {
		return bool_category_livre;
	}

	public boolean get_category_film() {
		return bool_category_film;
	}

	public boolean get_category_divers() {
		return bool_category_divers;
	}

	public String get_category() {
		return category;
	}

	public String get_name() {
		return name;
	}

	public Friends get_friends() {
		return friends;
	}

}
