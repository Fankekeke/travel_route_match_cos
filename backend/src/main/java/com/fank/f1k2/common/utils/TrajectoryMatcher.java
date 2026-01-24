package com.fank.f1k2.common.utils;

import cn.hutool.json.JSONArray;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.math.BigDecimal;
import java.util.List;

/**
 * 轨迹匹配器
 *
 * @author FanK
 * @date 2025-05-12 10:33:19
 */
public class TrajectoryMatcher {

    /**
     * 定义预设路径（目标路线）
     *
     * @return 结果
     * @throws Exception 异常
     */
    private static LineString createTargetPath(JSONArray roadCoordinates) throws Exception {
        Coordinate[] coords = new Coordinate[roadCoordinates.size()];
        for (int i = 0; i < roadCoordinates.size(); i++) {
            JSONArray pointItem = roadCoordinates.getJSONArray(i);
            coords[i] = new Coordinate(pointItem.get(0, double.class), pointItem.get(1, double.class));
        }
        return new GeometryFactory().createLineString(coords);
    }

    /**
     * 构建实际轨迹（用户上报的轨迹点）
     *
     * @return 结果
     */
//    private static LineString createActualTrajectory(List<HistoryPoint> historyPointList) {
//        Coordinate[] coords = new Coordinate[historyPointList.size()];
//        for (int i = 0; i < historyPointList.size(); i++) {
//            HistoryPoint point = historyPointList.get(i);
//            coords[i] = new Coordinate(point.getLongitude(), point.getLatitude());
//        }
//        return new GeometryFactory().createLineString(coords);
//    }

    /**
     * 计算匹配率
     *
     * @param actual         实际轨迹
     * @param target         作业道路
     * @param bufferDistance 缓冲距离阿斯顿
     * @return 结果
     */
    public static double calculateMatchRate(LineString actual, LineString target, double bufferDistance) {
        if (actual.getLength() == 0) {
            return 0.0;
        }

        // 为预设路径创建缓冲区
        Geometry bufferedTarget = BufferOp.bufferOp(target, bufferDistance);

        // 计算实际轨迹与缓冲区的交集
        Geometry intersection = actual.intersection(bufferedTarget);

        // 计算匹配率
        double matchedLength = intersection.getLength();
        double totalLength = actual.getLength();
        double totalFixLength = target.getLength();
        System.out.printf("交集:" + intersection.getLength() + " 实际轨迹:" + totalLength);
        return (matchedLength / totalFixLength) * 100;
    }

    /**
     * 新增点匹配计算方式
     *
     * @param actual         实际
     * @param target         对比
     * @param bufferDistance 缓冲区
     * @return 结果
     */
    public static double calculatePointMatchRate(LineString actual, LineString target, double bufferDistance) {
        int matchedPoints = 0;
        Coordinate[] coords = target.getCoordinates();

        Geometry bufferedTarget = actual.buffer(bufferDistance);
        for (Coordinate coord : coords) {
            Point point = new GeometryFactory().createPoint(coord);
            if (bufferedTarget.contains(point)) {
                matchedPoints++;
            }
        }
        return (double) matchedPoints / coords.length * 100;
    }

    public static LineString convertToUtm(LineString original) throws Exception {
        CoordinateReferenceSystem sourceCrs = DefaultGeographicCRS.WGS84;
        CoordinateReferenceSystem targetCrs = CRS.decode("EPSG:32650");

        MathTransform transform = CRS.findMathTransform(sourceCrs, targetCrs, true);
        Geometry transformed = JTS.transform(original, transform);
        return (LineString) transformed;
    }

    /**
     * 点匹配计算作业是否完成
     *
     * @param historyPointList 历史轨迹
     * @param roadCoordinates  道路坐标
     * @return 匹配率
     */
    public static BigDecimal calculatePointMatchRate(JSONArray historyPointList, JSONArray roadCoordinates) {
        // 设置目标路线
        try {
            LineString targetPath = createTargetPath(roadCoordinates);
            LineString actualTrajectory = createTargetPath(historyPointList);

            // 1. 坐标转换
            LineString utmTarget = convertToUtm(targetPath);
            LineString utmActual = convertToUtm(actualTrajectory);

            double bufferDistance = 12;

            // 2. 线匹配计算
            // double matchRate = calculateMatchRate(utmActual, utmTarget, bufferDistance);

            // 3. 点匹配验证
            double pointMatchRate = calculatePointMatchRate(utmActual, utmTarget, bufferDistance);

//            System.out.printf("线匹配率: %.2f%%, 点匹配率: %.2f%%\n", matchRate, pointMatchRate);
//            System.out.printf("点匹配率:%.2f%%", pointMatchRate);
            return BigDecimal.valueOf(pointMatchRate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }
}
