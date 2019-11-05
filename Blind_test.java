package com.newtech.android.Blind_Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Chronometer.OnChronometerTickListener;
import com.newtech.blind_test.R;

public class Blind_test extends Activity implements OnChronometerTickListener {

	// Init Application
	public static final String FB_APP_ID = "104671426249595";

	private static final String[] PERMISSIONS = new String[] {
			"publish_stream", "user_likes", "friends_likes", "email" };

	/****************/
	/** COMPOSANTS **/
	/****************/
	// Layout->ACCUEIL
	private FrameLayout fram_new_partie;
	private FrameLayout fram_resultat;
	private FrameLayout fram_regle;
	private FrameLayout fram_classement;
	private ImageView b_partager;
	private ImageView b_login;
	private ImageView b_logout;
	private int visibilite = View.INVISIBLE;

	// Layout->QUIZZ
	private Likes likes_gagnant;
	private Score score = new Score();

	// Point
	private TextView points;
	private int compteur_partie = 1;
	private int max_partie = 10;
	private TextView number_partie;
	// Chrono
	private Chronometer chrono;
	private TextView cpt_rebours;
	private long elapsedTime_tamp;
	private String time;
	private int sec_tamp = 10;
	private int sec = 0;
	private int compte_rebours;

	// Joueur
	// Fond
	private ArrayList<FrameLayout> l_joueur = new ArrayList<FrameLayout>();
	// Image
	private ArrayList<ImageView> photo_joueur = new ArrayList<ImageView>();
	// Nom
	private ArrayList<TextView> text_nom_joueur = new ArrayList<TextView>();

	// PopUp Resultat
	public RelativeLayout fond_resultat;
	private TextView text_resultat;
	public boolean popup_score = false;

	/**********/
	/** Init **/
	/**********/
	private Facebook facebook = new Facebook();
	private AsyncFacebookRunner mAsyncRunner;
	private Friends_Management friends_management = new Friends_Management(
			Blind_test.this);
	public ProgressDialog chargement_dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("DŽpart", "DŽpart Application");
		super.onCreate(savedInstanceState);

		// Init
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		chargement_dialog = new ProgressDialog(Blind_test.this);
		chargement_dialog
				.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		chargement_dialog.setMessage("Chargement...");

