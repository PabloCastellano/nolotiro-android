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
import android.widget.ListView;

import org.alabs.nolotiro.R;
import org.alabs.nolotiro.Utils;
import org.alabs.nolotiro.Woeid;
import org.alabs.nolotiro.WoeidPlacesTask;

import java.util.List;
import java.util.concurrent.ExecutionException;


//step 3
public class ChooseLocationDialogFragment extends DialogFragment {

    private static final String TAG = "ChooseLocationDialogFragment";


    public interface ChooseLocationDialogListener {
        public void onChooseDialogPositiveClick(DialogFragment dialog, Woeid woeid);
    }

    ChooseLocationDialogListener mListener;

    public ChooseLocationDialogFragment(String location) {
        Bundle args = new Bundle();
        args.putString("location", location);
        this.setArguments(args);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            mListener = (ChooseLocationDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ChooseLocationDialogListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String location = getArguments().getString("location");
        List<Woeid> locations = null;
        WoeidPlacesTask task = new WoeidPlacesTask(this);

        try {
            locations = task.execute(location).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        final List<Woeid> woeids = locations;

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        ListView lw = ((AlertDialog)dialog).getListView();
                        Woeid woeid = woeids.get(lw.getCheckedItemPosition());
                        mListener.onChooseDialogPositiveClick(ChooseLocationDialogFragment.this, woeid);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };

        builder.setTitle(String.format(getResources().getString(R.string.choose_location), location));
        builder.setSingleChoiceItems(Utils.woeidsToCharSequence(locations), 0, null);
        builder .setPositiveButton(R.string.ok, listener)
                .setNegativeButton(R.string.cancel, listener);

        return builder.create();
    }

}
