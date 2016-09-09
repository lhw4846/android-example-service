package pe.lhw.example.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lhw48 on 2016-09-09.
 */
public class StartRMData implements Parcelable {
    private Integer uniqueId;

    public StartRMData(Integer uniqueId, String intentActionForCallback) {
        this.uniqueId = uniqueId;
    }

    public StartRMData(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Integer getUniqueId() {
        return uniqueId;
    }

    public static final Creator<StartRMData> CREATOR = new Creator<StartRMData>() {
        @Override
        public StartRMData createFromParcel(Parcel in) {
            return new StartRMData(in);
        }

        @Override
        public StartRMData[] newArray(int size) {
            return new StartRMData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(uniqueId);
    }

    protected StartRMData(Parcel in) {
        uniqueId = in.readInt();
    }
}
