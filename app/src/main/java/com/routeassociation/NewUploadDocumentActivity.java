package com.routeassociation;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.routeassociation.pojo.CheckInternet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class NewUploadDocumentActivity extends AppCompatActivity {
    private Spinner docSpinner;
    private EditText edtDocDesc;
    private TextView txtExpDate;
    private ImageView imgSetExpDate,imageView;
    private Button btnTakePicture,btnSave;
    private String imgString;
    private byte[] imgBase64String;
    private Context context;
    private File imageFile;
    private String expDate=null;
    private int year, month, day;
    private ArrayList<String> docList;
    private String docName=null;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        if (item.getItemId() == android.R.id.home) {
            this.startActivity(new Intent(NewUploadDocumentActivity.this,
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
        setContentView(R.layout.activity_new_upload_document);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Upload Document");
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

        //get reference
        context = this;

        //check internet connection
        CheckInternet checkInternet = new CheckInternet();
        boolean checkConnection = checkInternet.checkConnection(context);
        if (checkConnection) {

        } else {
            Toast.makeText(context,
                    "Please check your internet connection or try again later!", Toast.LENGTH_LONG).show();
        }
        //check permission for upload image
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};

        if(!hasPermissions(context, PERMISSIONS)){
            ActivityCompat.requestPermissions(NewUploadDocumentActivity.this, PERMISSIONS, PERMISSION_ALL);
        }

        docSpinner = (Spinner) findViewById(R.id.docSpinner);
        edtDocDesc = (EditText) findViewById(R.id.edtDocDesc);
        txtExpDate = (TextView) findViewById(R.id.txtExpDate);
        imgSetExpDate = (ImageView) findViewById(R.id.imgSetExpDate);
        btnTakePicture = (Button) findViewById(R.id.btnTakePicture);
        imageView = (ImageView) findViewById(R.id.imageView);
        btnSave = (Button) findViewById(R.id.btnSave);

        //set current date
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        txtExpDate.setText(day+"-"+month+"-"+year);

        docList = new ArrayList<String>();
        docList.add("Choose");
        docList.add("PUC");
        docList.add("License");
        docList.add("Insurance");
        docList.add("RC Book");
        docList.add("Police Verification");
        ArrayAdapter<String> spinnerData = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, docList);
        docSpinner.setAdapter(spinnerData);

        docSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0) {
                    docName = docSpinner.getSelectedItem().toString();
                }
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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //expiry date
                if(expDate == null ||  expDate.equals(""))
                {
                    Toast.makeText(context, "Please enter expiry date!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //doc name
                if( docName==null || docName.equals(""))
                {
                    Toast.makeText(context, "Please select document name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //desc
                String docDesc = edtDocDesc.getText().toString().trim();
                if(docDesc.equals("")||docDesc==null)
                {
                    Toast.makeText(context, "Please enter document description!", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

    }

    //set reg date
    public void setDate(View view) {
        showDialog(999);
    }



    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            DatePickerDialog datePickerDialog  =  new DatePickerDialog(context, myDateListener, year, month, day);
            datePickerDialog.show();
            Calendar c  =  Calendar.getInstance();
            int month  =  c.get(Calendar.MONTH );
            int year  =  c.get(Calendar.YEAR);
            int day = c.get(Calendar.DAY_OF_MONTH);
            datePickerDialog.getDatePicker().updateDate(year,month,day);
            // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener  =  new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1  =  year
            // arg2  =  month
            // arg3  =  day
            showDate(arg1, arg2+1, arg3);
        }
    };

    private void showDate(final int year, final int month,final int day) {
        try {
            expDate = new StringBuilder().append(day).append("-")
                    .append(month).append("-").append(year).append(" ").toString();
            txtExpDate.setText("  " + expDate);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
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
                    ActivityCompat.requestPermissions(NewUploadDocumentActivity.this, PERMISSIONS, PERMISSION_ALL);
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
