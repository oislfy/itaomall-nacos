//package com.wgml.itmall.list.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.wgml.itmall.bean.list.SkuLsInfo;
//import com.wgml.itmall.bean.list.SkuLsParams;
//import com.wgml.itmall.bean.list.SkuLsResult;
//import com.wgml.itmall.config.RedisUtil;
//import com.wgml.itmall.list.service.ListService;
//import io.searchbox.client.JestClient;
//import io.searchbox.core.*;
//import io.searchbox.core.search.aggregation.MetricAggregation;
//import io.searchbox.core.search.aggregation.TermsAggregation;
//import lombok.extern.slf4j.Slf4j;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.MatchQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.index.query.TermQueryBuilder;
//import org.elasticsearch.search.aggregations.AggregationBuilders;
//import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
//import org.elasticsearch.search.sort.SortOrder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//import redis.clients.jedis.Jedis;
//
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
// * import org.elasticsearch.search.highlight.HighlightBuilder;
// *
// * @author Ye Linfang
// * @date 2021/8/13 13:47
// * @since JDK1.8
// */
//@Slf4j
//@Service
//@RefreshScope
//@Deprecated
//public class ListServiceImplDeprecated implements ListService {
//    @Resource
//    private JestClient jestClient;
//
//    @Value("${es.ES_INDEX:itaomall}")
//    public static final String ES_INDEX = "itaomall";
//    @Value("${es.ES_TYPE:SkuInfo}")
//    public static final String ES_TYPE = "SkuInfo";
//    @Resource
//    private RedisUtil redisUtil;
//
//    @Override
//    public Boolean saveSkuLsInfo(SkuLsInfo skuLsInfo) {
//        // 保存数据
//        Index index = new Index.Builder(skuLsInfo).index(ES_INDEX).type(ES_TYPE).id(skuLsInfo.getId()).build();
//        try {
//            DocumentResult documentResult = jestClient.execute(index);
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    @Override
//    public SkuLsResult search(SkuLsParams skuLsParams) {
//        //动态生成查询语句
//        String query = makeQueryStringForSearch(skuLsParams);
//        //定制查询动作
//        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
//        try {
//            //执行查询动作，返回查询结果
//            SearchResult searchResult = jestClient.execute(search);
//            //处理，封装查询结果
//            SkuLsResult skuLsResult = makeResultForSearch(skuLsParams, searchResult);
//            log.debug("skuLsResult={}", skuLsResult);
//            return skuLsResult;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private SkuLsResult makeResultForSearch(SkuLsParams skuLsParams, SearchResult searchResult) {
//        //创建存储SkuLsInfo集合对象
//        List<SkuLsInfo> skuLsInfoList = new ArrayList<>(skuLsParams.getPageSize());
//        //获取hits
//        List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
//        for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
//            //获取SkuLsInfo对象
//            SkuLsInfo skuLsInfo = hit.source;
//            if (hit.highlight != null && hit.highlight.size() > 0) {
//                List<String> skuNameList = hit.highlight.get("skuName");
//                //把带有高亮标签的字符串替换skuName
//                skuLsInfo.setSkuName(skuNameList.get(0));
//            }
//            skuLsInfoList.add(skuLsInfo);
//        }
//
//        //取记录个数并计算出总页数
//        long totalPage = (searchResult.getTotal() + skuLsParams.getPageSize() - 1)
//                / skuLsParams.getPageSize();
//
//        //取出涉及的属性值id
//        List<String> attrValueIdList = new ArrayList<>();
//        MetricAggregation aggregations = searchResult.getAggregations();
//        TermsAggregation groupby_attr = aggregations.getTermsAggregation("groupby_attr");
//        if (groupby_attr != null) {
//            List<TermsAggregation.Entry> buckets = groupby_attr.getBuckets();
//            for (TermsAggregation.Entry bucket : buckets) {
//                attrValueIdList.add(bucket.getKey());
//            }
//        }
//
//        //创建返回结果对象，设置并各个属性值
//        //设置skuLsInfoList、总条数、总页数、属性值id集合
//        return new SkuLsResult()
//                .setSkuLsInfoList(skuLsInfoList)
//                .setTotal(searchResult.getTotal())
//                .setTotalPages(totalPage)
//                .setAttrValueIdList(attrValueIdList);
//    }
//
//    private String makeQueryStringForSearch(SkuLsParams skuLsParams) {
//        // 创建查询bulid
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//
//        //判断是否有查询条件keyword
//        if (!StringUtils.isEmpty(skuLsParams.getKeyword())) {
//            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", skuLsParams.getKeyword());
//            boolQueryBuilder.must(matchQueryBuilder);
//            // 设置高亮
//            HighlightBuilder highlightBuilder = new HighlightBuilder()
//                    .field("skuName")
//                    .preTags("<span style=color:red>")
//                    .postTags("</span>");
//            // 将高亮结果放入查询器中
//            searchSourceBuilder.highlighter(highlightBuilder);
////            searchSourceBuilder.highlight(highlightBuilder);
//        }
//
//        // 设置三级分类
//        if (!StringUtils.isEmpty(skuLsParams.getCatalog3Id())) {
//            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", skuLsParams.getCatalog3Id());
//            boolQueryBuilder.filter(termQueryBuilder);
//        }
//        // 设置属性值
//        if (skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0) {
//            for (int i = 0; i < skuLsParams.getValueId().length; i++) {
//                String valueId = skuLsParams.getValueId()[i];
//                boolQueryBuilder
//                        .filter(new TermQueryBuilder("skuAttrValueList.valueId", valueId));
//            }
//        }
//        searchSourceBuilder.query(boolQueryBuilder);
//        // 设置分页
//        int from = (skuLsParams.getPageNo() - 1) * skuLsParams.getPageSize();
//        searchSourceBuilder.from(from);
//        searchSourceBuilder.size(skuLsParams.getPageSize());
//        // 设置按照热度排序
//        searchSourceBuilder.sort("hotScore", SortOrder.DESC);
//        //设置聚合
////        TermsBuilder groupby_attr =
//        TermsAggregationBuilder groupby_attr = AggregationBuilders.terms("groupby_attr").field("skuAttrValueList.valueId");
//        searchSourceBuilder.aggregation(groupby_attr);
//
//        String query = searchSourceBuilder.toString();
//        log.debug("动态生成dsl语句===》query={}", query);
//        return query;
//    }
//
//    @Override
//    public Boolean checkOnSale(String skuId) {
//        //创建查询条件
//        String query = new SearchSourceBuilder()
//                .query(QueryBuilders.matchQuery("_id", skuId))
//                .toString();
//        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
//        SearchResult result = null;
//        try {
//            result = jestClient.execute(search);
//            if (result.getTotal() == 1) {
//                return true;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    @Override
//    public String makeValuesSkuJsonByES(String spuId) {
//        String query = "{\n" +
//                "  \"query\": {\n" +
//                "  \"bool\": {\n" +
//                "    \"filter\": [{\"term\": {\"spuId\": \"" + spuId + "\"}}]\n" +
//                "    }\n" +
//                "  }\n" +
//                "  ,\n" +
//                "  \"aggs\": {\n" +
//                "  \"groupby_valuesSku\": {\n" +
//                "    \"terms\": {\n" +
//                "    \"field\": \"valuesSkuJson\"\n" +
//                "    }\n" +
//                "  }\n" +
//                "  }\n" +
//                "}";
//        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
//        Map<String, String> map = new HashMap<>();
//        try {
//            SearchResult searchResult = jestClient.execute(search);
//            MetricAggregation aggregations = searchResult.getAggregations();
//            TermsAggregation groupby_valuesSku = aggregations.getTermsAggregation("groupby_valuesSku");
//            if (groupby_valuesSku != null) {
//                List<TermsAggregation.Entry> buckets = groupby_valuesSku.getBuckets();
//                for (TermsAggregation.Entry bucket : buckets) {
//                    //117|119:33
//                    String key = bucket.getKey();
//                    String[] split = key.split(":");
//                    map.put(split[0], split[1]);
//                }
//            }
//            return JSON.toJSONString(map);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//    //更新热度评分
//    @Override
//    public void incrHotScore(String skuId) {
//        try {
//            Jedis jedis = redisUtil.getJedis();
//            int timesToEs = 10;
//            Double hotScore = jedis.zincrby("hotScore", 1, "skuId:" + skuId);
//            if (hotScore % timesToEs == 0) {
//                updateHotScore(skuId, Math.round(hotScore));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void updateHotScore(String skuId, long hotScore) {
//        String updateJson = "{\n" +
//                "   \"doc\":{\n" +
//                "     \"hotScore\":" + hotScore + "\n" +
//                "   }\n" +
//                "}";
//
//        Update update = new Update.Builder(updateJson).index(ES_INDEX).type(ES_TYPE).id(skuId).build();
//        try {
//            jestClient.execute(update);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
