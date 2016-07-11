package annikatsai.portfolioapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import annikatsai.portfolioapp.Models.Post;

// Taking the Post objects and turning them into Views displayed in the list
public class PostsArrayAdapter extends ArrayAdapter<Post> {

    public PostsArrayAdapter(Context context, List<Post> posts) {
        super(context, 0, posts);
    }

    // Override and set up custom template
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. Get the post
        Post post = getItem(position);

        // 2. Find or inflate the template
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_post, parent, false);
        }

        // 3. Find the subviews to fill with data in the template
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        TextView tvPreviewStory = (TextView) convertView.findViewById(R.id.tvPreviewStory);
        ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);

        // 4. Populate data into the subviews - double check this is correct
        tvTitle.setText(post.getTitle());
        tvPreviewStory.setText(post.getBody());
        ivImage.setImageResource(android.R.color.transparent); //clear out the old image for a recycled view
        //Figure out how to populate the images correctly - camera roll
        //Picasso.with(getContext()).load(post.getUser().getProfileImageUrl()).into(ivImage);

        // 5. Return the view to be inserted into the list
        return convertView;
    }
}
