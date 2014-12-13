package edu.cmsc434.paintdrip.paintdripprototype.Feed;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.cmsc434.paintdrip.paintdripprototype.R;

/**
 * Created by jamesbwills on 12/12/14.
 */
public class PaintingListAdapter extends BaseAdapter  {

    List<Painting> mItems;
    Context mContext;

    static class ViewHolder {
        public TextView username;
        public TextView likes;
        public ImageView image;
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


        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            rowView = inflater.inflate(R.layout.feed_item, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.likes = (TextView) rowView.findViewById(R.id.likes_text);
            viewHolder.username = (TextView) rowView.findViewById(R.id.username_view);
            viewHolder.image = (ImageView) rowView.findViewById(R.id.painting_image);
            rowView.setTag(viewHolder);
        }

        Log.i("JB", "Getting view at " + position);
        Painting painting = (Painting) getItem(position);
        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.username.setText(painting.getUsername());
        holder.likes.setText(painting.getLikesCount() + "");
        holder.image.setImageBitmap(painting.getImage());

        return rowView;
    }


}
