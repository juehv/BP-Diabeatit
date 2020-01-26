package info.nightscout.androidaps.plugins.insulin.prediction;

public interface InterpolationMethod<TX, TY> {
    void addDatapoint(TX x, TY y);

    TY getValueAt(TX x);
}
