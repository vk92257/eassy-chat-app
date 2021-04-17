package com.eassychat.Chatmodule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import com.eassychat.BaseActivity;
import com.eassychat.Chatmodule.adapter.MessageChatAdapter;
import com.eassychat.Chatmodule.pojo.MessageDetails;
import com.eassychat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.paperdb.Paper;

public class ChatActivity extends BaseActivity {
    private static final int VIDEO_CODE = 2;
    private ImageView send, back, gallery, video,profileImage;
    private CardView attachmentConten;
    private EditText input;
    private boolean flag = true;
    private boolean innerFlag = true;
    private TextView mTextView,title;
    private ArrayList<MessageDetails> list;
    private String s = "";
    private StorageReference mStorageRef;
    private ImageView attachment;
    private static int GALLRY_CODE = 1;
    private MessageChatAdapter messageChatAdapter;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("all_messages");
    private DatabaseReference chatHistory = database.getReference("chat_history");
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final int TOTAL_ITEMS_TO_LOAD = 20;
    private int mCurrentPage = 1;
    private ShimmerRecyclerView shimmerRecyclerView;
    private ChildEventListener valueEventListener;
    public static String  user ;
    public static String  uid ;
    private TextView username ;
    public static ArrayList<Uri> imageUri;
    public static ArrayList<Uri> videoUri;
    private LinearLayout viewProfile,chatBox;
    private ValueEventListener chatHistoryListener;
    private int totalNumberOfNodes;

    private ValueEventListener messageValidationListener;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.chat);
        swipeToRefresh();
        findViews();
        list = new ArrayList<>();
           mTextView =  findViewById(R.id.text);
           SharedPreferences pref = getSharedPreferences("com.accountabuddy.android",Context.MODE_PRIVATE);
           title.setText(getIntent().getStringExtra("user_name"));
