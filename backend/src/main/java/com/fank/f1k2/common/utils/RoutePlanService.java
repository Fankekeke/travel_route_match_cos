package com.fank.f1k2.common.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路线规划服务
 */
@Service
public class RoutePlanService {

    private static final String ROUTE_PLAN_API_URL = "https://lbs.xiaojukeji.com/api/v2/routeplanv3";

    /**
     * 获取路线规划
     *
     * @param startLongitude 起始经度
     * @param startLatitude  起始纬度
     * @param endLongitude   终点经度
     * @param endLatitude    终点纬度
     * @return 路线规划结果
     */
    public String getRoutePlan(Double startLongitude, Double startLatitude,
                               Double endLongitude, Double endLatitude) {
        // 构建请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("key", "fk04e01085b5de84cd79a80707604c795e");

        // 构建packs数组
        List<Map<String, Object>> packs = new ArrayList<>();

        // 第一个路线规划请求 - 不需要polyline
        Map<String, Object> pack1 = new HashMap<>();
        pack1.put("origin", startLongitude + "," + startLatitude);
        pack1.put("destination", endLongitude + "," + endLatitude);
        pack1.put("departure_time", System.currentTimeMillis() / 1000 + 3600); // 一小时后
        pack1.put("routeplan_type", "1");
        pack1.put("need_polyline", false);
        pack1.put("eta_strategy", 0);
        pack1.put("strategy", "2");
        packs.add(pack1);

        // 第二个路线规划请求 - 需要polyline
        Map<String, Object> pack2 = new HashMap<>();
        pack2.put("origin", startLongitude + "," + startLatitude);
        pack2.put("destination", endLongitude + "," + endLatitude);
        pack2.put("departure_time", System.currentTimeMillis() / 1000 + 3600); // 一小时后
        pack2.put("routeplan_type", "1");
        pack2.put("need_polyline", true);
        packs.add(pack2);

        params.put("packs", packs);

        try {
            // 发送GET请求
            String response = HttpUtil.get(ROUTE_PLAN_API_URL, params);

            // 解析响应JSON
            JSONObject jsonResponse = JSONUtil.parseObj(response);

            // 获取results数组
            JSONArray results = jsonResponse.getJSONArray("results");

            if (results != null && results.size() > 1) {
                // 获取第二个元素（索引为1）
                JSONObject secondResult = results.getJSONObject(1);
                // 获取其中的routes数据
                Object routes = secondResult.get("routes");

                // 返回routes部分的JSON字符串
                return JSONUtil.toJsonStr(routes);
            } else {
                throw new RuntimeException("API响应中没有足够的results数据或不存在第二个元素");
            }
        } catch (Exception e) {
            // 记录错误日志
            e.printStackTrace();
            throw new RuntimeException("路线规划API调用失败: " + e.getMessage());
        }
    }
}