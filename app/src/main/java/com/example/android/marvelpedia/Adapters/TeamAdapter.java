package com.example.android.marvelpedia.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Thumbnail;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.ViewHolder> {

    private final String portrait_uncanny = "portrait_uncanny";
    private Context mContext;
    private List<Character> mCharacters;


    public TeamAdapter(Context context, List<Character> characters) {
        this.mContext = context;
        this.mCharacters = characters;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_team_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Character currentTeamMember = mCharacters.get(i);
        ImageView memberImage = viewHolder.mMemberImage;

        Thumbnail currentMemberThumbnail = currentTeamMember.getThumbnail();
        //Log.v("TeamAdapter", memberImageLink);
        if (!(currentMemberThumbnail.getPath().isEmpty())) {
            memberImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String thumbnailExtension = currentMemberThumbnail.getExtension();
            String thumbnailPath = currentMemberThumbnail.getPath();
            //String combinedPath = thumbnailPath + "." + thumbnailExtension;
            String combinedPath = thumbnailPath + "/" + portrait_uncanny + "." + thumbnailExtension;

            Picasso.get().load(combinedPath).into(memberImage);
        }

    }

    public void setTeamData(List<Character> teamMembers) {
        this.mCharacters = teamMembers;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCharacters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final ImageView mMemberImage;
        //public final TextView mComicName;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mMemberImage = view.findViewById(R.id.team_member_image);
            mView.setOnClickListener(this);
            //mComicName = view.findViewById(R.id.comic_title);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Character character = mCharacters.get(adapterPosition);
        }
    }
}