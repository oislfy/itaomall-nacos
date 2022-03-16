package com.wgml.itmall.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.wgml.itmall.bean.list.SkuLsInfo;
import com.wgml.itmall.bean.manage.*;
import com.wgml.itmall.manage.constant.ManageConst;
import com.wgml.itmall.manage.mapper.*;
import com.wgml.itmall.manage.service.ManageService;
import com.wgml.itmall.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Ye Linfang
 * @since JDK1.8
 */
@Service
@Slf4j
public class ManageServiceImpl implements ManageService {
    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;
    @Resource
    private BaseCatalog1Mapper baseCatalog1Mapper;
    @Resource
    private BaseCatalog2Mapper baseCatalog2Mapper;
    @Resource
    private BaseCatalog3Mapper baseCatalog3Mapper;
    @Resource
    private SpuInfoMapper spuInfoMapper;
    @Resource
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Resource
    private SpuImageMapper spuImageMapper;
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Resource
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private SkuImageMapper skuImageMapper;
    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Resource
    private SkuAttrValueMapper skuAttrValueMapper;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public List<BaseCatalog1> getCatalog1() {
        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);
        return baseCatalog2Mapper.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);
        return baseCatalog3Mapper.select(baseCatalog3);
    }

    /**
     * @param catalog3Id
     * @return: java.util.List<com.wgml.itmall.bean.manage.BaseAttrInfo>
     * @author Ye Linfang
     * @date 2021/8/8 11:18
     * @description 修改后
     */
    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
        return baseAttrInfoMapper.getBaseAttrInfoListByCatalog3Id(catalog3Id);
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(String attrId) {
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrId);
        return baseAttrValueMapper.select(baseAttrValue);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        try {
            //如果有主键就进行更新，如果没有就插入
            if (!StringUtils.isEmpty(baseAttrInfo.getId())) {
                baseAttrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);
            } else {
                //防止主键被赋上一个空字符串
                baseAttrInfo.setId(null);
                baseAttrInfoMapper.insertSelective(baseAttrInfo);
            }
            //把原属性值全部清空
            BaseAttrValue baseAttrValue = new BaseAttrValue();
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.delete(baseAttrValue);
            //重新插入属性值
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            if (attrValueList != null && attrValueList.size() > 0) {
                for (BaseAttrValue attrValue : attrValueList) {
                    attrValue.setId(null);
                    attrValue.setAttrId(baseAttrInfo.getId());
                    baseAttrValueMapper.insertSelective(attrValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<SpuInfo> getSupInfoList(SpuInfo spuInfo) {
        return spuInfoMapper.select(spuInfo);
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpuInfo(SpuInfo spuInfo) {
        //根据spuId判断是否新增还是更新spu
        String spuId = spuInfo.getId();
        if (StringUtils.isEmpty(spuId)) {
            //新增数据
            spuInfo.setId(null);
            spuInfoMapper.insertSelective(spuInfo);
            //主键回填
            spuId = spuInfo.getId();
        } else {
            //更新数据
            spuInfoMapper.updateByPrimaryKeySelective(spuInfo);
        }

        //保存图片列表，先删除再新增
        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(spuId);
        spuImageMapper.delete(spuImage);
        //获取图片数据
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (spuImageList != null && spuImageList.size() > 0) {
            for (SpuImage image : spuImageList) {
                image.setSpuId(spuId);
                image.setId(null);
                spuImageMapper.insertSelective(image);
            }
        }

        //保存销售属性列表和销售属性值都先删除，再新增
        SpuSaleAttr spuSaleAttr = new SpuSaleAttr();
        spuSaleAttr.setSpuId(spuId);
        spuSaleAttrMapper.delete(spuSaleAttr);

        SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
        spuSaleAttrValue.setSpuId(spuId);
        spuSaleAttrValueMapper.delete(spuSaleAttrValue);
        //获取数据
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (spuSaleAttrList != null && spuSaleAttrList.size() > 0) {
            for (SpuSaleAttr saleAttr : spuSaleAttrList) {
                //保存销售属性
                saleAttr.setSpuId(spuId);
                saleAttr.setId(null);
                spuSaleAttrMapper.insertSelective(saleAttr);
                //保存销售属性值
                List<SpuSaleAttrValue> spuSaleAttrValueList = saleAttr.getSpuSaleAttrValueList();
                if (spuSaleAttrValueList != null && spuSaleAttrValueList.size() > 0) {
                    for (SpuSaleAttrValue saleAttrValue : spuSaleAttrValueList) {
                        saleAttrValue.setSpuId(spuId);
                        saleAttrValue.setId(null);
                        spuSaleAttrValueMapper.insertSelective(saleAttrValue);
                    }
                }
            }
        }

    }

    @Override
    public List<SpuImage> getSpuImageListBySpuId(SpuImage spuImage) {
        return spuImageMapper.select(spuImage);
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListBySpuId(String spuId) {
        return spuSaleAttrMapper.getSpuSaleAttrListBySpuId(spuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSkuInfo(SkuInfo skuInfo) {
        //1保存skuInfo，根据skuId判断是保存还是更新
        if (StringUtils.isEmpty(skuInfo.getId())) {
            skuInfo.setId(null);
            skuInfoMapper.insertSelective(skuInfo);
        } else {
            skuInfoMapper.updateByPrimaryKeySelective(skuInfo);
        }
        //2先删除再保存skuImage
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuInfo.getId());
        skuImageMapper.delete(skuImage);

        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (skuImageList != null && skuImageList.size() > 0) {
            for (SkuImage image : skuImageList) {
                if (image.getId() != null) {
                    image.setId(null);
                }
                image.setSkuId(skuInfo.getId());
                skuImageMapper.insertSelective(image);
            }
        }

        //3先删除再保存skuAttrValue
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuInfo.getId());
        skuAttrValueMapper.delete(skuAttrValue);

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (skuAttrValueList != null && skuAttrValueList.size() > 0) {
            for (SkuAttrValue attrValue : skuAttrValueList) {
                if (attrValue.getId() != null) {
                    attrValue.setId(null);
                }
                attrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insertSelective(attrValue);
            }
        }

        //4先删除再保存skuSaleAttrValue
        SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuInfo.getId());
        skuSaleAttrValueMapper.delete(skuSaleAttrValue);

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (skuSaleAttrValueList != null && skuSaleAttrValueList.size() > 0) {
            for (SkuSaleAttrValue saleAttrValue : skuSaleAttrValueList) {
                if (saleAttrValue.getId() != null) {
                    saleAttrValue.setId(null);
                }
                saleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValueMapper.insertSelective(saleAttrValue);
            }
        }
    }

    @Override
    public SkuInfo getSkuInfoByRedis(String skuId) {
//        return getInfoJedis(skuId);
        return getInfoRedission(skuId);
    }

    /**
     * @param skuId
     * @return: com.wgml.itmall.bean.manage.SkuInfo
     * @author Ye Linfang
     * @date 2021/8/10 21:22
     * @description 使用Redission实现分布式锁解决缓存击穿问题
     */
    private SkuInfo getInfoRedission(String skuId) {
        RLock lock = null;
        SkuInfo skuInfo = null;
        String skuInfokey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
        try {
            Config config = new Config();
            config.useSingleServer().setAddress(ManageConst.REDISSION_HOST).setPassword("itmallredis");
            RedissonClient redissonClient = Redisson.create(config);
            // 使用redisson 调用getLock
            lock = redissonClient.getLock("yourLock");
            // 加锁
            lock.lock(10, TimeUnit.SECONDS);

            // 放入业务逻辑代码
            if (redisUtil.hasKey(skuInfokey)) {
                //存在key
                String skuInfoJsonString = (String) redisUtil.get(skuInfokey);
                if (!StringUtils.isEmpty(skuInfoJsonString)) {
                    //数据不为空
                    skuInfo = JSON.parseObject(skuInfoJsonString, SkuInfo.class);
                    log.debug("缓存查完返回转换数据");
                }
            } else {
                //考虑缓存key失效缓存击穿问题  这里用redission分布式锁解决了
                // 不存在key，去DB查询
                log.debug(" 不存在skuInfoKey，去DB查询");
                skuInfo = getSkuInfoDB(skuId);
                //查完放入redis缓存并设置过期时间
                redisUtil.setEX(skuInfokey, JSON.toJSONString(skuInfo), ManageConst.SKUKEY_TIMEOUT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("redis缓存崩溃，去DB查询skuInfo{}", e.getMessage());
            // redis缓存崩溃，去DB查询
            skuInfo = getSkuInfoDB(skuId);
        } finally {
            //释放锁
            if (lock != null) {
                lock.unlock();
            }
        }
        return skuInfo;
    }

    /**
     * @param skuId
     * @return: com.wgml.itmall.bean.manage.SkuInfo
     * @author Ye Linfang
     * @date 2021/8/10 21:21
     * @description 使用Jedis实现分布式锁解决缓存击穿问题
     */
    private SkuInfo getInfoJedis(String skuId) {
        SkuInfo skuInfo = null;
        // 定义key
        String skuInfoKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;

        try {
            if (redisUtil.hasKey(skuInfoKey)) {
                //存在key 命中缓存
                log.debug("存在skuInfoKey，命中缓存！");
                String skuInfoJsonString = (String) redisUtil.get(skuInfoKey);
                if (!StringUtils.isEmpty(skuInfoJsonString)) {
                    //数据不为空，返回数据
                    skuInfo = JSON.parseObject(skuInfoJsonString, SkuInfo.class);
                    log.debug("缓存查完返回转换数据{}", skuInfo);
                }
            } else {
                log.debug("不存在skuInfoKey 没有命中缓存！");
                //不存在key 没有命中缓存，需考虑缓存key失效、防止大量用户访问后台数据库导致崩溃，缓存击穿问题
                // 需要加锁！没有拿到锁的就睡眠，直到有锁的取出完数据，还要放入缓存中，下次直接从缓存中取得即可！
                // 1定义key sku:skuId:lock
                String skuLockKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKULOCK_SUFFIX;
                // 2生成锁 10s   旧版用setnx，setex
                //String lockKey = jedis.set(skuLockKey, "OK", "NX", "PX", ManageConst.SKULOCK_EXPIRE_PX); 判断 "OK".equals(lockKey)
                Boolean ok = redisUtil.setNX(skuLockKey, "OK", ManageConst.SKULOCK_EXPIRE_PX, TimeUnit.SECONDS);
                // 3判断
                if (ok) {
                    log.debug("获取锁！,去数据库中取得数据");
                    // 从数据库中取得数据
                    skuInfo = getSkuInfoDB(skuId);
                    // 将对象数据转换成字符串并放入缓存
                    redisUtil.setEX(skuInfoKey, JSON.toJSONString(skuInfo), ManageConst.SKUKEY_TIMEOUT);
                } else {
                    log.debug("睡眠等待！");
                    // 等待
                    Thread.sleep(1000);
                    // 自旋
                    skuInfo = getInfoJedis(skuId);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.debug("redis缓存崩溃，去DB查询skuInfo{}", e.getMessage());
            // redis缓存崩溃，去DB查询
            skuInfo = getSkuInfoDB(skuId);
        }
        return skuInfo;
    }

//之前版本
//    @Override
//    public SkuInfo getSkuInfoDB(String skuId) {
//        //查询基本信息
//        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
//        SkuImage skuImage = new SkuImage();
//        skuImage.setSkuId(skuId);
//        List<SkuImage> skuImageList = skuImageMapper.select(skuImage);
//        skuInfo.setSkuImageList(skuImageList);
//        //查询销售属性,设置属性
//        //详情页回显锁定
//        skuInfo.setSpuSaleAttrList(getSpuSaleAttrListCheckBySku(skuInfo));
////        //详情页切换销售属性
////        skuInfo.setSkuSaleAttrValueList(getSkuSaleAttrValueListBySpu(skuInfo.getSpuId()));
//        //查询平台属性值集合 用于存到es
//        SkuAttrValue skuAttrValue = new SkuAttrValue();
//        skuAttrValue.setSkuId(skuId);
//        skuInfo.setSkuAttrValueList(skuAttrValueMapper.select(skuAttrValue));
//        log.debug("数据库DB查询返回SkuInfo数据");
//        return skuInfo;
//    }


    /**
     * @param skuId
     * @return: com.wgml.itmall.bean.manage.SkuInfo
     * @author Ye Linfang
     * @date 2021/8/15 12:56
     * @description 从mysql获取数据，封装skuInfo,缓存进redis,
     */
    @Override
    public SkuInfo getSkuInfoDB(String skuId) {
        //查询基本信息
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        //查询图片集合
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImageList = skuImageMapper.select(skuImage);
        //查询平台属性值集合 用于存到es
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuId);
        List<SkuAttrValue> skuAttrValueList = skuAttrValueMapper.select(skuAttrValue);
        //设置valuesSkuIdJson 用于valueid1|valueid2|valueid3|... ：skuId
        String valuesSkuJson = makeValuesSkuIdString(skuId);
        //设置各个属性
        skuInfo.setSkuImageList(skuImageList)
                //查询销售属性,设置属性，用于详情页回显锁定
                .setSpuSaleAttrList(getSpuSaleAttrListCheckBySku(skuInfo))
                .setSkuAttrValueList(skuAttrValueList)
                .setValuesSkuJson(valuesSkuJson);
        log.debug("数据库DB查询返回SkuInfo数据");
        return skuInfo;
    }

    /**
     * @param skuId
     * @return: java.lang.String
     * @author Ye Linfang
     * @date 2021/8/15 18:45
     * @description //设置valuesSkuIdJson 用于valueid1|valueid2|valueid3|... ：skuId
     */
    private String makeValuesSkuIdString(String skuId) {
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuSaleAttrValueMapper.select(new SkuSaleAttrValue().setSkuId(skuId));
        String valueIds = "";
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            if (!StringUtils.isEmpty(valueIds)) {
                valueIds += "|";
            }
            valueIds += skuSaleAttrValue.getSaleAttrValueId();
        }
        valueIds += ":" + skuId;
        return valueIds;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo) {
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuInfo.getId(), skuInfo.getSpuId());
        return spuSaleAttrs;
    }

    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {
        return skuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(spuId);
    }

    @Override
    public List<BaseAttrInfo> getAttrList(List<String> attrValueIdList) {
        //拼接字符串
        String attrValueIds = org.apache.commons.lang3.StringUtils.join(attrValueIdList.toArray(), ",");
        log.debug("拼接字符串attrValueIds={}", attrValueIds);
        return baseAttrInfoMapper.selectAttrInfoListByIds(attrValueIds);
    }

    @Override
    public SkuLsInfo getSkuLsInfo(String skuId) {
        SkuLsInfo skuLsInfo = new SkuLsInfo();
        SkuInfo skuInfo = getSkuInfoByRedis(skuId);
        BeanUtils.copyProperties(skuInfo, skuLsInfo);
        return skuLsInfo;
    }


}
