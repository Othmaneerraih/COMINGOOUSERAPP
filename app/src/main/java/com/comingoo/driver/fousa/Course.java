package com.comingoo.driver.fousa;


public class Course {

    private String startAddress;
    private String endAddress;

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getPreWaitTime() {
        return preWaitTime;
    }

    public void setPreWaitTime(String preWaitTime) {
        this.preWaitTime = preWaitTime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(String waitTime) {
        this.waitTime = waitTime;
    }

    public Course(String startAddress, String endAddress, String client, String date, String distance, String driver, String preWaitTime, String price, String waitTime) {

        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.client = client;
        this.date = date;
        this.distance = distance;
        this.driver = driver;
        this.preWaitTime = preWaitTime;
        this.price = price;
        this.waitTime = waitTime;
    }

    private String client;
    private String date;
    private String distance;
    private String driver;
    private String preWaitTime;
    private String price;
    private String waitTime;

    public Course() {

    }

}