//        Log.e("TAG", "onCreate: "+ Paper.book().read(ConstantString.PAPER_ID) );
           user = Paper.book().read(PAPER_ID);
           uid = getIntent().getStringExtra(PAPER_ID);
        Log.e("TAG", "onCreate: uid"+uid);
           Picasso.get().load(getIntent().getStringExtra(PROFILE_PIC)).into(profileImage);
          username.setText(getIntent().getStringExtra(NAME));

           iniilizeAdapter();
           messageRecieve();
          fetchingChatHistory();
    }
    /**
     * using pagination from fetching the the previous chat
     * if will fetch the previous chat by the increment of 20.
     */
    private void swipeToRefresh() {
        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mCurrentPage++;
            messageChatAdapter.clear();
            fetchingChatHistory();
        });
        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    /**
     *  fetching the chat history of the user.The starting limit of the chat is
     *
     */
    private void fetchingChatHistory() {
        DatabaseReference databaseReference = chatHistory.child(user + "_" + uid);
        Query queryReffrence = databaseReference.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
        queryReffrence.addListenerForSingleValueEvent(chatHistoryListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.i("TAG", "onDataChange: " + dataSnapshot.getKey() + " value" + dataSnapshot.getValue());
                    MessageDetails   messageDetails = dataSnapshot.getValue(MessageDetails.class);
                    list.add(messageDetails);
                }

                  shimmerRecyclerView.hideShimmerAdapter();
                  viewProfile.setVisibility(View.VISIBLE);
                  chatBox.setVisibility(View.VISIBLE);
                  messageChatAdapter.notifyDataSetChanged();
                  swipeRefreshLayout.setRefreshing(false);
                 shimmerRecyclerView.smoothScrollToPosition(shimmerRecyclerView.getAdapter().getItemCount());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    private void messageRecieve() {
        DatabaseReference databaseReference = myRef.child(uid + "_" + user);
        Query queryReffrence = databaseReference.limitToLast(2);
        queryReffrence.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MessageDetails   messageDetails1 = null;
               for (DataSnapshot dataSnapshot: snapshot.getChildren())
                      messageDetails1 = dataSnapshot.getValue(MessageDetails.class);
                         if (flag == false && messageDetails1 != null){
                       if (messageDetails1.isDownloaded()) {
                        list.add(messageDetails1);
                       if (messageChatAdapter!= null){
                           messageChatAdapter.notifyDataSetChanged();
                           shimmerRecyclerView.smoothScrollToPosition(shimmerRecyclerView.getAdapter().getItemCount());
                       }
                       }
                }
                    flag = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLRY_CODE && resultCode == RESULT_OK) {
            imageUri = new ArrayList<>();
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                for (int i = 0; i < count; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    Log.i("TAG", "onActivityResult: " + "false" + count);
                    Log.i("TAG", "onActivityResult: " + uri.toString());
                    imageUri.add(uri);
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                }
                saveImage();
            } else if (data.getData() != null) {
                Log.i("TAG", "onActivityResult: " + "true");
                Uri uri = data.getData();
                Log.i("TAG", "onActivityResult: " + uri);
                imageUri.add(uri);
                saveImage();
            }
        }
        if (requestCode == VIDEO_CODE && resultCode == RESULT_OK) {
            videoUri = new ArrayList<>();
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                for (int i = 0; i < count; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
//                    Log.i("TAG", "onActivityResult: " + uri.toString());
                    videoUri.add(uri);
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                }
                saveVideo();
            } else if (data.getData() != null) {
                Uri uri = data.getData();
//                Log.i("TAG", "onActivityResult: "+uri);
                videoUri.add(uri);
                saveVideo();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("CheckResult")
    private void saveVideo() {
    mStorageRef = FirebaseStorage.getInstance().getReference();
        if (!videoUri.isEmpty() && videoUri != null) {
            for (Uri url : videoUri) {

                MessageDetails messageDetails = new MessageDetails();
                messageDetails.setVideo_uri(url.toString());
                messageDetails.setTime(new Date().toString());
                messageDetails.setId(user + "_" + uid);
//                String key = myRef.child(user + "_" + uid).push().getKey();
                messageDetails.setDownloaded(false);
//                messageDetails.setKey(key);
//                myRef.child(user + "_" + uid).child(key).setValue(messageDetails);
//                  saveVideo(messageDetails,url.toString());
                    list.add(messageDetails);
                messageChatAdapter.notifyDataSetChanged();
                shimmerRecyclerView.smoothScrollToPosition(shimmerRecyclerView.getAdapter().getItemCount());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private  void saveImage(String image_uri,MessageDetails messageDetails) {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child("images")
                .child("image_"+String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli())+".jpg");
        Bitmap originalImage  = null;
        try {
            originalImage = MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.parse(image_uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        originalImage.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
        byte[] data = outputStream.toByteArray();
        riversRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
//                        MessageDetails messageDetails = new MessageDetails();

                        messageDetails.setImage_uri(uri.toString());
//                        messageDetails.setTime(new Date().toString());
//                        messageDetails.setId(user + "_" + uid);
//                        String key = myRef.child(user + "_" + uid).push().getKey();
//                        messageDetails.setKey(key);
//                        messageDetails.setDownloaded(true);
                        myRef.child(user + "_" + uid).push().setValue(messageDetails);
//                        list.add(messageDetails);
//                        messageChatAdapter.notifyDataSetChanged();
//                        shimmerRecyclerView.smoothScrollToPosition(shimmerRecyclerView.getAdapter().getItemCount());
                        chatHistory.child(ChatActivity.user+"_"+ChatActivity.uid).push().setValue(messageDetails);
                        chatHistory.child(ChatActivity.uid+"_"+ChatActivity.user).push().setValue(messageDetails);

                    }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private  void saveVideo(MessageDetails messageDetails,  String video_uri) {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // saving  a thumbnail
        Glide.with(this)
                .asBitmap()
                .load(video_uri)
                .thumbnail(0.3f)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        StorageReference thumbRef = mStorageRef.child("thumb_nail")
                                .child("image_"+String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli())+".jpg");
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                        byte[] data = outputStream.toByteArray();
                        saveThumbNail(data,messageDetails);
                        return true;
                    }

                })
                .preload();
        // Generating the url form the Firebase url
        StorageReference riversRef = mStorageRef.child("videos")
                .child("video_"+String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli())+".mp4");
        riversRef.putFile(Uri.parse(video_uri)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                         messageDetails.setVideo_uri(uri.toString());
                         messageDetails.setDownloaded(true);
                         myRef.child(ChatActivity.user+"_"+ChatActivity.uid).push().setValue(messageDetails);
                         chatHistory.child(ChatActivity.user+"_"+ChatActivity.uid).push().setValue(messageDetails);
                         chatHistory.child(ChatActivity.uid+"_"+ChatActivity.user).push().setValue(messageDetails);
                    }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

//                        holder.myAviVideo.show();
                         int p = (int) (100.0 * taskSnapshot.getBytesTransferred()/ taskSnapshot.getTotalByteCount());
//                        holder.myProgress.setText(p+" % ");
//                        holder.myProgress.setVisibility(View.VISIBLE);

//                        if (messageChatAdapter!= null){
//                            messageChatAdapter.showProgress(p);
//                        }
//                       if (messageChatAdapter != null) {
//                           Log.e("TAG", "onProgress: in adapter  " + messageChatAdapter.getPosition());
//                           View view =  shimmerRecyclerView.findViewHolderForAdapterPosition(messageChatAdapter.getPosition()).itemView;
//                           TextView textView =  view.findViewById(R.id.my_buffer);
//                            textView.setVisibility(View.VISIBLE);
//                            textView.setText("uploading"+p);
//                       }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveThumbNail(byte[] data, MessageDetails messageDetails) {
        StorageReference thumbRef = mStorageRef.child("thumb_nail")
                .child("image_"+String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli())+".jpg");
        thumbRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                thumbRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        messageDetails.setThumbNail(uri.toString());
                    }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("TAG", "onFailure: "+e.toString());
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveImage() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        if (!imageUri.isEmpty() && imageUri != null) {
            for (Uri url : imageUri) {
//                saveImage(url.toString());
                MessageDetails messageDetails = new MessageDetails();
                messageDetails.setImage_uri(url.toString());
                messageDetails.setTime(new Date().toString());
                messageDetails.setId(user + "_" + uid);
//                String key = myRef.child(user + "_" + uid).push().getKey();
//                messageDetails.setKey(key);
                 messageDetails.setDownloaded(true);
//                myRef.child(user + "_" + uid).child(key).setValue(messageDetails);
                list.add(messageDetails);
                saveImage(url.toString(),messageDetails);
                messageChatAdapter.notifyDataSetChanged();
                shimmerRecyclerView.smoothScrollToPosition(shimmerRecyclerView.getAdapter().getItemCount());

                   Log.i("TAG", "saveImage: inside loop " + url.toString());
            }
        } else {
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void findViews() {
        username = findViewById(R.id.chatTitle);
        attachment = findViewById(R.id.attachment);
        attachment.bringToFront();
        profileImage= findViewById(R.id.profileimage);
        title = findViewById(R.id.chatTitle);
        attachmentConten = findViewById(R.id.attachment_content);
        gallery = findViewById(R.id.camera);
        video = findViewById(R.id.video);
        gallery.bringToFront();
        chatBox = findViewById(R.id.input_bar);
        viewProfile = findViewById(R.id.viewProfile);
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(ChatActivity.this,ViewProfile.class);
//                intent.putExtra("id",uid);
//                startActivity(intent);
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, GALLRY_CODE);
//                Toast.makeText(ChatActivity.this, "gallery", Toast.LENGTH_SHORT).show();
                 attachmentConten.setVisibility(View.GONE);
            }
        });
        video.bringToFront();
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("video/*");
                startActivityForResult(intent, VIDEO_CODE);
                attachmentConten.setVisibility(View.GONE);
