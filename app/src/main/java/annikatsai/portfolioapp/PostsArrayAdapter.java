package annikatsai.portfolioapp;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import annikatsai.portfolioapp.Models.Post;

// Taking the Post objects and turning them into Views displayed in the list
public class PostsArrayAdapter extends RecyclerView.Adapter<PostsArrayAdapter.ViewHolder> {

    private PostsArrayAdapterCallback callback;

    // View lookup cache
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public ImageView ivImage;
        public ImageButton btnPopupMenu;
        public TextView tvDate;

        public ViewHolder(final View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            btnPopupMenu = (ImageButton) itemView.findViewById(R.id.btnPopupMenu);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }

    private List<Post> mPosts;
    private Context mContext;

    // Constructor
    public PostsArrayAdapter(Context context, List<Post> posts) {
        mPosts = posts;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    public void setCallback(PostsArrayAdapterCallback callback) {
        this.callback = callback;
    }

    public interface PostsArrayAdapterCallback {
        public void launchEditPost(int position);

        public void deletePost(int position);
    }

    @Override
    public PostsArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.item_post, parent, false);

        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final PostsArrayAdapter.ViewHolder holder, final int position) {
        final Post post = mPosts.get(position);

        holder.tvTitle.setText(post.getTitle());
        holder.tvDate.setText(post.getDate());
        holder.ivImage.setImageResource(0);
        holder.btnPopupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creating instance of popupMenu and inflating view
                PopupMenu popup = new PopupMenu(getContext(), holder.btnPopupMenu);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                // Registering with click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
//                        Toast.makeText(getContext(), "You clicked: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        if (item.getTitle().toString().equals("Edit")) {
                            callback.launchEditPost(position);
                        } else if (item.getTitle().toString().equals("Delete")) {
                            callback.deletePost(position);
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        if(post.photoUrl != null && !(post.photoUrl.isEmpty())){
            // Load image
            if (post.photoType.equals("vertical")) {
                if (post.realOrientation.equals("left")) {
                    Picasso.with(getContext()).load(post.photoUrl).fit().centerCrop().into(holder.ivImage);
                } else if (post.realOrientation.equals("original")) {
                    // test orientation display stretch image
                    Picasso.with(getContext()).load(post.photoUrl).fit().centerCrop().rotate(90f).into(holder.ivImage);
                } else if (post.realOrientation.equals("right")) {
                    Picasso.with(getContext()).load(post.photoUrl).fit().centerCrop().rotate(180f).into(holder.ivImage);

                } else { // upsideDown
                    // test orientation display stretch image
                    Picasso.with(getContext()).load(post.photoUrl).fit().centerCrop().rotate(270f).into(holder.ivImage);
                }
            }
            if (post.photoType.equals("horizontal")) {
                if (post.realOrientation.equals("original")) {
                    Picasso.with(getContext()).load(post.photoUrl).fit().centerCrop().into(holder.ivImage);
                } else if (post.realOrientation.equals("right")) {
                    // test orientation display stretch image
                    Picasso.with(getContext()).load(post.photoUrl).fit().centerCrop().rotate(90f).into(holder.ivImage);
                } else if (post.realOrientation.equals("upsideDown")) {
                    Picasso.with(getContext()).load(post.photoUrl).fit().centerCrop().rotate(180f).into(holder.ivImage);
                } else { // left
                    // test orientation display stretch image
                    Picasso.with(getContext()).load(post.photoUrl).fit().centerCrop().rotate(270f).into(holder.ivImage);
                }
            }
        } else {
            Picasso.with(getContext()).load(R.drawable.default_photo).fit().centerCrop().into(holder.ivImage);
        }
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }
}
