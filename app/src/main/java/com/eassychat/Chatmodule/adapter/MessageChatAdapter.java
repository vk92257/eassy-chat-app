package com.eassychat.Chatmodule.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.eassychat.Chatmodule.ChatActivity;
import com.eassychat.Chatmodule.pojo.MessageDetails;

import com.eassychat.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MessageChatAdapter extends RecyclerView.Adapter<MessageChatAdapter.MyViewHolder> {
    private Context context;
    private int lastPosition = -1;
    private ArrayList<MessageDetails> strings ;
    private String TAG="MessageCahtAdapter";
    private StorageReference mStorageRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("all_messages");
    private   DatabaseReference chatHistory = database.getReference("chat_history");
    private int mCurrentPosition = 0;
    private boolean paused=false;
    private MyViewHolder myViewHolder;
    RecyclerView recyclerView;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_chat,parent,false);
        return new MyViewHolder(view);
    }
    public MessageChatAdapter(Context context, ArrayList<MessageDetails> strings, RecyclerView recyclerView) {
        setHasStableIds(true);
        this.context = context;
        this.strings = strings;
        this.recyclerView = recyclerView;
    }

    private void showImageDialog(String url){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_image_view);
        dialog.setCancelable(true);

        PhotoView imageView = dialog.findViewById(R.id.dialog_imageview);
        Picasso.get().load(url).into(imageView);
        final Window window = dialog.getWindow();
        window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        dialog.show();
;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String covertTimeToText(String date) {

        String convTime = null;
        String prefix = "";
        String suffix = "Ago";
        Date nowTime = new Date();
        Date pasTime = new Date(date);
        long dateDiff = nowTime.getTime() - pasTime.getTime();
        long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
        long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
        long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
        long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

        if (second < 60) {
           convTime = currentTime(date);
//            convTime = second + " Seconds " + suffix;
        } else if (minute < 60) {
            convTime =currentTime(date);
//            convTime = minute + " Minutes "+suffix;
        } else if (hour < 24) {
            convTime =currentTime(date);
//            convTime = hour + " Hours "+suffix;
        } else if (day >= 7) {
            if (day > 360) {
                convTime = (day / 360) + " Years " + suffix;
            } else if (day > 30) {
                convTime = (day / 30) + " Months " + suffix;
            } else {
                convTime = (day / 7) + " Week " + suffix;
            }
        } else if (day < 7) {
            convTime = day+" Days "+suffix;
        }
        return convTime;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String currentTime(String data){
        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        String dateString = dateFormat.format(new Date(data)).toString();
        return  dateString;
    }

   // clearing the space of ImageView of sender and user
    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.myTextImage!= null)
        holder.myTextImage.setImageURI(null);
        if (holder.senderTextImage!=null);
        holder.senderTextImage.setImageURI(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        myViewHolder = holder;
        MediaController controller = new MediaController(context);
        MessageDetails  userData = strings.get(position);
        String id = ChatActivity.user+"_"+ChatActivity.uid;
        if (userData.getId()!=null){
            if (userData.getId().equals(id)){
                holder.my.setVisibility(View.VISIBLE);
                holder.myTextTime.setVisibility(View.VISIBLE);
                holder.myTextTime.setText(covertTimeToText(userData.getTime()));
                if (userData.getImage_uri() != null){
                    holder.my.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                    holder.myTextTime.setTextColor(Color.BLACK);
                    holder.myAvi.setVisibility(View.VISIBLE);
                    holder.myImageParent.setVisibility(View.VISIBLE);
                    holder.myAvi.show();
                    Picasso.get()
                            .load(userData.getImage_uri())
                            .resize(200,200)
                            .centerCrop()
                            .onlyScaleDown()
                            .into(holder.myTextImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.myAvi.hide();
                                    holder.myAvi.setVisibility(View.GONE);
                                }
                                @Override
                                public void onError(Exception e) {
                                    holder.myAvi.hide();
                                }
                            });
                        holder.myTextImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showImageDialog(userData.getImage_uri());
                        }
                    });
                }

                else if (userData.getVideo_uri() != null){
                                    this.position = position;
                                    if (!userData.isDownloaded())
                                        saveVideo(userData,holder);
//                    holder.my.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
//                    holder.myTextTime.setTextColor(Color.BLACK);
                    holder.myTextTime.setTextColor(Color.WHITE);
                    holder.my.setVisibility(View.VISIBLE);
                    holder.myVideoBackgroudn.setVisibility(View.VISIBLE);
                    holder.myDownload.setVisibility(View.VISIBLE);
                    holder.myDownload.bringToFront();
                    Picasso.get().load(userData.getThumbNail())
                            .into(holder.myThumb, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.myThumb.setVisibility(View.VISIBLE);
                                    holder.myAvi.hide();
                                }

                                @Override
                                public void onError(Exception e) {
                                    holder.myAvi.hide();
                                }
                            });
                    holder.myDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            holder.myAviVideo.hide();
                            holder.myDownload.setVisibility(View.GONE);
                            SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();
                            holder.myVideoView.setPlayer(player);
                            MediaItem mediaItem = MediaItem.fromUri(userData.getVideo_uri());
                            holder.myThumb.setVisibility(View.GONE);
                            holder.myVideoView.setShowFastForwardButton(false);
                            holder.myVideoView.setShowRewindButton(false);
                            player.addMediaItem(mediaItem);
                            player.prepare();
                            player.play();
                            holder.myDownload.setVisibility(View.GONE);
                        }
                    });
                }
                else{
                    holder.myText.setVisibility(View.VISIBLE);
                    holder.my.setVisibility(View.VISIBLE);
                    holder.myText.setText(userData.getMessage());
                }
            }
            else {
               holder.sender.setVisibility(View.VISIBLE);
                holder.senderTextTime.setVisibility(View.VISIBLE);
                holder.senderTextTime.setText(covertTimeToText(userData.getTime()));
                if (userData.getImage_uri() != null){
                    holder.senderAvi.setVisibility(View.VISIBLE);
                    holder.sender.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                    holder.senderAvi.show();
                    holder.senderImageParent.setVisibility(View.VISIBLE);
                    Picasso.get().load(userData.getImage_uri())
                            .resize(200,200)
                            .centerCrop()
                            .onlyScaleDown()
                            .into(holder.senderTextImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.senderAvi.hide();
                                }

                                @Override
                                public void onError(Exception e) {
                                    holder.senderAvi.hide();
                                }
                            });
