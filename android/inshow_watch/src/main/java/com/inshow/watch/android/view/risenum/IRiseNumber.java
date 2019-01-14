package com.inshow.watch.android.view.risenum;

public interface IRiseNumber {
    public void start();
    public RiseNumberTextView withNumber(float number);
    public RiseNumberTextView withNumber(int number);
    public RiseNumberTextView withString(int number);
    public RiseNumberTextView setDuration(long duration);
    public void setOnEnd(RiseNumberTextView.EndListener callback);
}
