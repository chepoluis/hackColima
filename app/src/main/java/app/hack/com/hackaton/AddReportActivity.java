package app.hack.com.hackaton;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import app.hack.com.hackaton.Model.Reports;

public class AddReportActivity extends AppCompatActivity {

    EditText title;
    EditText description;
    ImageView image;
    Button btnAddImage;
    Button btnUbication;

    // Firebase
    FirebaseDatabase db;
    DatabaseReference reports;
    DatabaseReference keyR;
    DatabaseReference keyReports;

    String keyDb;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private String userID;

    private StorageReference mStorageRef;

    // RadioGroup
    RadioGroup radioGroup;
    RadioButton rbExtravio;
    RadioButton rbMaltrato;
    String type = "";

    // Images
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_add);
        setSupportActionBar(toolbar);

        title = findViewById(R.id.title);
        description = findViewById(R.id.reportDescription);
        image = findViewById(R.id.image);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnUbication = findViewById(R.id.btnUbication);

        radioGroup = findViewById(R.id.radioGroup);
        rbExtravio = findViewById(R.id.extravio);
        rbMaltrato = findViewById(R.id.maltrato);

        // Init firebase
        //userID = mAuth.getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance();
        reports = db.getReference("Report");
        keyR = reports;
        // Create a empty field(key) in the child Reports
        keyReports = keyR.push();

        mStorageRef = FirebaseStorage.getInstance().getReference("report_images");

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_report_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_report:
                uploadFile();
                break;
            default:
                break;
        }
        return true;
    }

    // Upload report to Firebase
    private void uploadFile() {
        String userIdR = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userIdR).child("reports");

        if (rbExtravio.isChecked()) {
            type = "Extravío";
        } else if (rbMaltrato.isChecked()) {
            type = "Maltrato";
        }

        if (mImageUri != null) {

            // Get the image name
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));
            final String fileconext = fileReference.toString();
            String amount = fileconext;
            String[] s = amount.split("/");
            String imageName = s[4];
            final StorageReference ref = mStorageRef.child(imageName);

            uploadTask = ref.putFile(mImageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        Uri downloadUri = task.getResult();
                        String miUrlOk = downloadUri.toString();
                        // urlImage = miUrlOk;
                        //Upload upload = new Upload(productName.getText().toString().trim(), miUrlOk);
                        // String uploadId = mDatabaseRef.push().getKey();
                        //keyDb = keyProduct.getKey();
                        //products.child(keyDb).setValue(upload);

                        keyDb = keyReports.getKey();
                        // Save product to db
                        Reports report = new Reports();
                        report.setIdReport(keyDb);
                        report.setTitle(title.getText().toString());
                        report.setType(type);
                        report.setDescription(description.getText().toString());
                        report.setPicture(miUrlOk);
                        report.setStatus("Pendiente");
                        report.setDate(currentDateTimeString);
                        userRef.child(report.getIdReport()).setValue(true);

                        // Save the product
                        reports.child(keyDb)
                                .setValue(report)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(AddReportActivity.this, "Reporte enviado!", Toast.LENGTH_LONG)
                                                .show();
                                        Intent intent = new Intent(AddReportActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddReportActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddReportActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(this, "Ningun archivo seleccionado", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(image);
        }
    }
}
