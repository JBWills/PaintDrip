package edu.cmsc434.paintdrip.paintdripprototype.Feed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
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
import com.parse.SaveCallback;

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
    FeedItemListener mListener;

    public PaintingListAdapter(Context context, final int feedID, FeedItemListener listener) {

        super(context, new ParseQueryAdapter.QueryFactory<Painting>() {
            public ParseQuery<Painting> create() {
                ParseQuery query = new ParseQuery("Painting");
                if (feedID == ME_FRAGMENT) {
                    query.whereEqualTo("authorId", ParseUser.getCurrentUser().getObjectId());
                    query.orderByDescending("createdAt");
                    return query;
                } else {
                    query.orderByDescending("createdAt");
                    return query;
                }
            }
        });

        mItems = new ArrayList<Painting>();
        mContext = context;
        mListener = listener;

        setPaginationEnabled(false);
    }

    // Customize the layout by overriding getItemView
    @Override
    public View getItemView(final Painting painting, View rowView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        rowView = inflater.inflate(R.layout.feed_item, parent, false);
        final View row = rowView;
        View.OnClickListener likesClickedListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePaintingLike(painting, row, false);
            }
        };
        ImageView heartImage = (ImageView) rowView.findViewById(R.id.heart_image);
        heartImage.setOnClickListener(likesClickedListener);

        ImageView menuImage = (ImageView) rowView.findViewById(R.id.feed_item_menu);
        menuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.menuClicked(painting);
            }
        });

        TextView likesView = (TextView) rowView.findViewById(R.id.likes_text);
        likesView.setOnClickListener(likesClickedListener);

        TextView username = (TextView) rowView.findViewById(R.id.username_view);

        TextView description = (TextView) rowView.findViewById(R.id.description_view);

        ImageView heartImage2 = (ImageView) rowView.findViewById(R.id.heart_in_painting);
        heartImage2.setVisibility(View.INVISIBLE);
        heartImage2.clearAnimation();

        ImageView paintingImage = (ParseImageView) rowView.findViewById(R.id.painting_image);
        paintingImage.setOnClickListener(new View.OnClickListener() {
            boolean clicked = false;
            long lastClickedTime = -1;
            @Override
            public void onClick(View v) {
                // User double-clicked the view
                long time = System.currentTimeMillis();
                if (clicked && time - lastClickedTime < 300) {
                    clicked = false;
                    togglePaintingLike(painting, row, true);
                } else {
                    clicked = true;
                    lastClickedTime = time;
                }
            }
        });

        // Add and download the image
        ParseFile imageFile = painting.getPhotoFile();
        if (imageFile != null) {
            ParseImageView imgFileView =  (ParseImageView) rowView.findViewById(R.id.painting_image);
            imgFileView.setMinimumHeight(mContext.getResources().getDisplayMetrics().widthPixels);
            imgFileView.setParseFile(imageFile);
            imgFileView.loadInBackground();
        }else {
            System.out.println("");
        }

        if (painting.isLiked()) {
            heartImage.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.heart_selected));
        } else {
            heartImage.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.heart_deselected));
        }

        if (ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().getUsername().equals(painting.getUsername())) {
            menuImage.setVisibility(View.VISIBLE);
        } else {
            menuImage.setVisibility(View.INVISIBLE);
        }

        username.setText(painting.getUsername());
        likesView.setText(painting.getLikesCount() + " ");
        description.setText(painting.getDescription());

        return rowView;
    }

    private void togglePaintingLike(final Painting p, final View rowView, boolean forceLiked) {
        ImageView heartView1 = (ImageView) rowView.findViewById(R.id.heart_image);
        final TextView likesView = (TextView) rowView.findViewById(R.id.likes_text);

        if (!p.isLiked()) {
            ImageView heartView2 = (ImageView) rowView.findViewById(R.id.heart_in_painting);
            p.likePainting();
            animateLike(heartView2);
            heartView1.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.heart_selected));
        } else if (forceLiked) {
            ImageView heartView2 = (ImageView) rowView.findViewById(R.id.heart_in_painting);
            animateLike(heartView2);
            heartView1.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.heart_selected));
        } else {
            p.unlikePainting();
            heartView1.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.heart_deselected));
        }

        p.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                likesView.setText(p.getLikesCount() + "");
            }
        });
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

