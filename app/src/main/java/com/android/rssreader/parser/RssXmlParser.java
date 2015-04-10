package com.android.rssreader.parser;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mariam on 08.04.2015.
 */
public class RssXmlParser {

    private static final String ns = null;
    private static final String START_TAG = "rss";
    private static final String ITEM_TAG = "item";
    private static final String CHANNEL_TAG = "channel";
    private static final String TAG_TITLE = "title";
    private static final String TAG_LINK = "link";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_IMAGE = "media:thumbnail";
    private static final String END_TAG = "rss";

    public ArrayList parse(InputStream in) throws XmlPullParserException, IOException{
        try{
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);

        }
        finally{
            in.close();
        }
    }

    private ArrayList readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList entries = new ArrayList();
        String name;
        parser.require(XmlPullParser.START_TAG, ns, START_TAG);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            name = parser.getName();
            if (name.equals(CHANNEL_TAG)) {
                entries.addAll(readChannelEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private ArrayList readChannelEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList entries = new ArrayList();
        String name;
        parser.require(XmlPullParser.START_TAG, ns, CHANNEL_TAG);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            name = parser.getName();
            if (name.equals(ITEM_TAG)) {

                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    public static class Item implements Parcelable{
        public final String title;
        public final String link;
        public final String image;
        public final String category;

        private Item(String title, String link, String image, String category) {
            this.title = title;
            this.category = category;
            this.image = image;
            this.link = link;
        }

        public Item(Parcel in){
            String[] data = new String[4];

            in.readStringArray(data);
            this.title = data[0];
            this.category = data[1];
            this.image = data[2];
            this.link = data[3];
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeStringArray(new String[] {this.title,
                    this.category,
                    this.image,
                this.link});
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public Item createFromParcel(Parcel in) {
                return new Item(in);
            }

            public Item[] newArray(int size) {
                return new Item[size];
            }
        };
    }

    private Item readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, ITEM_TAG);
        String title = null;
        String link = null;
        String image = null;
        String category = null;
        int n=0;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TAG_TITLE)) {
                title = readTitle(parser);}
             else if (name.equals(TAG_CATEGORY)) {
                category = readText(parser);
            } else if (name.equals(TAG_LINK)) {
                link = readText(parser);
            }
            else if (name.equals(TAG_IMAGE)&&n==0) {
                image = readThumbnail(parser);
                n++;
            }else {
                skip(parser);
            }
        }
        return new Item(title,link,image,category);
    }

    private String readThumbnail(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_IMAGE);
        String thumbnailUrl = parser.getAttributeValue(null, "url");
        parser.nextTag();
        return thumbnailUrl;
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_TITLE);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, TAG_TITLE);
        return title;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
