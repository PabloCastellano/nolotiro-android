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

package org.alabs.nolotiro;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import org.alabs.nolotiro.exceptions.NolotiroException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class CreateAdActivity extends ActionBarActivity {

    private static String TAG = "CreateAdActivity";
    private static int RESULT_LOAD_IMAGE_GALLERY = 1;
    private static int RESULT_LOAD_IMAGE_CAMERA = 2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ad);

        String location = getIntent().getStringExtra("location");
        setTitle(String.format(getResources().getString(R.string.create_new_ad_in), location));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new CreateAdFragment())
                    .commit();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public static String getNolotiroPhotoName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Calendar cal = Calendar.getInstance();
        return String.format("nolotiro_%s.jpg", dateFormat.format(cal.getTime()));
    }

    public void setPhotoFromCamera(Intent data, ImageButton button) {
        Log.i(TAG, "setPhotoFromCamera");
        Log.i(TAG, data.getExtras().toString());
        Toast.makeText(this, "Photo saved", Toast.LENGTH_LONG).show();
        Log.i(TAG, data.getExtras().get(MediaStore.EXTRA_OUTPUT).toString());

        // load into button
        // receive uri
    }

    // Save a photo taken from the camera
    // The photo is saved to nolotiro directory (external if available)
    public void setPhotoFromCamera2(Intent data, ImageButton button) {
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        File f = null;
        FileOutputStream fo = null;
        String nolotiroDir = null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        try {
            nolotiroDir = Utils.getNolotiroDir(this);
            f = new File(nolotiroDir);
            if (!f.exists()) {
                Log.i(TAG, "Mkdir " + f);
                f.mkdirs();
            }

            f = new File(nolotiroDir + getNolotiroPhotoName());
            f.createNewFile();
            fo = new FileOutputStream(f.getAbsoluteFile());
            fo.write(bytes.toByteArray());
            fo.close();
            Log.i(TAG, "File saved to: " + f.getAbsolutePath());

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(f.getAbsolutePath(), bmOptions);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = Math.min(bmOptions.outWidth/100, bmOptions.outHeight/100); // scale factor
            bmOptions.inPurgeable = true;

            //button.setBackground();
            button.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath(), bmOptions));
        } catch (NolotiroException e) {
            Toast.makeText(this, "Error saving photo", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Set the image chosen from gallery to the image button
    // TODO: Scale better, panoramic
    public void setPhotoFromGallery(Intent data, ImageButton button) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, bmOptions);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = Math.min(bmOptions.outWidth/100, bmOptions.outHeight/100); // scale factor
        bmOptions.inPurgeable = true;

        //button.setBackground();
        button.setImageBitmap(BitmapFactory.decodeFile(picturePath, bmOptions));
    }

    public static class CreateAdFragment extends Fragment {

        private ImageButton button;

        public CreateAdFragment() {

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_create_ad, container, false);

            Toast.makeText(getActivity(), getResources().getString(R.string.not_yet_implemented), Toast.LENGTH_LONG).show();

            button = (ImageButton) rootView.findViewById(R.id.choose_photo_button);
            View.OnClickListener listener1 = new View.OnClickListener() {
                public void onClick(View v) {

                    String filePath = null;
                    try {
                        String nolotiroDir = Utils.getNolotiroDir(((CreateAdActivity) getActivity()));
                        File f = new File(nolotiroDir);
                        if (!f.exists()) {
                            Log.i(TAG, "Mkdir " + f);
                            f.mkdirs();
                        }
                        filePath = nolotiroDir + getNolotiroPhotoName();
                    } catch (NolotiroException e) {
                        // asd
                        Toast.makeText(CreateAdFragment.this.getActivity(), "Error saving photo", Toast.LENGTH_LONG).show();
                    }
                    Log.i("createAd", "filePath: " + filePath);

                    File file = new File(filePath);
                    Uri output = Uri.fromFile(file);
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                    startActivity(cameraIntent);
                    //startActivityForResult(cameraIntent, RESULT_LOAD_IMAGE_CAMERA); // devuelve un data que es una imagen reducida, como scacar la grande?

                    // Intent chooser: camera & gallery
                    /* 2
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    Intent[] intentArray =  {cameraIntent};

                    Intent chooserIntent = Intent.createChooser(galleryIntent, "Pick a photo");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooserIntent, RESULT_LOAD_IMAGE);
                    */

                    /* 1
                    //Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //startActivityForResult(intent, RESULT_LOAD_IMAGE);
                    */
                }
            };
            button.setOnClickListener(listener1);

            return rootView;
        }

        public void onActivityResult (int requestCode, int resultCode, Intent data) {
            Log.i(TAG, "actresult: res:" + resultCode + " req:" + requestCode);
            //data es null cuando se llama con EXTRAS

            if (resultCode == RESULT_OK && null != data) {

                if (requestCode == RESULT_LOAD_IMAGE_GALLERY) {
                    Log.i(TAG, "actresult gallery");
                    ((CreateAdActivity) getActivity()).setPhotoFromGallery(data, button);
                } else if (requestCode == RESULT_LOAD_IMAGE_CAMERA) {
                    Log.i(TAG, "actresult camera");
                    ((CreateAdActivity) getActivity()).setPhotoFromCamera(data, button);
                }
            }


        }
    }

}
