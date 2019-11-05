package com.newtech.android.Blind_Test;

import android.os.Bundle;

public final class LoginDialogListener implements DialogListener {
	private Blind_test blind_test;
	private AsyncFacebookRunner mAsyncRunner;

	public LoginDialogListener(Blind_test blind_test_arg,
			AsyncFacebookRunner mAsyncRunner_arg) {
		blind_test = blind_test_arg;
		mAsyncRunner = mAsyncRunner_arg;
	}

	public void onComplete(Bundle values) {
		SessionEvents.onLoginSuccess(this.blind_test);
		mAsyncRunner.request("me", new Friends_RequestListener(mAsyncRunner,
				blind_test, "me"));
		blind_test.init_bouton_accueil();
	}

	public void onFacebookError(FacebookError error) {
		SessionEvents.onLoginError(error.getMessage(), this.blind_test);
	}

	public void onError(DialogError error) {
		SessionEvents.onLoginError(error.getMessage(), this.blind_test);
	}

	public void onCancel() {
		SessionEvents.onLoginError("Action Canceled", this.blind_test);
	}
}
