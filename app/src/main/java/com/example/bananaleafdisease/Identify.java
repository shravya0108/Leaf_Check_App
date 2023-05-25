package com.example.bananaleafdisease;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bananaleafdisease.ml.RealAlexnet;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;

public class Identify extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    public ImageButton cameraBtn;
    public ImageButton galleryBtn;
    public ImageView preview;
    public TextView result;
    public TextView heading;
    public Button predictBtn;

    public Button backBtn;
    public TextView cameraTxt;
    public TextView galleryTxt;
    public Bitmap bitmap;
    public ImageView preview2;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_identify, container, false);

        heading=view.findViewById(R.id.tab_description);
        preview =view.findViewById(R.id.preview);
        cameraBtn = view.findViewById(R.id.camera);
        galleryBtn=view.findViewById(R.id.gallery);
        predictBtn = view.findViewById(R.id.predict_button);
        result=view.findViewById(R.id.textView);
        backBtn=view.findViewById(R.id.back);
        preview2=view.findViewById(R.id.preview2);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                onPhotoButtonClicked();
            }
        });


        galleryBtn.setOnClickListener(view1 -> onGalleryClicked());

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.setText("");
                predictBtn.setVisibility(View.GONE);
                cameraBtn.setVisibility(View.VISIBLE);
                galleryBtn.setVisibility(View.VISIBLE);

                preview.setImageResource(R.drawable.capture1);
                preview2.setImageResource(R.drawable.capture1);
                heading.setText("Upload or click a picture!!");

                backBtn.setVisibility(View.GONE);




            }
        });
        predictBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heading.setText("RESULT");

                try {
                    Context context = getContext();
                    RealAlexnet model = RealAlexnet.newInstance(context);

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
                    bitmap=Bitmap.createScaledBitmap(bitmap,224,224,true);
                    inputFeature0.loadBuffer(TensorImage.fromBitmap(bitmap).getBuffer());


                    // Runs model inference and gets result.
                    RealAlexnet.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    float[] scores = outputFeature0.getFloatArray();
                    int maxIndex = getMax(scores);
                    float maxScore = scores[maxIndex];
                    //float confidencePercentage = maxScore * 100.0f;
                    //float cf=maxScore/10;
                    int x= getMax(outputFeature0.getFloatArray());
                    if(x==0){
                        result.setText("Cordana");
                    } else if (x==1) {
                        result.setText("Healthy");
                    } else if (x==2) {
                        result.setText("Sigatoka");
                    }
                    else{
                        result.setText("Not found!");
                    }


                    // Releases model resources if no longer used.
                    model.close();
                } catch (IOException e) {
                    // TODO Handle the exception
                }

            }
        });
        predictBtn.setVisibility(View.GONE);
        backBtn.setVisibility(View.GONE);
        return view;
    }
    public void onPhotoButtonClicked() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_IMAGE_CAPTURE);
        } else {

            launchCameraIntent();
        }
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchCameraIntent();
        }
    }




    int getMax(float[] arr){
        int max=0;
        for(int i=0;i<arr.length;i++){
            if(arr[i]>arr[max]){
                max=i;
            }
        }
        return max;
    }
    public void onGalleryClicked() {
        Intent iGallery= new Intent(Intent.ACTION_PICK);
        iGallery.setType("image/*");
        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(iGallery, 2);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {


            preview.setVisibility(View.INVISIBLE);
            preview2.setVisibility(View.VISIBLE);
            Bitmap photo = bitmap= (Bitmap) data.getExtras().get("data");

            preview2.setImageBitmap(photo);

            // Hide camera and gallery buttons
            cameraBtn.setVisibility(View.INVISIBLE);
            galleryBtn.setVisibility(View.INVISIBLE);
            // Show predict button
            //cameraTxt.setVisibility(View.GONE);
            predictBtn.setVisibility(View.VISIBLE);
            backBtn.setVisibility(View.VISIBLE);



        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            preview.setVisibility(View.VISIBLE);
            preview2.setVisibility(View.INVISIBLE);
            preview.setImageURI(data.getData());
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Hide camera and gallery buttons
            cameraBtn.setVisibility(View.GONE);
            galleryBtn.setVisibility(View.GONE);

            predictBtn.setVisibility(View.VISIBLE);
            backBtn.setVisibility(View.VISIBLE);
        }
        else{
            Context context = getContext();
            Toast.makeText(context,"Cancelled",Toast.LENGTH_SHORT).show();
        }

    }
}
