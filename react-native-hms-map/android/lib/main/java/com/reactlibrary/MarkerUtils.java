package com.reactlibrary;

import android.animation.ValueAnimator;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.huawei.hms.maps.Projection;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;


public class MarkerUtils {


    /**
     * Animate (move) a marker to a new position on the map
     * @param marker the marker that will animated (moved)
     * @param latitude new LATITUDE of the coordinate (location) where the marker will move
     * @param longitude new longitude of the coordinate (location) where the marker will move
     */
    public static void animateMarkerToCoordinate(final Marker marker ,
                                                double latitude,
                                                double longitude
                                                ){
    //  animateMarkerToCoordinate(marker, new LatLng(LATITUDE, longitude), new LatLngInterpolator.LinearFixed());
        //animateMarkerToCoordinate2(marker,new LatLng(LATITUDE,longitude));
         animateMarker(marker,  latitude , longitude    );
    }


    /**
     * Animate the change in position of the marker from its original coordiante, to a new coordinate
     * with a linear interpolator, for 1 second
     * @param marker the marker that will animated (moved)
     * @param latitude new LATITUDE of the coordinate (location) where the marker will move
     * @param longitude new longitude of the coordinate (location) where the marker will move
     */
    public static void animateMarker(final Marker marker,
                                     double latitude,
                                     double longitude ) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();

        LatLng toPosition = new LatLng(latitude,longitude  );

        final LatLng startLatLng = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);

        final long duration = Constants.MARKER_ANIMATION_DURATION;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double longitude = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double latitude = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(latitude, longitude));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    /**
     * Method to animate marker to destination location
     * @param destination destination location (must contain bearing attribute, to ensure
     *                    marker rotation will work correctly)
     * @param marker marker to be animated
     */
    public static void animateMarkerToCoordinate2(Marker marker,LatLng destination ) {
        if (marker != null) {
            LatLng startPosition = marker.getPosition();
            LatLng endPosition = new LatLng(destination.latitude, destination.longitude);

            LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(1000); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                     //   marker.setRotation(computeRotation(v, startRotation, destination.getBearing());
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });

            valueAnimator.start();
        }
    }



    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double latitude = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double longitude = lngDelta * fraction + a.longitude;
                return new LatLng(latitude, longitude);
            }
        }
    }


  private static void animateMarkerToCoordinate(final Marker marker,
                                         final LatLng finalPosition,
                                         final LatLngInterpolator latLngInterpolator) {
        final LatLng startPosition = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 500;
        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;
            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);
                marker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));
                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 5ms later.
                    handler.postDelayed(this, 5);
                }
            }
        });
    }
}
