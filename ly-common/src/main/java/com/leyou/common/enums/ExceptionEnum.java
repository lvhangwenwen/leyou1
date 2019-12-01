package com.leyou.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {


    BRAND_NOT_FOUND(404,"品牌不存在"),
    CATEGORY_NOT_FOUND(404,"商品分类没有查到"),
    SPEC_NOT_FOUND(404,"商品规格组没有查到"),
    BRAND_SAVE_ERROR(500,"品牌增加失败"),
    UPLOAD_ERROR(500,"文件上传失败"),
    FILE_TYPE_ERROR(400,"文件类型错误"),
    GOODS_SKU_NOT_FOUND(404,"商品sku不存在"),
    GOODS_STOCK_NOT_FOUND(404,"商品库存不存在"),
    SPEC_PARAM_NOT_FOUND(404,"商品规格参数不存在"),
    GOODS_NOT_FOUND(404,"商品不存在" ),
    GOODS_DETAIL_NOT_FOUND(404,"商品详情不存在" ),
    GOOD_SAVE_ERROR(500,"商品保存失败" ),

    GOOD_UPDATE_ERROR(500,"商品更新失败"),
    GOOD_ID_CANNOT_BE_NULL(400,"商品id不能为空"),
    INVALID_USER_DATA_TYPE(400,"用户数据类型无效"),
    INVALID_VERIFY_CODE(400,"验证码无效"),
    INVALID_USERNAME_PASSWORD(400,"用户名或密码错误"),
    CREATE_TOKEN_ERROR(500,"用户凭证生成失败"),
    UN_AUTHREAED(403,"未授权该服务"),
    CART_NOT_FOUND(404,"购物车为空"),
    CREATE_ORDER_ERROR(500,"创建订单失败"),
    STOCK_NOT_ENOUGH(500,"库存不足"),
    ORDER_NOT_FOUND(404,"订单不存在"),
    ORDER_DETAIL_NOT_FOUND(404,"订单详情不存在"),
    ORDER_STATUS_NOT_FOUND(404,"订单状态不存在"),
    WXPAY_CREATE_ERROR(500,"微信支付失败"),
    ORDER_STATUS_ERROR(400,"订单状态异常"),
    INVALID_SIGN(400,"签名异常"),
    MONEY_ERROR(400,"金额异常"),
    UPDATE_ORDER_STATUS_ERROR(500,"更新订单状态失败"),
    ;


    private int     code;
    private String name;
}