//                          holder.senderTextImage.bringToFront();
                          holder.senderTextImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showImageDialog(userData.getImage_uri());
                        }
                    });
                }
                else if (userData.getVideo_uri() != null){
                    holder.senderDownload.setVisibility(View.VISIBLE);
                    holder.senderDownload.bringToFront();
//                    holder.sender.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                    holder.senderPlayer.setVisibility(View.VISIBLE);
                    holder.senderTextTime.setTextColor(Color.BLACK);
                    Picasso.get().load(userData.getThumbNail())
                            .into(holder.senderThumb, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.senderThumb.setVisibility(View.VISIBLE);
                                    holder.senderAvi.hide();
                                }

                                @Override
                                public void onError(Exception e) {
                                    holder.senderAvi.hide();
                                }
                            });

                    holder.senderDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            holder.mVideoView.setVideoPath(userData.getVideo_uri());
//                            holder.senderVideoView.setVideoPath(userData.getVideo_uri());
                            SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();
                            holder.senderVideoView.setPlayer(player);
                            MediaItem  mediaItem = MediaItem.fromUri(userData.getVideo_uri());
                            holder.senderThumb.setVisibility(View.GONE);
                            holder.senderVideoView.setShowFastForwardButton(false);
                            holder.senderVideoView.setShowRewindButton(false);
                            player.addMediaItem(mediaItem);
                            player.prepare();
                            player.play();
                            holder.senderDownload.setVisibility(View.GONE);
                            holder.senderAviVideo.setVisibility(View.GONE);
                        }
                    });
                }else{
                    holder.senderText.setVisibility(View.VISIBLE);
                    holder.sender.setVisibility(View.VISIBLE);
                    holder.senderText.setText(userData.getMessage());
                }
            }
        }
        else {
            holder.my.setVisibility(View.GONE);
            holder.sender.setVisibility(View.GONE);
            holder.myTextTime.setVisibility(View.GONE);
            holder.senderTextTime.setVisibility(View.GONE);
            holder.myVideoView.setVisibility(View.GONE);
            holder.myTextImage.setVisibility(View.GONE);
            holder.senderTextImage.setVisibility(View.GONE);

        }
    }
    public void clear() {
        strings.clear();
        notifyDataSetChanged();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private  void saveVideo(MessageDetails messageDetails, MyViewHolder holder) {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // saving  a thumbnail
        Glide.with(context)
                .asBitmap()
                .load(messageDetails.video_uri)
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
        riversRef.putFile(Uri.parse(messageDetails.getVideo_uri())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        holder.myDownload.setVisibility(View.VISIBLE);
                        holder.myVideoView.setVisibility(View.VISIBLE);
                        holder.myProgress.setVisibility(View.GONE);
                        messageDetails.setVideo_uri(uri.toString());
                        messageDetails.setDownloaded(true);
                        Picasso.get().load(messageDetails.getThumbNail())
                                .into(holder.myThumb, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.myThumb.setVisibility(View.VISIBLE);
                                    }
                                    @Override
                                    public void onError(Exception e) {
                                    }
                                });

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
                        int p = (int) (100.0 * taskSnapshot.getBytesTransferred()/ taskSnapshot.getTotalByteCount());
                        holder.myDownload.setVisibility(View.GONE);
                        holder.myProgress.setText("Uploading " +p+ " % ");
                        holder.myProgress.setVisibility(View.VISIBLE);
//                        holder.myVideoView.setVisibility(View.GONE);
//                        holder.my.setVisibility(View.VISIBLE);

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
// Add a list of items -- change to type used
    public void showProgress(int progress){
      if (progress>0&&progress<99) {
//          myViewHolder.myAviVideo.setVisibility(View.VISIBLE);
          myViewHolder.myProgress.setVisibility(View.VISIBLE);
          myViewHolder.myProgress.setText("uploading... "+progress+"%");
//          myViewHolder.myVideoBackgroudn.setVisibility(View.GONE);
//          myViewHolder.myDownload.setVisibility(View.GONE);
//          myViewHolder.my.setVisibility(View.VISIBLE);
      }else if (progress==100){
          myViewHolder.myAviVideo.setVisibility(View.GONE);
          myViewHolder.myProgress.setVisibility(View.GONE);
//          myViewHolder.myDownload.setVisibility(View.VISIBLE);
//          myViewHolder.myVideoBackgroudn.setVisibility(View.VISIBLE);
      }
    }
  int position =0;
    public int getPosition(){
        return position;
    }

    @Override
    public int getItemCount()
    {
        return strings.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView myText,senderText,myTextTime,senderTextTime,myProgress,senderProgress;
        ImageView myTextImage,senderTextImage,senderThumb,myThumb;
        FrameLayout my,sender,myDownload,senderDownload;
        CardView myVideoBackgroudn,senderPlayer,myImageParent,senderImageParent;
        AVLoadingIndicatorView myAvi, senderAvi,myAviVideo, senderAviVideo;
        private StyledPlayerView senderVideoView,myVideoView ;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myVideoBackgroudn = itemView.findViewById(R.id.my_video_back);
            senderThumb = itemView.findViewById(R.id.sender_thumb);
            myThumb = itemView.findViewById(R.id.my_thumb);
            myText = itemView.findViewById(R.id.my_text);
            senderText = itemView.findViewById(R.id.sender_text);
            myTextTime = itemView.findViewById(R.id.my_text_time);
            senderTextTime = itemView.findViewById(R.id.sender_text_time);
            myTextImage = itemView.findViewById(R.id.my_text_image);
            senderTextImage = itemView.findViewById(R.id.semder_text_image);
            my = itemView.findViewById(R.id.me);
            sender = itemView.findViewById(R.id.sender);
            myAvi = itemView.findViewById(R.id.my_avi);
            senderAvi = itemView.findViewById(R.id.sender_avi);
            senderVideoView =itemView.findViewById(R.id.ep_video_view);
            myVideoView =itemView.findViewById(R.id.my_ep_video_view);
            myAviVideo = itemView.findViewById(R.id.my_avi_video);
            senderAviVideo = itemView.findViewById(R.id.sender_avi_video);
            myProgress = itemView.findViewById(R.id.my_buffer);
            senderProgress =itemView.findViewById(R.id.sender_buffer);
            // video play button icon
            myDownload = itemView.findViewById(R.id.my_download);
            senderDownload = itemView.findViewById(R.id.sender_download);
            // sender media player
            senderPlayer = itemView.findViewById(R.id.sender_player);

            // image root layout
            myImageParent = itemView.findViewById(R.id.my_image_parent);
            senderImageParent = itemView.findViewById(R.id.sender_image_backgorud);
        }

    }


}
