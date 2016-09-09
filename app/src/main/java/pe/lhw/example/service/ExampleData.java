package pe.lhw.example.service;

import android.os.Parcel;
import android.os.Parcelable;

import pe.lhw.example.Example;

/**
 * Created by lhw on 2016-09-09.
 */
public class ExampleData extends Example implements Parcelable {

    public ExampleData(Example example) {
        super(example);
    }

    public static final Creator<ExampleData> CREATOR = new Creator<ExampleData>() {
        @Override
        public ExampleData createFromParcel(Parcel in) {
            return new ExampleData(in);
        }

        @Override
        public ExampleData[] newArray(int size) {
            return new ExampleData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(idata);
        out.writeFloat(fdata);
        out.writeDouble(ddata);
    }

    protected ExampleData(Parcel in) {
        idata = in.readInt();
        fdata = in.readFloat();
        ddata = in.readDouble();
    }
}
