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
import android.widget.Toast;

import java.util.List;

import annikatsai.portfolioapp.Models.Post;

// Taking the Post objects and turning them into Views displayed in the list
public class PostsArrayAdapter extends RecyclerView.Adapter<PostsArrayAdapter.ViewHolder> {

    private PostsArrayAdapterCallback callback;

    // View lookup cache
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvTitle;
        public ImageView ivImage;
        public ImageButton btnPopupMenu;

        public ViewHolder(final View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            btnPopupMenu = (ImageButton) itemView.findViewById(R.id.btnPopupMenu);
        }
    }

    private List<Post> mPosts;
    private Context mContext;

    // Constructor
    public PostsArrayAdapter(Context context, List<Post> posts){
//        super(context, android.R.layout.simple_list_item_1, posts);
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
        public int getPostPosition(Post post);
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
    public void onBindViewHolder(final PostsArrayAdapter.ViewHolder holder, int position) {
        final Post post = mPosts.get(position);

        holder.tvTitle.setText(post.getTitle());
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
                        Toast.makeText(getContext(), "You clicked: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        if (item.getTitle().toString().equals("Edit")) {
                            callback.launchEditPost(callback.getPostPosition(post));
                        } else if (item.getTitle().toString().equals("Delete")) {
                            callback.deletePost(callback.getPostPosition(post));
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    //    // Override and set up custom template
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        // Get the post
//        final Post post = getItem(position);
//
//        // Check if an existing view is being reused, otherwise inflate the view
//        final ViewHolder viewHolder; // view lookup cache stored in tag
//        if(convertView == null){
//            viewHolder = new ViewHolder();
//            LayoutInflater inflater = LayoutInflater.from(getContext());
//            convertView = inflater.inflate(R.layout.item_post, parent, false);
//
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        //clear out image from convertView
//        viewHolder.ivImage.setImageResource(0);
//        viewHolder.btnPopupMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Creating instance of popupMenu and inflating view
//                PopupMenu popup = new PopupMenu(getContext(), viewHolder.btnPopupMenu);
//                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
//
//                // Registering with click listener
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        Toast.makeText(getContext(), "You clicked: " + item.getTitle(), Toast.LENGTH_SHORT).show();
//                        if (item.getTitle().toString().equals("Edit")) {
//                            callback.launchEditPost(getPosition(post));
//                        } else if (item.getTitle().toString().equals("Delete")) {
//                            callback.deletePost(getPosition(post));
//                        }
//                        return true;
//                    }
//                });
//                popup.show();
//
//            }
//        });
//        // Populate the data into the template view using the data object
//        viewHolder.tvTitle.setText(post.getTitle());
//        //Figure out how to populate the images correctly - camera roll
//        //loads images and placeholders if needed
//        //Picasso.with(getContext()).load(post.getImagePath()).fit().centerCrop().placeholder(R.drawable.default_poster).into(viewHolder.ivImage);
//
//        // 5. Return the view to be inserted into the list
//        return convertView;
//    }
}
