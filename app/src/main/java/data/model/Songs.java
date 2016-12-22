package data.model;

/**
 * Created by tuanbg on 12/22/2016.
 */

public class Songs {
    private String mLinkImage;
    private String mTitle;

    public Songs(String linkImage, String title) {
        mLinkImage = linkImage;
        mTitle = title;
    }

    public String getLinkImage() {
        return mLinkImage;
    }

    public String getTitle() {
        return mTitle;
    }
}