//                Toast.makeText(ChatActivity.this, "video", Toast.LENGTH_SHORT).show();
            }
        });
        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (attachmentConten.getVisibility() == View.GONE)
                    attachmentConten.setVisibility(View.VISIBLE);
                else attachmentConten.setVisibility(View.GONE);
            }
        });
        shimmerRecyclerView = findViewById(R.id.message_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        shimmerRecyclerView.setLayoutManager(linearLayoutManager);
        shimmerRecyclerView.setItemViewCacheSize(50);
        shimmerRecyclerView.showShimmerAdapter();
        sendMessage();
    }

    private void sendMessage() {
        input = findViewById(R.id.input);
        send = findViewById(R.id.messag_send);
        s = input.getText().toString();
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                send.setImageResource(R.drawable.ic_baseline_sendblue_24);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                s = editable.toString();
            }

        });
        send.bringToFront();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("TAG", "onClick: " + s + uid);
                if (!s.isEmpty() && uid != null) {
                    sendProcess();
                    send.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_send_24));
                }
            }
        });

    }

    private void sendProcess() {
        MessageDetails messageDetails = new MessageDetails();
        messageDetails.setMessage(s);
        messageDetails.setTime(new Date().toString());
        messageDetails.setDownloaded(true);
        messageDetails.setId(user+"_"+uid);
        myRef.child(user+"_"+uid).push().setValue(messageDetails);
        chatHistory.child(user+"_"+uid).push().setValue(messageDetails);
        chatHistory.child(uid+"_"+user).push().setValue(messageDetails);
        list.add(messageDetails);
//        iniilizeAdapter();
        messageChatAdapter.notifyDataSetChanged();
        input.setText("");
        shimmerRecyclerView.smoothScrollToPosition(shimmerRecyclerView.getAdapter().getItemCount());
    }

    private void iniilizeAdapter() {
        messageChatAdapter = new MessageChatAdapter(this,list,shimmerRecyclerView);
        shimmerRecyclerView.setAdapter(messageChatAdapter);
        shimmerRecyclerView.setHasFixedSize(true);

        shimmerRecyclerView.showShimmerAdapter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = true;
        imageUri = null;
        if (myRef!= null ){
            if (valueEventListener!= null) myRef.removeEventListener(valueEventListener);
            if (chatHistoryListener != null) myRef.removeEventListener(chatHistoryListener);
            if (messageValidationListener != null )myRef.removeEventListener(messageValidationListener);
        }
       if (messageChatAdapter!= null) messageChatAdapter = null;
       if (shimmerRecyclerView != null) {
           shimmerRecyclerView.removeAllViews();
           shimmerRecyclerView.setAdapter(null);
           shimmerRecyclerView = null;
       }

    }


}
