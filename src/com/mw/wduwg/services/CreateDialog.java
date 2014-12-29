package com.mw.wduwg.services;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

public class CreateDialog {
	Context context;
	ProgressDialog progressDialog;

	public CreateDialog(Context context) {
		this.context = context;
	}

	public AlertDialog.Builder createAlertDialog(String title, String message,
			boolean cancel) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		if (title != null) {
			alertDialogBuilder.setTitle(title);
		}
		if (message != null) {
			alertDialogBuilder.setMessage(message);
		}
		alertDialogBuilder.setCancelable(cancel);
		return alertDialogBuilder;
	}

	public ProgressDialog createProgressDialog(String title, String message,
			boolean indeterminateState, Drawable drawable) {
		progressDialog = new ProgressDialog(context);
		if (title != null) {
			progressDialog.setTitle(title);
		}
		if (message != null) {
			progressDialog.setMessage(message);
		}

		progressDialog.setIndeterminate(indeterminateState);
		if (drawable != null)
			progressDialog.setIndeterminateDrawable(drawable);
		progressDialog.setCancelable(false);
		return progressDialog;
	}

	public void singleOKButton(final AlertDialog.Builder alertDialogBuilder) {
		alertDialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
	}
}
