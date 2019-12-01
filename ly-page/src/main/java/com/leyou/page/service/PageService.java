package com.leyou.page.service;

import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class PageService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private SpecificationClient specClient;

    @Autowired
    private TemplateEngine templateEngine;

    public Map<String, Object> loadModel(Long spuId) {

        Map<String, Object> model=new HashMap<>();

        //查spu
        Spu spu = goodsClient.querySpuById(spuId);
        //skus
        List<Sku> skus = spu.getSkus();

        //详情
        SpuDetail detail = spu.getSpuDetail();
        //brand
        Brand brand = brandClient.queryBrandById(spu.getBrandId());

        //分类
        List<Category> categories = categoryClient.queryCategoryByIds(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //规格参数
        List<SpecGroup> specs = specClient.queryGroupByCid(spu.getCid3());


        model.put("title",spu.getTitle());
        model.put("subTitle",spu.getSubTitle());
        model.put("skus",skus);
        model.put("detail",detail);
        model.put("brand",brand);
        model.put("categories",categories);
        model.put("specs",specs);
        return model;
    }

    public void createHtml(Long spuId){

        Context context=new Context();
        context.setVariables(loadModel(spuId));

        File dest=new File("G:\\Program Files (x86)",spuId+".html");

        if (dest.exists()){
            dest.delete();
        }

        try(PrintWriter writer= new PrintWriter(dest,"UTF-8")){

            templateEngine.process("item",context,writer);

        }catch (Exception e){

            log.error("生成静态页面异常",e);
        }
    }

    public void deleteHtml(Long spuId) {

        File dest=new File("G:\\Program Files (x86)",spuId+".html");
        if (dest.exists()){
            dest.delete();
        }
    }
}
