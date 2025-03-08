package com.ruoyi.system.service;

import java.util.Map;

/**
 * 数据问答服务接口
 */
public interface IDataQaService {

    /**
     * 处理用户查询并返回相关数据
     * 
     * @param query 用户查询文本
     * @return 查询结果
     */
    Map<String, Object> processQuery(String query);

    /**
     * 获取与查询相关的数据图谱
     * 
     * @param query 用户查询文本
     * @param limit 结果限制数量
     * @return 图谱数据
     */
    Map<String, Object> getRelatedDataGraph(String query, Integer limit);
}