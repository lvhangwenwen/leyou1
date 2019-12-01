package com.leyou.cart.service;

import com.leyou.auth.entiy.UserInfo;
import com.leyou.cart.interceptor.UserInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX="cart:uid";

    public void addCart(Cart cart) {
        //获取登陆用户
        UserInfo user = UserInterceptor.getUser();
        //KEY
        String key = KEY_PREFIX+user.getId();

        String hashKey = cart.getSkuId().toString();
        Integer num=cart.getNum();

        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);

        //判断当前购物车是否存在
        if (operation.hasKey(hashKey)) {
            //存在
            String json = operation.get(hashKey).toString();
            cart = JsonUtils.parse(json, Cart.class);
            cart.setNum(cart.getNum() + num);
            //写会

        }
        operation.put(hashKey,JsonUtils.serialize(cart));
    }

    public List<Cart> queryCartList() {
        //获取登陆用户
        UserInfo user = UserInterceptor.getUser();
        //KEY
        String key = KEY_PREFIX+user.getId();
        if (!redisTemplate.hasKey(key)){
            throw  new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        //获取登陆用户的所有购物车数据
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);

        List<Cart> carts = operation.values().stream().map(o -> JsonUtils.parse(o.toString(), Cart.class))
                .collect(Collectors.toList());
        return carts;
    }

    public void updateCartNum(Long skuId, Integer num) {
        //获取登陆用户
        UserInfo user = UserInterceptor.getUser();
        //KEY
        String key = KEY_PREFIX+user.getId();
        //hashkey
        String hashkey=skuId.toString();
        //获取操作
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
        //判断是否存在
        if (!operation.hasKey(skuId.toString())) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        Cart cart = JsonUtils.parse(operation.get(skuId.toString()).toString(), Cart.class);
        cart.setNum(num);

        //写会redis
        operation.put(hashkey,JsonUtils.serialize(cart));

    }

    public void deleteCart(Long skuId) {

        //获取登陆用户
        UserInfo user = UserInterceptor.getUser();
        //KEY
        String key = KEY_PREFIX+user.getId();

        //删除
        redisTemplate.opsForHash().delete(key,skuId.toString());
    }
}
