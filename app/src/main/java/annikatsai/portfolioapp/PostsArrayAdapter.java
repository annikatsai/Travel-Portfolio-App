package annikatsai.portfolioapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import annikatsai.portfolioapp.Models.Post;

// Taking the Post objects and turning them into Views displayed in the list
public class PostsArrayAdapter extends ArrayAdapter<Post> {

    // View lookup cache
    private static class ViewHolder {
        TextView tvTitle;
        TextView tvPreviewStory;
        ImageView ivImage;
    }

    // Constructor
    public PostsArrayAdapter(Context context, ArrayList<Post> posts){
        //super(context, R.layout.item_post, posts);
        super(context, android.R.layout.simple_list_item_1, posts);
    }

    /*public PostsArrayAdapter(Context context, List<Post> posts) {
        super(context, 0, posts);
    }*/

    // Override and set up custom template
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the post
        Post post = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_post, parent, false);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvPreviewStory = (TextView) convertView.findViewById(R.id.tvPreviewStory);
            viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //clear out image from convertView
        viewHolder.ivImage.setImageResource(0);

        // Populate the data into the template view using the data object
        viewHolder.tvTitle.setText(post.getTitle());
        viewHolder.tvPreviewStory.setText(post.getBody());
        //Figure out how to populate the images correctly - camera roll
        //loads images and placeholders if needed
        //Picasso.with(getContext()).load(post.getImagePath()).fit().centerCrop().placeholder(R.drawable.default_poster).into(viewHolder.ivImage);

        // 5. Return the view to be inserted into the list
        return convertView;
    }
}