		init_layout_accueil();
	}

	public void init_layout_accueil() {
		Runtime r = Runtime.getRuntime();
		r.gc();
		// Affichage
		setContentView(R.layout.accueil);
		// Accueil
		fram_new_partie = (FrameLayout) findViewById(R.id.Frame_n_partie);
		fram_new_partie.setVisibility(visibilite);
		fram_new_partie.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				init_layout_multi();
			}
		});

		fram_resultat = (FrameLayout) findViewById(R.id.Frame_resultat);
		fram_resultat.setVisibility(visibilite);
		fram_resultat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				chargement_dialog = ProgressDialog.show(Blind_test.this, "",
						"Chargement des données", true);
				init_layout_resultat();
			}
		});

		fram_regle = (FrameLayout) findViewById(R.id.Frame_regle);
		fram_regle.setVisibility(visibilite);

		fram_classement = (FrameLayout) findViewById(R.id.Frame_classement);
		fram_classement.setVisibility(visibilite);
		fram_classement.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				chargement_dialog = ProgressDialog.show(Blind_test.this, "",
						"Chargement des données", true);
				ArrayList<Friends> array_friends = new ArrayList<Friends>();
				array_friends = score.get_classement();
				init_layout_classement(array_friends);
			}
		});

		b_partager = (ImageView) findViewById(R.id.partager);
		b_partager.setVisibility(visibilite);

		b_login = (ImageView) findViewById(R.id.login);
		b_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				facebook.authorize(Blind_test.this, FB_APP_ID, PERMISSIONS,
						new LoginDialogListener(Blind_test.this, mAsyncRunner));
			}
		});

		b_logout = (ImageView) findViewById(R.id.logout);
		b_logout.setVisibility(visibilite);
		b_logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					visibilite = View.INVISIBLE;
					facebook.logout(Blind_test.this);
					b_logout.setVisibility(visibilite);
					b_partager.setVisibility(visibilite);
					fram_regle.setVisibility(visibilite);
					fram_resultat.setVisibility(visibilite);
					fram_new_partie.setVisibility(visibilite);
					fram_classement.setVisibility(visibilite);
					b_login.setVisibility(View.VISIBLE);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		// http://developers.facebook.com/docs/reference/api/post
		final Bundle partage = new Bundle();
		partage
				.putString(
						"message",
						"Connais tu vraiments tes amis Facebook?\nViens le savoir en téléchargeant BlindFriends!");

		b_partager.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					facebook.request("me/feed", partage, "POST");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void init_bouton_accueil() {
		visibilite = View.VISIBLE;
		b_login.setVisibility(visibilite);
		b_logout.setVisibility(visibilite);
		fram_new_partie.setVisibility(visibilite);
		b_partager.setVisibility(visibilite);
		fram_regle.setVisibility(visibilite);
		fram_resultat.setVisibility(visibilite);
		fram_classement.setVisibility(visibilite);
	}

	public void init_layout_classement(ArrayList<Friends> array_friends) {
		if (score.get_joueur() == null || (!score.get_joueur().get_chargement_likes_termine() && !score.get_joueur().get_chargement_picture_termine())) {
			try {
				Thread.sleep(1000);
				init_layout_resultat();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			chargement_dialog.dismiss();
			LinearLayout layout;
			setContentView(R.layout.classement);
			layout = (LinearLayout) findViewById(R.id.LinearLayoutResult);

			ImageView b_retour = (ImageView) findViewById(R.id.retour);
			b_retour.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					init_layout_accueil();
				}
			});

			// Construction Layout
			// Fond
			ArrayList<FrameLayout> frame_layout_resultat = new ArrayList<FrameLayout>();
			// Image
			ArrayList<ImageView> img_view_fond = new ArrayList<ImageView>();
			// Image
			ArrayList<ImageView> img_view_photo = new ArrayList<ImageView>();
			// Score
			ArrayList<TextView> txt_view_nom = new ArrayList<TextView>();
			// Score
			ArrayList<TextView> txt_view_score = new ArrayList<TextView>();
			int index = 0;
			for (Friends friends : array_friends) {
				// FrameLayout
				frame_layout_resultat.add(new FrameLayout(Blind_test.this));
				frame_layout_resultat.get(index).setLayoutParams(
						new FrameLayout.LayoutParams(
								ViewGroup.LayoutParams.FILL_PARENT,
								ViewGroup.LayoutParams.WRAP_CONTENT));

				// ImageView Fond
				img_view_fond.add(new ImageView(Blind_test.this));
				img_view_fond.get(index).setLayoutParams(
						new FrameLayout.LayoutParams(
								ViewGroup.LayoutParams.WRAP_CONTENT,
								ViewGroup.LayoutParams.WRAP_CONTENT,
								Gravity.CENTER));

				if (!score.get_joueur().get_id().equals(friends.get_id()))
					img_view_fond.get(index).setImageResource(R.drawable.bg_question);
				else
					img_view_fond.get(index).setImageResource(R.drawable.bg_bt_red);
				/*
				 * // ImageView Photo img_view_photo.add(new
				 * ImageView(Blind_test.this));
				 * img_view_photo.get(index).setLayoutParams( new
				 * FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				 * ViewGroup.LayoutParams.WRAP_CONTENT));
				 * img_view_photo.get(index
				 * ).setImageBitmap(friends.get_picture());
				 * img_view_photo.get(index).setPadding(20, 4, 0, 0);
				 */
				// TextView
				txt_view_nom.add(new TextView(Blind_test.this));
				txt_view_nom.get(index).setLayoutParams(
					new FrameLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT,
						Gravity.CENTER_VERTICAL | Gravity.LEFT));
				txt_view_nom.get(index).setText(friends.get_name());
				txt_view_nom.get(index).setTextSize(18);
				txt_view_nom.get(index).setTextColor(Color.WHITE);
				txt_view_nom.get(index).setTypeface(Typeface.DEFAULT_BOLD);
				txt_view_nom.get(index).setPadding(40, 0, 100, 0);

				// TextView
				txt_view_score.add(new TextView(Blind_test.this));
				txt_view_score.get(index).setLayoutParams(
						new FrameLayout.LayoutParams(
								ViewGroup.LayoutParams.WRAP_CONTENT,
								ViewGroup.LayoutParams.WRAP_CONTENT,
								Gravity.CENTER_VERTICAL | Gravity.RIGHT));
				txt_view_score.get(index).setText(friends.get_score() + " points");
				txt_view_score.get(index).setTextSize(16);
				txt_view_score.get(index).setTextColor(Color.WHITE);
				txt_view_score.get(index).setTypeface(Typeface.DEFAULT_BOLD);
				txt_view_score.get(index).setPadding(0, 0, 25, 0);

				frame_layout_resultat.get(index).addView(img_view_fond.get(index));
				// frame_layout_resultat.get(index).addView(img_view_photo.get(index));
				frame_layout_resultat.get(index).addView(txt_view_nom.get(index));
				frame_layout_resultat.get(index).addView(txt_view_score.get(index));
				layout.addView(frame_layout_resultat.get(index), index);

				index++;
			}
		}
	}

	public void init_layout_resultat() {
		if (score.get_joueur() == null
				|| (!score.get_joueur().get_chargement_likes_termine() && !score
						.get_joueur().get_chargement_picture_termine())) {
			try {
				Thread.sleep(1000);
				init_layout_resultat();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			chargement_dialog.dismiss();
			setContentView(R.layout.resultat);
			ArrayList<String> array_score = new ArrayList<String>();
			array_score = score.get_meilleur_score();
			LinearLayout layout;
			layout = (LinearLayout) findViewById(R.id.LinearLayoutResult);

			ImageView b_retour = (ImageView) findViewById(R.id.retour);
			b_retour.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					init_layout_accueil();
				}
			});

			// Construction Layout
			// Fond
			ArrayList<FrameLayout> frame_layout_resultat = new ArrayList<FrameLayout>();
			// Image
			ArrayList<ImageView> img_view_fond = new ArrayList<ImageView>();
			// Image
			ArrayList<ImageView> img_view_photo = new ArrayList<ImageView>();
			// Nom
			ArrayList<TextView> txt_view_score = new ArrayList<TextView>();
			int index = 0;
			for (String credit : array_score) {
				// ImageView Fond
				img_view_fond.add(new ImageView(Blind_test.this));
				img_view_fond.get(index).setLayoutParams(
						new FrameLayout.LayoutParams(
								ViewGroup.LayoutParams.WRAP_CONTENT,
								ViewGroup.LayoutParams.WRAP_CONTENT,
								Gravity.CENTER));
				img_view_fond.get(index).setImageResource(
						R.drawable.bg_question);

				// FrameLayout
				frame_layout_resultat.add(new FrameLayout(Blind_test.this));
				frame_layout_resultat.get(index).setLayoutParams(
						new FrameLayout.LayoutParams(
								ViewGroup.LayoutParams.FILL_PARENT,
								ViewGroup.LayoutParams.WRAP_CONTENT));

				// ImageView Photo
				img_view_photo.add(new ImageView(Blind_test.this));
				img_view_photo.get(index).setLayoutParams(
						new FrameLayout.LayoutParams(
								ViewGroup.LayoutParams.WRAP_CONTENT,
								ViewGroup.LayoutParams.WRAP_CONTENT));
				img_view_photo.get(index).setImageBitmap(
						score.get_joueur().get_picture());
				img_view_photo.get(index).setPadding(20, 4, 0, 0);

				// TextView
				txt_view_score.add(new TextView(Blind_test.this));
				txt_view_score.get(index).setLayoutParams(
						new FrameLayout.LayoutParams(
								ViewGroup.LayoutParams.WRAP_CONTENT,
								ViewGroup.LayoutParams.WRAP_CONTENT,
								Gravity.CENTER));
				txt_view_score.get(index).setText(credit + " points");
				txt_view_score.get(index).setTextSize(20);
				txt_view_score.get(index).setTextColor(Color.WHITE);
				txt_view_score.get(index).setTypeface(Typeface.DEFAULT_BOLD);
				txt_view_score.get(index).setPadding(34, 0, 0, 0);

				frame_layout_resultat.get(index).addView(
						img_view_fond.get(index));
				frame_layout_resultat.get(index).addView(
						img_view_photo.get(index));
				frame_layout_resultat.get(index).addView(
						txt_view_score.get(index));
				layout.addView(frame_layout_resultat.get(index), index);

				index++;
			}
		}
	}

	public void init_layout_multi() {
		Runtime r = Runtime.getRuntime();
		r.gc();
		// Quizz
		setContentView(R.layout.multi);

		// Multi
		ImageView b_retour = (ImageView) findViewById(R.id.retour);
		b_retour.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				init_layout_accueil();
			}
		});

		ImageView b_solo = (ImageView) findViewById(R.id.solo);
		b_solo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				compteur_partie = 1;
				init_layout_quizz();
				chargement_dialog = ProgressDialog.show(Blind_test.this, "",
						"Chargement des données", true);
				mAsyncRunner.request("me/friends", new Friends_RequestListener(
						mAsyncRunner, Blind_test.this, "me/friends"));
			}
		});
		ImageView b_m_joueurs = (ImageView) findViewById(R.id.m_joueurs);
		ImageView b_comment_faire = (ImageView) findViewById(R.id.comment_faire);
	}

	public void init_layout_score() throws IOException {
		Runtime r = Runtime.getRuntime();
		r.gc();
		setContentView(R.layout.score);

		score.publication_score();

		init_bouton_retour();

		TextView felicitation = (TextView) findViewById(R.id.bravo);
		TextView score_final = (TextView) findViewById(R.id.score_final);
		TextView temps_final = (TextView) findViewById(R.id.temps_final);

		if (score.get_score() < 20)
			felicitation.setText("Tu es sûre que ce sont tes amis?");
		else if (score.get_score() < 35)
			felicitation.setText("Mouai...");
		else if (score.get_score() < 50)
			felicitation.setText("Pas trop mal!");
		else if (score.get_score() < 65)
			felicitation.setText("Bravo!");
		else if (score.get_score() < 80)
			felicitation.setText("Fantastique!");
		else if (score.get_score() < 100)
			felicitation.setText("C'est incroyable!!!");
		else if (score.get_score() < 120)
			felicitation.setText("Ca fait peur là!");
		else if (score.get_score() >= 120)
			felicitation.setText("As tu fais buggé mon appli !?");

		score_final.setText("Score : " + String.valueOf(score.get_score())
				+ " points !");
		temps_final.setText("Temps : " + time);

		// http://developers.facebook.com/docs/reference/api/post
		final Bundle partage = new Bundle();
		partage.putString("message", String.valueOf(score.get_score())
				+ " points en " + time + " secondes!\nQui dit mieux?!");

		b_partager = (ImageView) findViewById(R.id.partager);
		b_partager.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					facebook.request("me/feed", partage, "POST");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void init_bouton_retour() {
		ImageView b_accueil = (ImageView) findViewById(R.id.accueil);
		ImageView b_relancer = (ImageView) findViewById(R.id.relancer);

		b_accueil.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				init_layout_multi();
			}
		});
		b_relancer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopChrono();
				compteur_partie = 1;
				init_layout_quizz();
				chargement_dialog = ProgressDialog.show(Blind_test.this, "",
						"Chargement des données", true);
				mAsyncRunner.request("me/friends", new Friends_RequestListener(
						mAsyncRunner, Blind_test.this, "me/friends"));
			}
		});
	}

	public void init_layout_quizz() {
		Runtime r = Runtime.getRuntime();
		r.gc();
		compteur_partie = 1;
		score.init_score(0);
		friends_management.set_tab_random();

		setContentView(R.layout.quizz);

		chrono = ((Chronometer) findViewById(R.id.chrono));
		cpt_rebours = ((TextView) findViewById(R.id.cpt_rebours));

		init_bouton_retour();

		l_joueur.add(0, (FrameLayout) findViewById(R.id.joueur1));
		l_joueur.get(0).setVisibility(View.INVISIBLE);
		l_joueur.add(1, (FrameLayout) findViewById(R.id.joueur2));
		l_joueur.get(1).setVisibility(View.INVISIBLE);
		l_joueur.add(2, (FrameLayout) findViewById(R.id.joueur3));
		l_joueur.get(2).setVisibility(View.INVISIBLE);
		l_joueur.add(3, (FrameLayout) findViewById(R.id.joueur4));
		l_joueur.get(3).setVisibility(View.INVISIBLE);

		photo_joueur.add(0, (ImageView) findViewById(R.id.photo_joueur1));
		photo_joueur.add(1, (ImageView) findViewById(R.id.photo_joueur2));
		photo_joueur.add(2, (ImageView) findViewById(R.id.photo_joueur3));
		photo_joueur.add(3, (ImageView) findViewById(R.id.photo_joueur4));

		text_nom_joueur.add(0, (TextView) findViewById(R.id.nom_joueur1));
		text_nom_joueur.add(1, (TextView) findViewById(R.id.nom_joueur2));
		text_nom_joueur.add(2, (TextView) findViewById(R.id.nom_joueur3));
		text_nom_joueur.add(3, (TextView) findViewById(R.id.nom_joueur4));

		fond_resultat = (RelativeLayout) findViewById(R.id.fond_resultat);
		fond_resultat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		fond_resultat.setVisibility(View.INVISIBLE);
		text_resultat = (TextView) findViewById(R.id.resultat);
		number_partie = (TextView) findViewById(R.id.number_partie);
	}

	public void init_joueur(Likes likes_gagnant_arg, ArrayList<Friends> tab_friend_arg) {
		Runtime r = Runtime.getRuntime();
		r.gc();
		likes_gagnant = likes_gagnant_arg;
		number_partie.setText("Question " + String.valueOf(compteur_partie)
				+ "/10");
		compteur_partie++;

		// Optimisation
		int longueur = tab_friend_arg.size();
		for (int i = 0; i < longueur; i++) {
			final Friends friends = tab_friend_arg.get(i);
			l_joueur.get(i).setVisibility(View.VISIBLE);
			text_nom_joueur.get(i).setText(friends.get_name());
			photo_joueur.get(i).setImageBitmap(friends.get_picture());

			l_joueur.get(i).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					stopChrono();
					fond_resultat.setVisibility(View.VISIBLE);
					popup_score = true;
					Log.d("Choix", "LIKE ID -> "
							+ likes_gagnant.get_friends().get_id()
							+ " -----> Friends ID ->" + friends.get_id());

					if (likes_gagnant.get_friends().get_id() == friends.get_id()) {
						score.set_score(10 + score.get_score_combo()+ compte_rebours);
						text_resultat.setText("VRAI !\n+ "+ (10 + score.get_score_combo() + compte_rebours)+ " points");
						score.set_combo(true);
						points = (TextView) findViewById(R.id.points);
						points.setText(score.get_score() + " points");
					} else {
						score.set_combo(false);
						text_resultat.setText("FAUX...\n\nRéponse...\n"+ likes_gagnant.get_friends().get_name());
					}
				}
			});
		}
		TextView t_question = (TextView) findViewById(R.id.question);
		t_question.setText("Qui est fan de :" + likes_gagnant.get_name());

		if (compteur_partie == 2) {
			chrono.setOnChronometerTickListener(this);
			chrono.setBase(SystemClock.elapsedRealtime());
			sec = 0;
			sec_tamp = 10;
			chrono.start();
		} else
			continueChrono();
	}

	@Override
	public void onChronometerTick(Chronometer chronometer) {
		long elapsedTime = SystemClock.elapsedRealtime()
				- chronometer.getBase();
		int min = (int) (elapsedTime / 60000);
		sec = (int) ((elapsedTime / 1000) % 60);
		time = min < 10 ? "0" + min : String.valueOf(min);
		time += ":";
		time += sec < 10 ? "0" + sec : String.valueOf(sec);
		compte_rebours = (sec_tamp >= 60 && sec == 0) ? sec_tamp -= 60
				: sec_tamp - sec;

		if (compte_rebours == 0) {
			score.set_combo(false);
			fond_resultat.setVisibility(View.VISIBLE);
			text_resultat.setText("Trop lent...\n\nRéponse...\n"
					+ likes_gagnant.get_friends().get_name());
			stopChrono();
		}

		if (compte_rebours == 7 && (compteur_partie - 1) > max_partie) {
			try {
				init_layout_score();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (compteur_partie <= max_partie) {
			chrono.setText(time);
			cpt_rebours.setText(String.valueOf(compte_rebours));
		}
	}

	public void stopChrono() {
		elapsedTime_tamp = SystemClock.elapsedRealtime();
		chrono.stop();

		if (compteur_partie <= max_partie) {
			// Lancement prochain Quizz
			mAsyncRunner.request("me/friends", new Friends_RequestListener(
					mAsyncRunner, Blind_test.this, "me/friends"));
		} else if (compteur_partie > max_partie) {
			compteur_partie++;
			continueChrono();
		}

	}

	public void continueChrono() {
		sec_tamp = sec + 10;
		chrono.setBase(chrono.getBase()
				+ (SystemClock.elapsedRealtime() - elapsedTime_tamp));
		chrono.start();
	}

	public Friends_Management get_friends_management() {
		return friends_management;
	}

	public int get_compteur_partie() {
		return compteur_partie;
	}

	public int get_max_partie() {
		return max_partie;
	}

	public Score get_score() {
		return score;
	}
}