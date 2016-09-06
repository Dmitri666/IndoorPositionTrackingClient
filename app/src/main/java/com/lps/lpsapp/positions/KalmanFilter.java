package com.lps.lpsapp.positions;

/**
 * Created by dle on 05.09.2016.
 */
public class KalmanFilter {

    double R = 0.01; // noise power desirable Process noise
    double Q = 20; // noise power estimated Measurement noise

    double A = 1; //State vector
    double B = 0; // Control vector
    double C = 1; // Measurement vector
    double cov;
    double x; // estimated signal without noise


    double filter(double z,double u) {

        if (this.x == 0) {
            this.x = (1 / this.C) * z;
            this.cov = (1 / this.C) * this.Q * (1 / this.C);
        }
        else {

            // Compute prediction
            double predX = (this.A * this.x) + (this.B * u);
            double predCov = ((this.A * this.cov) * this.A) + this.R;

            // Kalman gain
            double K = predCov * this.C * (1 / ((this.C * predCov * this.C) + this.Q));

            // Correction
            this.x = predX + K * (z - (this.C * predX));
            this.cov = predCov - (K * this.C * predCov);
        }

        return this.x;
    }
}
