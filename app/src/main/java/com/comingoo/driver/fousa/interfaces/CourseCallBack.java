package com.comingoo.driver.fousa.interfaces;

import com.google.android.gms.maps.model.LatLng;

public interface CourseCallBack {
    void callbackCourseInfo(String courseId,String clientId,String clientName,
                            String clientImageUri, String clientPhoneNumber,
                            String clientlastCourse, String startAddress,
                            String destAddress, String courseSta,
                            String clientTotalRide, String clientLastRideDate, String preWTime, LatLng clientDestLatLng);
}
