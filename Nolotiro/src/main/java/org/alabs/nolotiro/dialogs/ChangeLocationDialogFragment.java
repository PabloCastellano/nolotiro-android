/*
 * Copyright (c) 2013 "Pablo Castellano <pablo@anche.no>"
 * Copyright (c) 2013 "Eugenio Cano-Manuel Mendoza <eugeniocanom@gmail.com>"
 * Nolotiro App [http://nolotiro.org]
 *
 * This file is part of nolotiro-android.
 *
 * nolotiro-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.alabs.nolotiro.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;

import org.alabs.nolotiro.R;
import org.alabs.nolotiro.Utils;
import org.alabs.nolotiro.Woeid;
import org.alabs.nolotiro.db.DbAdapter;

import java.util.List;


//step 1
public class ChangeLocationDialogFragment extends DialogFragment {

    private static final String TAG = "ChangeLocationDialogFragment";

    public interface ChangeLocationDialogListener {
        public void onChangeDialogNeutralClick(DialogFragment dialog);
        public void onChangeDialogPositiveClick(DialogFragment dialog, Woeid woeid);
    }

    ChangeLocationDialogListener mListener;

    public ChangeLocationDialogFragment() {

    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            mListener = (ChangeLocationDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ChangeLocationDialogListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.change_location);

        DbAdapter dba = new DbAdapter(getActivity());
        dba.openToRead();
        final List<Woeid> locations = dba.getWoeids();
        dba.close();

        // TODO: Pre-select current location
        builder.setSingleChoiceItems(Utils.woeidsToCharSequence(locations), 0, null);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();;
                        mListener.onChangeDialogPositiveClick(ChangeLocationDialogFragment.this, locations.get(selectedPosition));
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        mListener.onChangeDialogNeutralClick(ChangeLocationDialogFragment.this);
                        break;
                }
            }
        };

        builder .setPositiveButton(R.string.ok, listener)
                .setNeutralButton(R.string.add_new, listener)
                .setNegativeButton(R.string.cancel, listener);

        return builder.create();
    }

}
