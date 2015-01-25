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
        mContext = context;
        mListener = listener;

        setPaginationEnabled(false);
    }

    // Customize the layout by overriding getItemView
    @Override
    public View getItemView(final Painting painting, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.feed_item, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.smallHeart = (ImageView) convertView.findViewById(R.id.heart_image);
            viewHolder.largeHeart = (ImageView) convertView.findViewById(R.id.heart_in_painting);
            viewHolder.menuImage = (ImageView) convertView.findViewById(R.id.feed_item_menu);
            viewHolder.paintingImage = (ParseImageView) convertView.findViewById(R.id.painting_image);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.username_view);
            viewHolder.likesText = (TextView) convertView.findViewById(R.id.likes_text);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description_view);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        View.OnClickListener likesClickedListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePaintingLike(painting, viewHolder, false);
            }
        };

        viewHolder.likesText.setOnClickListener(likesClickedListener);
        viewHolder.smallHeart.setOnClickListener(likesClickedListener);

        viewHolder.menuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.menuClicked(painting);
            }
        });

        viewHolder.largeHeart.setVisibility(View.INVISIBLE);
        viewHolder.largeHeart.clearAnimation();

        viewHolder.paintingImage.setOnClickListener(new View.OnClickListener() {
            boolean clicked = false;
            long lastClickedTime = -1;
            @Override
            public void onClick(View v) {
                // Checks for a double-tap
                long time = System.currentTimeMillis();
                if (clicked && time - lastClickedTime < 300) {
                    clicked = false;
                    togglePaintingLike(painting, viewHolder, true);
                } else {
                    clicked = true;
                    lastClickedTime = time;
                }
            }
        });

        // Add and download the image
        ParseFile imageFile = painting.getPhotoFile();
        if (imageFile != null) {
            viewHolder.paintingImage.setMinimumHeight(
                    mContext.getResources().getDisplayMetrics().widthPixels);
            viewHolder.paintingImage.setParseFile(imageFile);
            viewHolder.paintingImage.loadInBackground();
        } else {
            System.out.println("");
        }

        if (painting.isLiked()) {
            viewHolder.smallHeart.setImageBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.drawable.heart_selected));
        } else {
            viewHolder.smallHeart.setImageBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.drawable.heart_deselected));
        }

        if (ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().getUsername().equals(painting.getUsername())) {
            viewHolder.menuImage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.menuImage.setVisibility(View.INVISIBLE);
        }

        viewHolder.userName.setText(painting.getUsername());
        viewHolder.likesText.setText(painting.getLikesCount() + " ");
        viewHolder.description.setText(painting.getDescription());

        return convertView;
    }

    private void togglePaintingLike(final Painting p, final ViewHolder v, boolean forceLiked) {

        if (!p.isLiked()) {
            p.likePainting();
            animateLike(v.largeHeart);
            v.smallHeart.setImageBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.drawable.heart_selected));
        } else if (forceLiked) {
            animateLike(v.largeHeart);
            v.smallHeart.setImageBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.drawable.heart_selected));
        } else {
            p.unlikePainting();
            v.smallHeart.setImageBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.drawable.heart_deselected));
        }

        p.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                v.likesText.setText(p.getLikesCount() + "");
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

    static class ViewHolder {
        ImageView smallHeart;
        ImageView largeHeart;
        ImageView menuImage;
        ParseImageView paintingImage;

        TextView userName;
        TextView likesText;
        TextView description;

        public ViewHolder () {

        }
    }
}

