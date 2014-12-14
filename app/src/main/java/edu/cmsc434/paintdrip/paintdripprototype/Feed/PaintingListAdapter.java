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

import java.util.ArrayList;
import java.util.List;

import edu.cmsc434.paintdrip.paintdripprototype.R;

/**
 * Created by jamesbwills on 12/12/14.
 */
public class PaintingListAdapter extends BaseAdapter {

    List<Painting> mItems;
    Context mContext;

    static class ViewHolder {
        public TextView username;
        public TextView likes;
        public ImageView image;
        public ImageView heartImage;
        public ImageView heartImage2;
        public TextView description;
    }

    public PaintingListAdapter(Context c) {
        mItems = new ArrayList<Painting>();
        mContext = c;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(Painting painting) {
        mItems.add(painting);
    }

    public void setList(List<Painting> paintings) {
        mItems.clear();
        for (Painting f : paintings) {
            mItems.add(f);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // reuse views
        if (convertView == null) {
            Log.i("JB", "Creating View");
            LayoutInflater inflater = LayoutInflater.from(mContext);
            final View rowView = inflater.inflate(R.layout.feed_item, parent, false);

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

            viewHolder.image = (ImageView) rowView.findViewById(R.id.painting_image);
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

            convertView = rowView;
        }

        Log.i("JB", "Getting view at " + position);
        Painting painting = (Painting) getItem(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();

        holder.username.setText(painting.getUsername());
        holder.likes.setText(painting.getLikes() + " ");
        holder.image.setImageBitmap(painting.getImage());
        holder.description.setText(painting.getDescription());

        return convertView;
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

