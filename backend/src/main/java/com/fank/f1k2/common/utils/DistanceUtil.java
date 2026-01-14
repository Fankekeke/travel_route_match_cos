package com.fank.f1k2.common.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class DistanceUtil {

    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371.393;

    public final static String MAX_LON = "maxLon";
    public final static String MIN_LON = "minLon";
    public final static String MAX_LAT = "maxLat";
    public final static String MIN_LAT = "minLat";

    /**
     * 计算两坐标之间的距离 km
     *
     * @param userLat
     * @param userLng
     * @param venueLat
     * @param venueLng
     * @return
     */
    public static double calculateDistanceInKilometer(double userLat, double userLng, double venueLat, double venueLng) {
        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        BigDecimal bd = BigDecimal.valueOf(AVERAGE_RADIUS_OF_EARTH_KM * c);
        return bd.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 获取 距离坐标 一定距离的 范围 坐标极值
     *
     * @param lon
     * @param lat
     * @param distance km
     * @return
     */
    public static Map<String, Double> getRange(double lon, double lat, double distance) {
        double dlng = 2 * Math.asin(Math.sin(distance / (2 * AVERAGE_RADIUS_OF_EARTH_KM)) / Math.cos(lat * (Math.PI / 180)));
        dlng = degrees(dlng);
        double dlat = distance / AVERAGE_RADIUS_OF_EARTH_KM;
        dlat = degrees(dlat);
        Map<String, Double> map = new HashMap<>(6);
        map.put(MAX_LON, lon + dlng);
        map.put(MIN_LON, lon - dlng);
        map.put(MAX_LAT, lat + dlat);
        map.put(MIN_LAT, lat - dlat);
        return map;
    }

    private static double degrees(double d) {
        return d * (180 / Math.PI);
    }


    public static void main(String[] args) {

        Map<String, Double> map = getRange(121.438853, 31.221332, 10);
        System.out.println(map.toString());

        double km = calculateDistanceInKilometer(31.221332, 121.438853, 31.221332, 121.438853);
        System.out.println(km);
    }
}
