package net.crescenthikari.popmovies.features.moviedetail.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.crescenthikari.popmovies.R;
import net.crescenthikari.popmovies.data.model.MovieVideo;
import net.crescenthikari.popmovies.features.moviedetail.contract.VideoOnClickCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Muhammad Fiqri Muthohar on 7/31/17.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {

    List<MovieVideo> videos;
    VideoOnClickCallback videoOnClickCallback;

    public VideoAdapter() {
        videos = new ArrayList<>();
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int pos) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_video, null);
        return new VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoHolder viewHolder, int pos) {
        viewHolder.bindViews(videos.get(pos));
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void addVideos(List<MovieVideo> videoList) {
        videos.clear();
        videos.addAll(videoList);
        notifyDataSetChanged();
    }

    public void setVideoOnClickCallback(VideoOnClickCallback callback) {
        videoOnClickCallback = callback;
    }

    class VideoHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.video_thumbnail)
        ImageView videoThumbnail;

        @BindView(R.id.video_title)
        TextView videoTitle;

        public VideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int pos = getAdapterPosition();
                    MovieVideo video = videos.get(pos);
                    if (video != null && videoOnClickCallback != null) {
                        videoOnClickCallback.openVideo(video);
                    }
                }
            });
        }

        public void bindViews(MovieVideo video) {
            Picasso.with(itemView.getContext())
                    .load(video.getImageVideoUrl())
                    .into(videoThumbnail);
            videoTitle.setText(video.getName());
        }

    }
}
