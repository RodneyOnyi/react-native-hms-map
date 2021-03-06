package com.reactlibrary;

import com.huawei.hms.maps.model.LatLng;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;
import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.toDegrees;

public interface LatLngInterpolator {
    LatLng interpolate(float fraction, LatLng a, LatLng b);

    class Spherical implements LatLngInterpolator {
        /* From github.com/googlemaps/android-maps-utils */

        @Override
        public LatLng interpolate(float fraction, LatLng from, LatLng to) {
            // http://en.wikipedia.org/wiki/Slerp
            double fromLat = toRadians(from.latitude);
            double fromLng = toRadians(from.longitude);
            double toLat = toRadians(to.latitude);
            double toLng = toRadians(to.longitude);
            double cosFromLat = cos(fromLat);
            double cosToLat = cos(toLat);
            // Computes Spherical interpolation coefficients.
            double angle = computeAngleBetween(fromLat, fromLng, toLat, toLng);
            double sinAngle = sin(angle);
            if (sinAngle < 1E-6) {
                return from;
            }
            double a = sin((1 - fraction) * angle) / sinAngle;
            double b = sin(fraction * angle) / sinAngle;
            // Converts from polar to vector and interpolate.
            double x = a * cosFromLat * cos(fromLng) + b * cosToLat * cos(toLng);
            double y = a * cosFromLat * sin(fromLng) + b * cosToLat * sin(toLng);
            double z = a * sin(fromLat) + b * sin(toLat);
            // Converts interpolated vector back to polar.
            double latitude = atan2(z, sqrt(x * x + y * y));
            double longitude = atan2(y, x);
            return new LatLng(toDegrees(latitude), toDegrees(longitude));
        }

        private double computeAngleBetween(double fromLat, double fromLng, double toLat, double toLng) {
            // Haversine's formula
            double dLat = fromLat - toLat;
            double dLng = fromLng - toLng;
            return 2 * asin(sqrt(pow(sin(dLat / 2), 2) +
                    cos(fromLat) * cos(toLat) * pow(sin(dLng / 2), 2)));
        }
    }
}