package info.nightscout.androidaps.diabeatit.predictions;

public interface InterpolationMethod<TX, TY> {
    void addDatapoint(TX x, TY y);

    TY getValueAt(TX x);
}
