package hawaiiappbuilders.omniversapp.utils;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtil {

    // https://regexr.com/4u4ud
    public static boolean isYoutubeUrl(String url) {
        Pattern pattern = Pattern.compile("(\\/|%3D|v=)([0-9A-z-_]{11})([%#?&]|$)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            // YouTube video ID consists of 11 characters
            // and for each character there are 64 possible choices
            // (uppercase letters, numbers, lowercase letters, - and _).
            // That means that there are unique IDs that can be made.
            String videoID = matcher.group(2);
            if (videoID != null) {
                return videoID.length() == 11;
            }
        }
        return false;
    }

    public static String getVideoIdFromUrl(String url) {
        Pattern pattern = Pattern.compile("(\\/|%3D|v=)([0-9A-z-_]{11})([%#?&]|$)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            // YouTube video ID consists of 11 characters
            // and for each character there are 64 possible choices
            // (uppercase letters, numbers, lowercase letters, - and _).
            // That means that there are unique IDs that can be made.
            return matcher.group(2) != null && !matcher.group().isEmpty() ? matcher.group(2) : null;
        }
        return null;
    }

    // Method to extract video ID from YouTube URL
    public static String extractVideoId(String url) {
        String videoId = url.substring(url.length() - 11);
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2Fvideos%2F|youtu.be%2F|\\/v%2F)[^#\\&\\?\\n]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract video id.
        if (matcher.find()) {
            videoId = matcher.group();
        }
        return videoId;
    }

    public static boolean isValidYouTubeUrl(String url) {
        String pattern = "^(http(s)?://)?((w){3}.)?youtu(be|.be)?(\\.com)?/.+";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(url);
        return matcher.matches();
    }
}
