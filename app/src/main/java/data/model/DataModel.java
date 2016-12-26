package data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nhahv on 12/21/2016.
 * <></>
 */
public class DataModel implements Parcelable {
    private String mName;
    private String mImageUrl;
    private String mPathRender;
    private String mUrlMp4;

    public DataModel(String name, String image, String pathRender) {
        this.mName = name;
        this.mImageUrl = image;
        this.mPathRender = pathRender;
    }

    protected DataModel(Parcel in) {
        mName = in.readString();
        mImageUrl = in.readString();
        mPathRender = in.readString();
        mUrlMp4 = in.readString();
    }

    public static final Creator<DataModel> CREATOR = new Creator<DataModel>() {
        @Override
        public DataModel createFromParcel(Parcel in) {
            return new DataModel(in);
        }

        @Override
        public DataModel[] newArray(int size) {
            return new DataModel[size];
        }
    };

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getImage() {
        return mImageUrl;
    }

    public void setImage(String image) {
        this.mImageUrl = image;
    }

    public String getPathRender() {
        return mPathRender;
    }

    public void setPathRender(String pathRender) {
        this.mPathRender = pathRender;
    }

    public String getUrlMp4() {
        return mUrlMp4;
    }

    public void setUrlMp4(String urlMp4) {
        this.mUrlMp4 = urlMp4;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeString(mImageUrl);
        parcel.writeString(mPathRender);
        parcel.writeString(mUrlMp4);
    }
}
