package com.routeassociation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.routeassociation.pojo.CheckInternet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class UploadIncidenceActivity extends AppCompatActivity {

    private Spinner incTypeSpinner;
    private Button btnTakePicture;
    private ImageView imageView;
    private String imgString;
    private byte[] imgBase64String;
    private File imageFile;
    private Context context;

    private ArrayList<String> incidenceTypeList;
    private String incType;
    private EditText edttIncDesc;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        if (item.getItemId() == android.R.id.home) {
            this.startActivity(new Intent(UploadIncidenceActivity.this,
                    MainActivity.class));
            return true;
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_incidence);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Upload Incidence");
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
        //set white back icon to toolbar
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


        context = this;

        CheckInternet checkInternet = new CheckInternet();
        boolean checkConnection = checkInternet.checkConnection(context);
        if (checkConnection) {

        } else {
            Toast.makeText(context,
                    "connection not found...plz check connection", Toast.LENGTH_LONG).show();
        }
        //check permission for upload image
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};

        if(!hasPermissions(context, PERMISSIONS)){
            ActivityCompat.requestPermissions(UploadIncidenceActivity.this, PERMISSIONS, PERMISSION_ALL);
        }


        edttIncDesc = (EditText)findViewById(R.id.edttIncDesc);
        btnTakePicture = (Button) findViewById(R.id.btnTakePicture);
        imageView = (ImageView) findViewById(R.id.imageView);
        incTypeSpinner = (Spinner)findViewById(R.id.incTypeSpinner);
        //make list for inc type
        incidenceTypeList = new ArrayList<String>();
        incidenceTypeList.add("Type1");
        incidenceTypeList.add("Type2");
        incidenceTypeList.add("Type3");
        incidenceTypeList.add("Type4");
        ArrayAdapter<String> spinnerData = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, incidenceTypeList);
        incTypeSpinner.setAdapter(spinnerData);

        incTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              incType =   incTypeSpinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        //save incidence
        Button btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String incDesc = edttIncDesc.getText().toString().trim();
                //description
                if(incDesc.equals("") || incDesc == null)
                {
                    Toast.makeText(context, "Incidence details should not be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //type
                if(incType.equals("") || incType == null)
                {
                    Toast.makeText(context, "Incidence type should not be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                int PERMISSION_ALL = 1;
                String[] PERMISSIONS = {android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.WRITE_CONTACTS, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_SMS, android.Manifest.permission.CAMERA};

                if(!hasPermissions(context, PERMISSIONS)){
                    ActivityCompat.requestPermissions(UploadIncidenceActivity.this, PERMISSIONS, PERMISSION_ALL);
                }
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File imageFile = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //    chikchik = true;
            if (requestCode == 1) {

                imageFile = new File(Environment.getExternalStorageDirectory().toString());

                for (File temp : imageFile.listFiles()) {

                    if (temp.getName().equals("temp.jpg")) {

                        imageFile = temp;

                        break;

                    }

                }

                try {

                    Bitmap bitmap;

                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();


                    bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(),

                            bitmapOptions);


                    imageView.setImageBitmap(bitmap);
                    imgString = encodeImage(bitmap);


                    String path = android.os.Environment

                            .getExternalStorageDirectory()

                            + File.separator

                            + "Phoenix" + File.separator + "default";

                    // imageFile.delete();

                    OutputStream outFile = null;

                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");

                    try {

                        outFile = new FileOutputStream(file);

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);

                        outFile.flush();

                        outFile.close();

                    } catch (FileNotFoundException e) {

                        e.printStackTrace();

                    } catch (IOException e) {

                        e.printStackTrace();

                    } catch (Exception e) {

                        e.printStackTrace();

                    }

                } catch (Exception e) {

                    e.printStackTrace();

                }

            } else if (requestCode == 2) {

                try {


                    Uri selectedImage = data.getData();

                    String[] filePath = {MediaStore.Images.Media.DATA};

                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);

                    c.moveToFirst();

                    int columnIndex = c.getColumnIndex(filePath[0]);

                    String picturePath = c.getString(columnIndex);

                    String fileName = picturePath.substring(picturePath.lastIndexOf('/') + 1);
                    imageFile = new File(picturePath);

                    c.close();

                    Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));

                    imgString = encodeImage(thumbnail);

                    //  Log.w("path of image from gallery......******************.........", picturePath+"");

                    imageView.setImageBitmap(thumbnail);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
    private String encodeImage(Bitmap bm) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            Log.e("Original   dimensions", bm.getWidth() + " ");
            Log.e("Compressed dimensions", bm.getWidth() + " ");

            imgBase64String = baos.toByteArray();
            String image = Base64.encodeToString(imgBase64String, Base64.DEFAULT);

            Log.d("hi", "vfvfds" + image);
            return image;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return  null;


    }
}
