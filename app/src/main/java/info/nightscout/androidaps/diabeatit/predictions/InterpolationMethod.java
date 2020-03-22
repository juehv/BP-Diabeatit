package info.nightscout.androidaps.diabeatit.predictions;

/** An method of interpolation */
public interface InterpolationMethod<TX, TY> {
	/** 
	 * Add a datapoint to the set.  These need to be added in ascending order before the
	 * {@link #getValueAt} method may be called
	 * 
	 * @param		x		The value along the input-axis of the interpolated function
	 * @param		y		The value of the function at the point {@code x}
	 */
    void addDatapoint(TX x, TY y);

	/**
	 * Get the interpolated value at the given position.
	 * 
	 * @param	x		The position of the value to be interpolated
	 * @return			The interpolated value at the position
	 */
    TY getValueAt(TX x);
}
