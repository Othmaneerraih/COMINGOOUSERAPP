package com.comingoo.driver.fousa.interfaces;

public interface CourseCallBack {
    void callbackCourseInfo(String courseId,String clientId,String clientName,
                            String clientImageUri, String clientPhoneNumber,
                            String clientlastCourse, String startAddress, String destAddress, String courseSta);
}
