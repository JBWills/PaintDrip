package edu.cmsc434.paintdrip.paintdripprototype.Feed;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by jamesbwills on 12/12/14.
 */
public class PaintingListAdapter extends ParseQueryAdapter<Painting>  {
    private static final int FRIENDS_FRAGMENT = 0;
    private static final int GLOBAL_FRAGMENT = 1;
    private static final int ME_FRAGMENT = 2;

    List<Painting> mItems;
    Context mContext;

    static class ViewHolder {
        public TextView username;
        public TextView likes;
        //public ImageView image;
        public ParseImageView image;
    }

    public PaintingListAdapter(Context context) {

        super(context, new ParseQueryAdapter.QueryFactory<Painting>() {
            public ParseQuery<Painting> create() {
                // Here we can configure a ParseQuery to display
                // only top-rated meals.
                ParseQuery query = new ParseQuery("Painting");
                //query.whereContainedIn("rating", Arrays.asList("5", "4"));
                //query.orderByDescending("rating");
                return query;
            }
        });

        mItems = new ArrayList<Painting>();
        mContext = context;
    }

    public PaintingListAdapter(Context context, final int feedID) {

        super(context, new ParseQueryAdapter.QueryFactory<Painting>() {
            public ParseQuery<Painting> create() {
                // Here we can configure a ParseQuery to display
                // only top-rated meals.
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

    /*
    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Painting getItem(int position) {
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
    }*/

    // Customize the layout by overriding getItemView
    @Override
    public View getItemView(Painting painting, View rowView, ViewGroup parent) {
        if (rowView == null) {
            //v = View.inflate(getContext(), R.layout.urgent_item, null);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            rowView = inflater.inflate(R.layout.feed_item, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.likes = (TextView) rowView.findViewById(R.id.likes_text);
            viewHolder.username = (TextView) rowView.findViewById(R.id.username_view);
            viewHolder.image = (ParseImageView) rowView.findViewById(R.id.painting_image);

            rowView.setTag(viewHolder);
        }

        super.getItemView(painting,rowView,parent);

        ViewHolder holder = (ViewHolder) rowView.getTag();

        // Add and download the image
        ParseFile imageFile = painting.getPhotoFile();
        if (imageFile != null) {
            ParseImageView imgFileView =  (ParseImageView) rowView.findViewById(R.id.painting_image);
            imgFileView.setParseFile(imageFile);
            imgFileView.loadInBackground();
        }else {
            System.out.println("");
        }

        holder.username.setText(painting.getUsername());
        holder.likes.setText(painting.getLikesCount() + "");
        holder.image.setImageBitmap(painting.getImage());

        return rowView;
    }
}
