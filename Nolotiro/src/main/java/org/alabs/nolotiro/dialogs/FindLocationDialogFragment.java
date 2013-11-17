package org.alabs.nolotiro.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.alabs.nolotiro.R;

// This dialog asks the user to enter his location.
// Once he clicks 'Ok', it will query GeoIP Yahoo API to find places with this name.
// If there's more than one possibility then a new ChooseLocationDialogFragment is shown.
// The user will choose the correct location and it will be saved with its woeid.

public class FindLocationDialogFragment extends DialogFragment {

    private static final String TAG = "FindLocationDialogFragment";

    public interface FindLocationDialogListener {
        public void onFindDialogPositiveClick(DialogFragment dialog, String location);
    }

    FindLocationDialogListener mListener;

    public FindLocationDialogFragment() {

    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            mListener = (FindLocationDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement FindLocationDialogListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_location, null);
        final EditText textView = (EditText) view.findViewById(R.id.location_text_view);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        mListener.onFindDialogPositiveClick(FindLocationDialogFragment.this, textView.getText().toString());
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };

        builder.setTitle(R.string.change_location);
        //textView.setText("Cordoba");
        builder.setView(view)
                .setPositiveButton(R.string.ok, listener)
                .setNegativeButton(R.string.cancel, listener);

        return builder.create();
    }

}
