package com.baontq.mob201.until;

import android.util.Log;

import com.baontq.mob201.model.News;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ParseNews {
    public static final String TAG = "Parse progress";
    private News news;
    private String content;

    public List<News> parseNews(InputStream inputStream) {

        List<News> list = new ArrayList<>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = parser.getEventType();
                String tagName = parser.getName();
                Log.d(TAG, "Tag name =  " + tagName +
                        ", Độ sâu của thẻ = " + parser.getDepth() + ", event = " + eventType);
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("item")) {
                            news = new News();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        content = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (news != null) {
                            if (tagName.equalsIgnoreCase("item")) {
                                list.add(news);
                            } else if (tagName.equalsIgnoreCase("title")) {
                                news.setTitle(content);
                            } else if (tagName.equalsIgnoreCase("description")) {
                                String[] parts = content.split("</br>");
                               if (parts.length !=1) {
                                   news.setDescription(parts[1]);
                                   news.setPreviewImage(parts[0].split("src=\"")[1].replace("\" ></a>", ""));
                               }else {
                                   news.setDescription(parts[0]);
                               }
                            } else if (tagName.equalsIgnoreCase("link")) {
                                news.setLink(content);
                            }
                        }
                        break;
                    default:
                        Log.d(TAG, "eventType: " + eventType + ", tag = " + tagName);
                        break;
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
