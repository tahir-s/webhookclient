package com.attribe.webhookclient.pojo.thirdparty.model;

public class Request {
    private String cellNumber;
    private String command;
    private String id;

    // Constructor
    public Request() {
    }

    public Request(String cellNumber, String command, String id) {
        this.cellNumber = cellNumber;
        this.command = command;
        this.id = id;
    }

    // Getters and Setters
    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Request{" +
                "cellNumber='" + cellNumber + '\'' +
                ", command='" + command + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
