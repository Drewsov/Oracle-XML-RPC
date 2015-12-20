package com.nnetworks.xmlrpc;

public class ResourceBundle {
    public ResourceBundle() {
    }

    public static String getString(String pname) {
        return BaseAPIHandler.uProps.getProperty(pname);
    }

}
