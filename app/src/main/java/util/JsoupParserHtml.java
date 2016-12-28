package util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import data.model.ShortStory;
import data.model.Song;

/**
 * Created by Nhahv on 12/20/2016.
 * <></>
 */
public class JsoupParserHtml {
    private static final String TAG = "JsoupParserHtml";
    private static final String URL = "https://learnenglishkids.britishcouncil.org";
    private static final String URL_SONGS = "/en/songs";
    private static final String URL_SHORT_STORIES = "/en/short-stories";
    private static final String IMAGE = "img";
    private static final String SRC = "src";
    private static final String A = "a";
    private static final String HREF = "abs:href";
    private static final String DIV_ID_SONG = "div#block-views-magazine-glossary-block-1";
    private static final String DIV_SELECT_LIST = "div.view-content>.views-row";
    private static final String DIV_SELECT_AUDIO = ".viddler-auto-embed";
    private static final String ATTRIBUTE_VIDEO_ID = "data-video-id";
    private static final String HTTPS = "https";

    public static List<Song> parseSongs() throws IOException {
        List<Song> listSongs = new ArrayList<>();
        Document doc = Jsoup.connect(URL + URL_SONGS).followRedirects(true).get();
        Element idSong = doc.select(DIV_ID_SONG).first();
        Elements listSong = idSong.select(DIV_SELECT_LIST);
        if (listSong == null) return listSongs;
        Song song;
        for (Element item : listSong) {
            //get image url
            String imageUrl = item.select(IMAGE).first().absUrl(SRC);
            //get name
            Element nameElement = item.select(A).last();
            String name = nameElement.text();
            // get url render to get video url
            String url = nameElement.attr(HREF);
            url = (url.contains(HTTPS) ? url : (URL + url));
            song = new Song(name, imageUrl, url);
            listSongs.add(song);
        }
        return listSongs;
    }

    public static List<ShortStory> parseShortStories() throws IOException {
        List<ShortStory> stories = new ArrayList<>();
        Document doc = Jsoup.connect(URL + URL_SHORT_STORIES).followRedirects(true).get();
        Element idSong = doc.select(DIV_ID_SONG).first();
        Elements listSong = idSong.select(DIV_SELECT_LIST);
        if (listSong == null) return stories;
        ShortStory shortStory;
        for (Element item : listSong) {
            // get image url
            String imageUrl = item.select(IMAGE).first().absUrl(SRC);
            // get name
            Element nameElement = item.select(A).last();
            String name = nameElement.text();
            // get url render get video url
            String url = nameElement.attr(HREF);
            url = (url.contains(HTTPS) ? url : (URL + url));
            shortStory = new ShortStory(name, imageUrl, url);
            stories.add(shortStory);
        }
        return stories;
    }

    public static String parseUrlVideo(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return (doc.select(DIV_SELECT_AUDIO).first().attr(ATTRIBUTE_VIDEO_ID));
    }
}
