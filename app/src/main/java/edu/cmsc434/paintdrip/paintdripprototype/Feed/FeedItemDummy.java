package edu.cmsc434.paintdrip.paintdripprototype.Feed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.cmsc434.paintdrip.paintdripprototype.R;

/**
 * Created by jamesbwills on 12/12/14.
 */
public class FeedItemDummy {

    private List<Painting> friendsDummyList;
    private List<Painting> globalDummyList;
    private List<Painting> meDummyList;
    private Context mContext;

    private static final String[] NAMES = {
            "Charlie",
            "Chris",
            "Ali",
            "Nadeem",
            "Olivia",
            "Nayeem",
            "Scot",
            "Joe",
            "Andinet",
            "friesadt13",
            "maybejb",
            "sweet_conor",
            "Obama",
            "Louie"
    };

    private static final String[] DESCRIPTIONS = {
            "House",
            "Pet bird",
            "water bottle",
            "Xbox",
            "TV",
            "Trampoline",
            "Smile",
            "Face",
            "Car",
            "Jetski",
            "WWE",
            "Basketball",
            "Macbook",
            "Long walk"
    };

    private static final Bitmap[] BITMAPS = new Bitmap[5];

    public FeedItemDummy(Context c) {
        mContext = c;

        BITMAPS[0] = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ex1);
        BITMAPS[1] = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ex2);
        BITMAPS[2] = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ex3);
        BITMAPS[3] = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ex4);
        BITMAPS[4] = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ex5);
    }

    public List<Painting> getGlobalOrFeedItems(int size) {
        List<Painting> a = new ArrayList<Painting>();

        for (int i = 0; i < size; i ++) {
            a.add(getRandomUser());
        }

        return a;
    }

    public List<Painting> getMeItems(int size) {
        List<Painting> a = new ArrayList<Painting>();

        Random r = new Random();
        String name = NAMES[r.nextInt(NAMES.length)];
        for (int i = 0; i < size; i ++) {
            a.add(getRandomUser(name));
        }

        return a;
    }

    private Painting getRandomUser() {
        Random r = new Random();
        String name = NAMES[r.nextInt(NAMES.length)];
        String description = DESCRIPTIONS[r.nextInt(DESCRIPTIONS.length)];
        int likes = r.nextInt(150);

        Bitmap painting = BITMAPS[r.nextInt(BITMAPS.length)];

        return new Painting(name, description, likes, painting);
    }

    private Painting getRandomUser(String name) {
        Painting item = getRandomUser();
        item.setUsername(name);
        return item;
    }
}
