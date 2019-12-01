package com.leyou.order.service;

import com.leyou.auth.entiy.UserInfo;
import com.leyou.common.dto.CartDTO;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.pojo.Sku;
import com.leyou.order.DTO.AddressDTO;
import com.leyou.order.DTO.OrderDTO;
import com.leyou.order.client.AddressClient;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.interceptors.UserInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.utils.PayHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper detailMapper;
    @Autowired
    private OrderStatusMapper statusMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private PayHelper payHelper;

    @Transactional
    public Long createOrder(OrderDTO orderDTO) {
        //组织订单数据

        //新增订单
        Order order = new Order();
        //订单编号、基本信息

        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDTO.getPaymentType());

        //用户信息
        UserInfo user = UserInterceptor.getUser();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);
        //收货人地址
        //获取收货人
        AddressDTO addr = AddressClient.findById(orderDTO.getAddressId());

        order.setReceiver(addr.getName());
        order.setReceiverCity(addr.getCity());
        order.setReceiverAddress(addr.getAddress());
        order.setReceiverDistrict(addr.getDistrict());
        order.setReceiverMobile(addr.getPhone());
        order.setReceiverState(addr.getState());
        order.setReceiverZip(addr.getZipCode());
        //金额

        //把cartDTO转为一个map,key是sku的id,值是num
        Map<Long, Integer> numMap = orderDTO.getCarts().stream()
                .collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));

        //获取所有skuid
        Set<Long> ids = numMap.keySet();

        //根据id查询sku
        List<Sku> skus = goodsClient.querySkuByIds(new ArrayList<>(ids));

        //准备orderdetail集合
        List<OrderDetail> details=new ArrayList<>();
        long totalPay=0L;
        for (Sku sku : skus) {
            //计算总价
            totalPay+=sku.getPrice() * numMap.get(sku.getId());

            //封装orderdetail
            OrderDetail detail = new OrderDetail();
            detail.setImage(StringUtils.substringBefore(sku.getImages(),","));
            detail.setNum(numMap.get(sku.getId()));
            detail.setOrderId(orderId);
            detail.setOwnSpec(sku.getOwnSpec());
            detail.setPrice(sku.getPrice());
            detail.setSkuId(sku.getId());
            detail.setTitle(sku.getTitle());
            details.add(detail);
        }

        order.setTotalPay(totalPay);
        //实付金额
        order.setActualPay(totalPay+order.getPostFee()-0);

        //orde写入数据库
       int count= orderMapper.insertSelective(order);
       if (count!=1){
           log.error("[订单服务] 创建订单失败 订单编号：{}",orderId);
           throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
       }
        //新增订单详情

        count=detailMapper.insertList(details);
       if (count!=details.size()){
           log.error("[订单服务] 创建订单失败 订单编号：{}",orderId);
           throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
       }
        //新增订单状态
        OrderStatus orderStatus = new OrderStatus();
       orderStatus.setCreateTime(order.getCreateTime());
       orderStatus.setOrderId(orderId);
       orderStatus.setStatus(OrderStatusEnum.UN_PAY.value());

       count=statusMapper.insertSelective(orderStatus);
        if (count!=1){
            log.error("[订单服务] 创建订单失败 订单编号：{}",orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }
        //减库存
        List<CartDTO>cartDTOS=orderDTO.getCarts();
        goodsClient.decreaseStock(cartDTOS);
        return orderId;
    }

    public Order queryOrderById(Long id) {
        Order order = orderMapper.selectByPrimaryKey(id);

        if (order==null){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        //查订单详情
        OrderDetail detail = new OrderDetail();
        detail.setOrderId(order.getOrderId());
        List<OrderDetail> details = detailMapper.select(detail);
        if (CollectionUtils.isEmpty(details)){
            throw new LyException(ExceptionEnum.ORDER_DETAIL_NOT_FOUND);
        }
        order.setOrderDetails(details);
        //查订单状态
        OrderStatus orderStatus = statusMapper.selectByPrimaryKey(id);
        if (orderStatus==null){
            throw new LyException(ExceptionEnum.ORDER_STATUS_NOT_FOUND);
        }
        order.setOrderStatus(orderStatus);
        return order;
    }

    public String createUrl(Long orderId) {

        //商品金额
        Order order = queryOrderById(orderId);
        //判断订单状态
        Integer status = order.getOrderStatus().getStatus();
        if (status!=OrderStatusEnum.UN_PAY.value()){

            //状态异常
            throw new LyException(ExceptionEnum.ORDER_STATUS_ERROR);
        }

        Long actualPay = 1l;//order.getActualPay();

        //商品描述

        OrderDetail detail = order.getOrderDetails().get(0);
        String desc=detail.getTitle();
        return payHelper.createOrder(orderId,actualPay,desc);
    }

    public void handleNotify(Map<String, String> result) {
        //数据校验

        payHelper.isSuccess(result);

        //校验签名
        payHelper.isValidSign(result);

        //校验金额
        String totalFeeStr = result.get("total_fee");
        String tradeNo = result.get("out_trade_no");

        if (StringUtils.isEmpty(totalFeeStr)||StringUtils.isEmpty(tradeNo)){
            throw new LyException(ExceptionEnum.MONEY_ERROR);
        }

        long totalFee=Long.valueOf(totalFeeStr);

        Long orderId = Long.valueOf(tradeNo);
        Order order = orderMapper.selectByPrimaryKey(orderId);

        if (totalFee!=1){
            //金额不符
            throw new LyException(ExceptionEnum.MONEY_ERROR);
        }

        //修改订单状态
        OrderStatus status = new OrderStatus();
        status.setStatus(OrderStatusEnum.PAYED.value());
        status.setOrderId(orderId);
        status.setPaymentTime(new Date());
        int count=statusMapper.updateByPrimaryKeySelective(status);
        if (count!=1){
            throw new LyException(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
        }

        log.info("[订单回调] 订单支付成功");

    }
}
