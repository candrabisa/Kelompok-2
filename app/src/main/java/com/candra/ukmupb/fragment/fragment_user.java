package com.candra.ukmupb.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.candra.ukmupb.R;
import com.candra.ukmupb.activity.LoginActivity;
import com.candra.ukmupb.activity.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.Value;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class fragment_user extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;

    //deklarasi tempat penyimpanan foto profil dan cover
    private String storagePath = "users_profile_cover_img/";


    //deklarasi data dari xml
    private ImageView imgProfil,CoverProfil;
    private TextView tv_nama, tv_nim, tv_email, tv_password, tv_anggota;
    private EditText editText;
    private ProgressBar progressBar;
    private Button btn_edit;

    //deklarasi dialog
    private ProgressDialog progressDialog;

    //permissions contact
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 400;

    //arrays of permissions to be request
    private String cameraPermissions[];
    private String storagePermissons[];

    //uri picked image
    private Uri image_uri;

    //Check foto profil atau cover
    private String profileOrCoverPhoto;

    public fragment_user() {
        // Required empty public constructor
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        //init firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        storageReference = getInstance().getReference();


        //init views
        progressBar = view.findViewById(R.id.progressBar_user);
        imgProfil = view.findViewById(R.id.imgUser);
        CoverProfil = view.findViewById(R.id.cover_profile);

        tv_nama = view.findViewById(R.id.tv_nama_user);
        tv_email = view.findViewById(R.id.tv_email_user);
        tv_nim = view.findViewById(R.id.tv_nim_user);
        tv_anggota = view.findViewById(R.id.tv_oragnisasi_user);
        btn_edit = view.findViewById(R.id.btn_edit_profil);
        editText = view.findViewById(R.id.editText);

        //init progress dialog
        progressDialog = new ProgressDialog(getActivity());

        //init arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissons = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        progressBar.setVisibility(View.VISIBLE);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //get data
                    String namalengkap = "" + ds.child("namalengkap").getValue();
                    String email = "" + ds.child("email").getValue();
                    String nim = "" + ds.child("nim").getValue();
                    String image = "" + ds.child("image").getValue();
                    String cover = "" + ds.child("cover").getValue();
                    String anggotaukm = "" + ds.child("anggotaukm").getValue();

                    //set data
                    tv_nama.setText(namalengkap);
                    tv_email.setText(email);
                    tv_nim.setText(nim);
                    tv_anggota.setText(anggotaukm);
                    try {
                        Glide.with(getActivity())
                                .load(image)
                                .apply(RequestOptions.circleCropTransform())
                                .into(imgProfil);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.ic_account_circle_black_24dp).into(imgProfil);
                    }
                    try {
                        Picasso.get().load(cover).into(CoverProfil);
                    }
                    catch (Exception e){

                    }

                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfilDialog();
            }
        });

        return view;


    }

    private boolean checkStoragePermissions(){
        return ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
    }
    private void requestStoragePermissions(){
        requestPermissions(storagePermissons, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions(){
        boolean result = ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermissions(){
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void showEditProfilDialog() {
        String[] options = {"Edit Cover Profil", "Edit Foto Profil","Edit Nama", "Edit NIM", "Edit Organisasi"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    //edit foto cover
                    progressDialog.setMessage("Mengubah Gambar Cover");
                    profileOrCoverPhoto = "cover";
                    showImagePicDialog();
                } else if (which == 1){
                    //edit foto profil
                    progressDialog.setMessage("Mengubah Foto Profil");
                    profileOrCoverPhoto = "image";
                    showImagePicDialog();
                } else if (which == 2){
                    //edit nama profil
                    progressDialog.setMessage("Mengubah Nama");
                    showNamaNimOrganisasiUpdateDialog("namalengkap");
                } else if (which == 3){
                    //edit nim
                    progressDialog.setMessage("Mengubah NIM");
                    showNamaNimOrganisasiUpdateDialog("nim");
                } else if (which == 4){
                    //edit Organisasi
                    progressDialog.setMessage("Mengubah Organisasi");
                    showNamaNimOrganisasiUpdateDialog("anggotaukm");
                }

            }
        });
        builder.create().show();
    }

    private void showNamaNimOrganisasiUpdateDialog(final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update "+ key);
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final View customLayout = getLayoutInflater().inflate(R.layout.activity_alert, null);
        builder.setView(customLayout);
        //add button upload
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText = customLayout.findViewById(R.id.editText);
                String value = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)){
                    progressDialog.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);
                    databaseReference.child(firebaseUser.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Berhasil Updated.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "Mohon masukkan "+key, Toast.LENGTH_SHORT).show();
                }
            }
        });
        //add button cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //membuat dan muncul dialog
        builder.create().show();

    }

    private void showImagePicDialog() {
        String options[] = {"Kamera", "Galeri"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pilih gambar dari");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    //pilih camera
                    if (!checkCameraPermissions()){
                        requestCameraPermissions();
                    }
                    else {
                        pickFromCamera();
                    }
                } else if (which == 1) {
                    //pilih gallery
                    if (!checkStoragePermissions()){
                        requestStoragePermissions();
                    }
                    else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length >0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        //permissions enabled
                        pickFromCamera();
                    } else {
                        Toast.makeText(getActivity(), "Mohon izikan aplikasi membaca kamera dan penyimpanan", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length >0){
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        //permissions enabled
                        pickFromGallery();
                    } else {
                        Toast.makeText(getActivity(), "Mohon izikan aplikasi membaca penyimpanan internal", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                assert data != null;
                image_uri = data.getData();
                uploadProfileCoverPhoto(image_uri);
            }
        } if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                assert data != null;
                image_uri = data.getData();
                uploadProfileCoverPhoto(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri image_uri) {
        progressDialog.show();
        String filePathAndName = storagePath+ ""+ profileOrCoverPhoto+" "+ firebaseUser.getUid();

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //uri upload storage database users
                        Task<Uri>uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        //check apakah foto sudah terupload atau tidak dan url diterima
                        if (uriTask.isSuccessful()){
                            //upload gambar
                            HashMap<String, Object> result = new HashMap<>();
                            assert downloadUri != null;
                            result.put(profileOrCoverPhoto, downloadUri.toString());
                            databaseReference.child(firebaseUser.getUid()).updateChildren(result)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), "Berhasil update", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), "Update gagal", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // jika error
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Terjadi error", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //pesan kesan error jika terjadi kesalahan
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void pickFromCamera() {
        //Intent ambil gambar dari kamera handphone
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Desciption");
        image_uri = Objects.requireNonNull(getActivity()).getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //Intent ke kamera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_REQUEST_CODE);

    }

    private void pickFromGallery() {
        //ambil dari galerry
        Intent galerryIntent = new Intent(Intent.ACTION_PICK);
        galerryIntent.setType("image/*");
        startActivityForResult(galerryIntent, IMAGE_PICK_GALLERY_REQUEST_CODE);
    }

    private void checkUserStatus(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser !=null){

        }else {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.navigation, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}

