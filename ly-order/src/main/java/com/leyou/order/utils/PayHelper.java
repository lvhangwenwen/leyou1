package com.leyou.order.utils;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.order.config.PayConfig;
import com.leyou.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class PayHelper {

    @Autowired
    private WXPay wxPay;

    @Autowired
    private PayConfig payConfig;


    public String createOrder(Long orderId,Long totalPay,String desc) {
        try{
            Map<String,String>data=new HashMap<>();
            data.put("body",desc);
            data.put("out_trade_no",orderId.toString());
            data.put("total_fee",totalPay.toString());
            data.put("spbill_create_ip","127.0.0.1");
            data.put("notify_url", payConfig.getNotifyUrl());
            data.put("trade_type","NATIVE");

            Map<String,String>result=wxPay.unifiedOrder(data);

            for (Map.Entry<String, String> entry : result.entrySet()) {
                String key = entry.getKey();
                System.out.println(key+(key.length()>+8? "\t:" : "\t\t:")+entry.getValue());
            }
            System.out.println("----------------------");
            isSuccess(result);


            String url=result.get("code_url");
            return url;
        }catch (Exception e){
            log.error("[微信付款] 服务出现异常,原因{}",e.getMessage());
            return null;
        }
    }

    public void isSuccess(Map<String, String> result) {
        //通信标识
        String code = result.get("return_code");
        if (code.equals(WXPayConstants.FAIL)){

            log.error("[微信下单] 通信失败，原因：{}",result.get("return_msg"));
            throw new LyException(ExceptionEnum.WXPAY_CREATE_ERROR);
        }

        String resultCode = result.get("result_code");
        if (resultCode.equals(WXPayConstants.FAIL)){

            log.error("[微信下单] 通信失败，原因：{}",result.get("err_code"));
            throw new LyException(ExceptionEnum.WXPAY_CREATE_ERROR);
        }
    }

    public void isValidSign(Map<String, String> data) {
        //重更新生成签名，和传过来的比较
        try {
            String sign1 = WXPayUtil.generateSignature(data, payConfig.getKey(), WXPayConstants.SignType.HMACSHA256);
            String sign2 = WXPayUtil.generateSignature(data, payConfig.getKey(), WXPayConstants.SignType.MD5);

            String sign = data.get("sign");
            if (!StringUtils.equals(sign,sign1)&& !StringUtils.equals(sign,sign2)) {

                //抛出异常
                throw new LyException(ExceptionEnum.INVALID_SIGN);

            }
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.INVALID_SIGN);

        }
    }
}