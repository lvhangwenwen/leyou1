package com.leyou.sms.mq;


import com.aliyuncs.exceptions.ClientException;
import com.leyou.common.utils.JsonUtils;

import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
@Slf4j
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {

    @Autowired
    private SmsProperties prop;

    @Autowired
    private SmsUtils smsUtils;

    @RabbitListener(bindings = @QueueBinding(
            //队列名字
            value = @Queue(name="sms.verify.code.queue",durable = "true"),
            exchange = @Exchange(name="ly.sms.exchange",type = ExchangeTypes.TOPIC),
            key = "sms.verify.code"
    ))
    public void listenInsertOrUpdate(Map<String,String> msg){
        if (CollectionUtils.isEmpty(msg)){
            return;
        }
        String phone = msg.remove("phone");

        if (StringUtils.isEmpty(phone)){
            return;
        }
        //处理消息,对索引库新增或者修改
        try {
            smsUtils.sendSms(phone, JsonUtils.serialize(msg),prop.getSignName(),prop.getVerifyCodeTemplate());

            log.info("[短信服务]，发送短信验证码，手机号{}",phone);
        } catch (ClientException e) {
            e.printStackTrace();

        }
    }

}
