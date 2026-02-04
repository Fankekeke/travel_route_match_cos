package com.fank.f1k2.business.controller;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fank.f1k2.business.entity.DiscountInfo;
import com.fank.f1k2.business.entity.OrderInfo;
import com.fank.f1k2.business.service.IDiscountInfoService;
import com.fank.f1k2.business.service.IOrderInfoService;
import com.fank.f1k2.business.service.IPayService;
import com.fank.f1k2.business.service.IUserInfoService;
import com.fank.f1k2.common.domain.AlipayBean;
import com.fank.f1k2.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/business/pay")
public class PayController {

    @Autowired
    private IPayService payService;

    @Autowired
    private IOrderInfoService orderInfoService;

    @Autowired
    private IDiscountInfoService discountInfoService;

    @Autowired
    private IUserInfoService userInfoService;



    /**
     * 阿里支付
     *
     * @param subject
     * @param body
     * @return
     * @throws AlipayApiException
     */
    @PostMapping(value = "/alipay")
    public R alipay(String outTradeNo, String subject, String totalAmount, String body, Integer discountId) throws AlipayApiException {
        OrderInfo orderInfo = orderInfoService.getOne(Wrappers.<OrderInfo>lambdaQuery().eq(OrderInfo::getCode, outTradeNo));
        if (discountId != null && discountId != -1) {
            orderInfo.setDiscountId(discountId);
        }
        orderInfo.setAfterOrderPrice(new BigDecimal(totalAmount));
        orderInfoService.updateById(orderInfo);

        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOut_trade_no(outTradeNo);
        alipayBean.setSubject(subject);
        alipayBean.setTotal_amount(totalAmount);
        alipayBean.setBody(body);
        String result = payService.aliPay(alipayBean);
        return R.ok(result);
    }

}
