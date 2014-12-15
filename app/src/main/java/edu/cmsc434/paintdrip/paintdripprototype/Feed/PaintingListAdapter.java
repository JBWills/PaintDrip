package edu.cmsc434.paintdrip.paintdripprototype.Feed;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.cmsc434.paintdrip.paintdripprototype.R;

public class PaintingListAdapter extends ParseQueryAdapter<Painting>  {
    private static final int FRIENDS_FRAGMENT = 0;
    private static final int GLOBAL_FRAGMENT = 1;
    private static final int ME_FRAGMENT = 2;
    List<Painting> mItems;
    Context mContext;

    static class ViewHolder {
        public TextView username;
        public TextView likes;
        public ImageView heartImage;
        public ImageView heartImage2;
        public TextView description;
        public ParseImageView image;
    }

    public PaintingListAdapter(Context context) {

        super(context, new ParseQueryAdapter.QueryFactory<Painting>() {
            public ParseQuery<Painting> create() {
                ParseQuery query = new ParseQuery("Painting");

                return query;
            }
        });

        mItems = new ArrayList<Painting>();
        mContext = context;
    }

    public PaintingListAdapter(Context context, final int feedID) {

        super(context, new ParseQueryAdapter.QueryFactory<Painting>() {
            public ParseQuery<Painting> create() {
                ParseQuery query = new ParseQuery("Painting");
                if(feedID == ME_FRAGMENT) {
                    query.whereEqualTo("authorId", ParseUser.getCurrentUser().getObjectId());
                    query.orderByDescending("createdAt");
                    return query;
                }else {
                    query.orderByDescending("createdAt");
                    return query;
                }
            }
        });

        mItems = new ArrayList<Painting>();
        mContext = context;
    }

    // Customize the layout by overriding getItemView
    @Override
    public View getItemView(Painting painting, View rowView, ViewGroup parent) {
        // reuse views
        if (rowView == null) {
            Log.i("JB", "Creating View");
            LayoutInflater inflater = LayoutInflater.from(mContext);
            rowView = inflater.inflate(R.layout.feed_item, parent, false);
            final ViewHolder viewHolder = new ViewHolder();
            View.OnClickListener likesClickedListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("JB", "Clicked likes");
                    animateLike(viewHolder.heartImage2);
                }
            };
            viewHolder.heartImage = (ImageView) rowView.findViewById(R.id.heart_image);
            viewHolder.heartImage.setOnClickListener(likesClickedListener);
            viewHolder.likes = (TextView) rowView.findViewById(R.id.likes_text);
            viewHolder.likes.setOnClickListener(likesClickedListener);
            viewHolder.username = (TextView) rowView.findViewById(R.id.username_view);
            viewHolder.description = (TextView) rowView.findViewById(R.id.description_view);
            viewHolder.heartImage2 = (ImageView) rowView.findViewById(R.id.heart_in_painting);
            viewHolder.heartImage2.setVisibility(View.INVISIBLE);
            viewHolder.heartImage2.clearAnimation();
            viewHolder.image = (ParseImageView) rowView.findViewById(R.id.painting_image);
            viewHolder.image.setOnClickListener(new View.OnClickListener() {
                boolean clicked = false;
                long lastClickedTime = -1;
                @Override
                public void onClick(View v) {
                    // User double-clicked the view
                    long time = System.currentTimeMillis();
                    if (clicked && time - lastClickedTime < 300) {
                        clicked = false;
                        animateLike(viewHolder.heartImage2);
                    } else {
                        clicked = true;
                        lastClickedTime = time;
                    }
                }
            });
            rowView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.username.setText(painting.getUsername());
        holder.likes.setText(painting.getLikesCount() + " ");
        holder.image.setImageBitmap(painting.getImage());
        holder.description.setText(painting.getDescription());
        return rowView;
    }

    private void animateLike(ImageView heart) {
        Animation a = new AlphaAnimation(1.0f, 0.0f);
        a.setDuration(1000);

        Animation b = new ScaleAnimation(
                0.8f, 1f, // Start and end values for the X axis scaling
                0.8f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        b.setDuration(1000);
        AnimationSet anims = new AnimationSet(true);
        anims.addAnimation(a);
        anims.addAnimation(b);

        heart.clearAnimation();
        heart.startAnimation(anims);
    }
}

