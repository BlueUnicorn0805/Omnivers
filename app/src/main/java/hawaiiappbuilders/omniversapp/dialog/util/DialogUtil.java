package hawaiiappbuilders.omniversapp.dialog.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class DialogUtil {
    AlertDialog.Builder alert;
    Context context;
    OnDialogViewListener callback;

    OnDialogViewReadyListener dialogViewListener;

    public DialogUtil(Context context) {
        // initializing the callback object from the constructor
        this.callback = null;
        this.context = context;
        this.alert = new AlertDialog.Builder(context);
    }

    public AlertDialog createDialog(OnDialogViewListener callback) {
        this.callback = callback;
        return this.alert.create();
    }

    // Activity should be a subclass of BaseActivity
    public AlertDialog createDialogView(int viewId, boolean cancellable, boolean isTransparent, OnDialogViewReadyListener dialogViewListener) {
        View dialogView = ((BaseActivity) context).getLayoutInflater().inflate(viewId, null);
        this.dialogViewListener = dialogViewListener;
        dialogView = this.dialogViewListener.onViewReady(dialogView);
        this.alert.setView(dialogView);
        AlertDialog alertDialog = this.alert.create();
        if (isTransparent) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        return alertDialog;
    }

    public DialogUtil setTitleAndMessage(String title, String message) {
        if (!title.isEmpty()) {
            this.alert.setTitle(title);
        }
        if (!message.isEmpty()) {
            this.alert.setMessage(message);
        }
        return this;
    }

    public DialogUtil setCancellable(boolean cancellable) {
        this.alert.setCancelable(cancellable);
        return this;
    }

    public DialogUtil setNegativeButton(String text) {
        this.alert.setNegativeButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onNegativeClick();
            }
        });
        return this;
    }

    public DialogUtil setNeutralButton(String text) {
        this.alert.setNeutralButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onNeutralClick();
            }
        });
        return this;
    }

    public DialogUtil setPositiveButton(String text) {
        this.alert.setPositiveButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onPositiveClick();
            }
        });
        return this;
    }

}
