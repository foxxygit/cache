package com.foxxy.git.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetUtils {
    public static final String LOCAL_IP = getHostAddress();

    private static String getHostAddress() {
        String address = "127.0.0.1";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface ni = en.nextElement();
                Enumeration<InetAddress> ads = ni.getInetAddresses();
                while (ads.hasMoreElements()) {
                    InetAddress ip = ads.nextElement();
                    if (!ip.isLoopbackAddress() && ip.isSiteLocalAddress()) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
        }
        return address;
    }
}
