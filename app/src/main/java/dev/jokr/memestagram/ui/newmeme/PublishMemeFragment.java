package dev.jokr.memestagram.ui.newmeme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.misc.FragmentCreatedListener;
import dev.jokr.memestagram.models.Meme;
import dev.jokr.memestagram.ui.main.MainActivity;
import dev.jokr.memestagram.views.MemeDrawable;

/**
 * Created by jokr on 31.12.16..
 */

public class PublishMemeFragment extends Fragment {

    @Bind(R.id.img_meme)
    ImageView imgMeme;
    @Bind(R.id.btn_publish)
    Button btnPublish;
    @Bind(R.id.etxt_meme_title)
    EditText txtMemeTitle;

    private FragmentCreatedListener fragmentCreatedListener;
    private StorageReference storageRef;
    private DatabaseReference memesRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_publish_meme, container, false);
        ButterKnife.bind(this, v);

        memesRef = FirebaseDatabase.getInstance().getReference("memes");
        fragmentCreatedListener.onFragmentCreated();

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.fragmentCreatedListener = (FragmentCreatedListener) context;
    }

    public void setMemeDrawable(MemeDrawable drawable) {
        imgMeme.setImageDrawable(drawable);
        imgMeme.setAdjustViewBounds(true);
    }

    @OnClick(R.id.btn_publish)
    public void publishMeme() {
        String title = txtMemeTitle.getText().toString();
        Meme m = new Meme(title);

        String key = memesRef.push().getKey();
        Map<String, Object> update = new HashMap<>();
        update.put("/" + key, m.toMap());
        memesRef.updateChildren(update, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e("USER", "Publish meme: " + databaseError.getMessage());
                }
            }
        });

        buildImageAndUpload(key);
    }

    private void buildImageAndUpload(String key) {
        // Get the data from an ImageView as bytes
        imgMeme.setDrawingCacheEnabled(true);
        imgMeme.buildDrawingCache();
        Bitmap bitmap = imgMeme.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        uploadToFirebase(key, data);
    }

    private void uploadToFirebase(String key, byte[] data) {
        storageRef = FirebaseStorage.getInstance().getReference("memes/" + key);
        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e("USER", "UPLOAD FAILURE ", exception.fillInStackTrace());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(getContext(), "Published successfully", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getContext(), MainActivity.class);
                getActivity().startActivity(i);
            }
        });
    }


}
