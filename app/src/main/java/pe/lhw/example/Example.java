package pe.lhw.example;

/**
 * Created by lhw48 on 2016-09-09.
 */
public class Example {
    protected int idata;
    protected float fdata;
    protected double ddata;

    public Example() {
        idata = 0;
        fdata = 0.f;
        ddata = 0.d;
    }

    public Example(Example otherExample) {
        idata = otherExample.idata;
        fdata = otherExample.fdata;
        ddata = otherExample.ddata;
    }

    public Example(int idata, float fdata, double ddata) {
        this.idata = idata;
        this.fdata = fdata;
        this.ddata = ddata;
    }

    public static Example fromReceivedData(int idata, float fdata, double ddata) {
        return new Example(idata, fdata, ddata);
    }

    public int getIdata() {
        return idata;
    }

    public void setIdata(int idata) {
        this.idata = idata;
    }

    public float getFdata() {
        return fdata;
    }

    public void setFdata(float fdata) {
        this.fdata = fdata;
    }

    public double getDdata() {
        return ddata;
    }

    public void setDdata(double ddata) {
        this.ddata = ddata;
    }
}
