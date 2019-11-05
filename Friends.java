package com.newtech.android.Blind_Test;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Friends {
	private String id;
	private String name;
	private Bitmap picture;
	private String email;
	private Map<String, Map<String, String>> id_vs_infos = new HashMap<String, Map<String, String>>();
	private boolean chargement_picture = false;
	private boolean chargement_likes = false;
	private int score;

	public Friends(String id_arg, String name_arg,
			AsyncFacebookRunner mAsyncRunner, Blind_test blind_test, String moi) {
		this.id = id_arg;
		this.name = name_arg;

		String request_picture = "me&fields=picture";
		String request_likes = "me/likes";

		mAsyncRunner.request(request_picture, new Friends_RequestListener(
				mAsyncRunner, blind_test, request_picture, Friends.this));
		mAsyncRunner.request(request_likes, new Friends_RequestListener(
				mAsyncRunner, blind_test, request_likes, Friends.this));
	}

	public Friends(String id_arg, String name_arg,
			AsyncFacebookRunner mAsyncRunner, Blind_test blind_test) {
		id = id_arg;
		name = name_arg;

		String request_picture = this.id + "&fields=picture";
		String request_likes = this.id + "/likes&limit=40";

		mAsyncRunner.request(request_picture, new Friends_RequestListener(
				mAsyncRunner, blind_test, request_picture, Friends.this));

		mAsyncRunner.request(request_likes, new Friends_RequestListener(
				mAsyncRunner, blind_test, request_likes, Friends.this));
	}

	public Friends(String id_arg, String name_arg, int score_arg) {
		id = id_arg;
		name = name_arg;
		score = score_arg;
	}

	public String get_name() {
		return name;
	}

	public String get_id() {
		return id;
	}

	public void set_picture(String picture_arg) {

		InputStream inputStream = null;
		try {
			inputStream = new URL(picture_arg).openStream();
		} catch (Exception e) {

		}
		picture = BitmapFactory.decodeStream(inputStream);
	}

	public Bitmap get_picture() {
		return picture;
	}

	public boolean get_chargement_picture_termine() {
		return chargement_picture;
	}

	public void set_chargement_picture_termine(boolean chargement_picture_arg) {
		chargement_picture = chargement_picture_arg;
	}

	public void set_likes(String id, String category, String name) {
		Map<String, String> category_vs_likes = new HashMap<String, String>();
		category_vs_likes.put(category, name);
		id_vs_infos.put(id, category_vs_likes);
	}

	public Map<String, Map<String, String>> get_likes() {
		return id_vs_infos;
	}

	public boolean get_chargement_likes_termine() {
		return chargement_likes;
	}

	public void set_chargement_likes_termine(boolean chargement_likes_arg) {
		chargement_likes = chargement_likes_arg;
	}

	public void set_email(String email_arg) {
		email = email_arg;
	}

	public String get_email() {
		return email;
	}

	public int get_score() {
		return score;
	}

	public void set_score(int score_arg) {
		score = score_arg;
	}
}
