package com.ruoyi.system.service.impl;

import com.ruoyi.system.service.IDataQaService;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Record;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 数据问答服务实现类
 */
@Service
public class DataQaServiceImpl implements IDataQaService {

    @Value("${neo4j.uri:bolt://localhost:7687}")
    private String neo4jUri;

    @Value("${neo4j.username:neo4j}")
    private String neo4jUsername;

    @Value("${neo4j.password:password}")
    private String neo4jPassword;

    private Driver driver;

    @PostConstruct
    public void init() {
        driver = GraphDatabase.driver(neo4jUri, AuthTokens.basic(neo4jUsername, neo4jPassword));
    }

    @PreDestroy
    public void close() {
        if (driver != null) {
            driver.close();
        }
    }

    /**
     * 处理用户查询并返回相关数据
     *
     * @param query 用户查询文本
     * @return 查询结果
     */
    @Override
    public Map<String, Object> processQuery(String query) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();

        try (Session session = driver.session()) {
            // 优化搜索逻辑，支持模糊查询所有字段
            String cypher = "MATCH (d:数据对象) " +
                    "MATCH (d)-[:数据类型]->(type) " +
                    "MATCH (d)-[:数据质量描述文本]->(desc) " +
                    "MATCH (d)-[:来源链接]->(link) " +
                    "MATCH (d)-[:网站名称]->(site) " +
                    "MATCH (d)-[:浏览次数]->(views) " +
                    "MATCH (d)-[:下载次数]->(downloads) " +
                    "WHERE d.数据对象 CONTAINS $keyword " +
                    "OR type.value CONTAINS $keyword " +
                    "OR desc.value CONTAINS $keyword " +
                    "OR site.value CONTAINS $keyword " +
                    "RETURN d.数据对象 as dataObject, type.value as dataType, " +
                    "desc.value as description, link.value as sourceLink, " +
                    "site.value as siteName, views.value as viewCount, " +
                    "downloads.value as downloadCount " +
                    "LIMIT 10";

            Result queryResult = session.run(cypher, Map.of("keyword", query));

            while (queryResult.hasNext()) {
                Record record = queryResult.next();
                Map<String, Object> dataItem = new HashMap<>();
                dataItem.put("dataObject", record.get("dataObject").asString());
                dataItem.put("dataType", record.get("dataType").asString());
                dataItem.put("description", record.get("description").asString());
                dataItem.put("sourceLink", record.get("sourceLink").asString());
                dataItem.put("siteName", record.get("siteName").asString());
                dataItem.put("viewCount", record.get("viewCount").asString());
                dataItem.put("downloadCount", record.get("downloadCount").asString());
                dataList.add(dataItem);
            }
        }

        // 构建响应
        result.put("query", query);
        result.put("dataList", dataList);
        result.put("totalCount", dataList.size());

        // 生成回复消息
        String replyMessage = generateReplyMessage(query, dataList);
        result.put("replyMessage", replyMessage);

        return result;
    }

    /**
     * 获取与查询相关的数据图谱
     *
     * @param query 用户查询文本
     * @param limit 限制返回结果数量
     * @return 图谱数据
     */
    @Override
    public Map<String, Object> getRelatedDataGraph(String query, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 50; // 默认限制
        }

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> nodes = new ArrayList<>();
        List<Map<String, Object>> links = new ArrayList<>();
        Map<Integer, Map<String, Object>> dataMap = new HashMap<>(); // 用于保存完整数据

        try (Session session = driver.session()) {
            // 优化查询，支持模糊匹配所有字段
            String cypher = "MATCH (d:数据对象) " +
                    "MATCH (d)-[:数据类型]->(type) " +
                    "MATCH (d)-[:数据质量描述文本]->(desc) " +
                    "MATCH (d)-[:来源链接]->(link) " +
                    "MATCH (d)-[:网站名称]->(site) " +
                    "MATCH (d)-[:浏览次数]->(views) " +
                    "MATCH (d)-[:下载次数]->(downloads) " +
                    "WHERE d.数据对象 CONTAINS $keyword " +
                    "OR type.value CONTAINS $keyword " +
                    "OR desc.value CONTAINS $keyword " +
                    "OR site.value CONTAINS $keyword " +
                    "RETURN d.数据对象 as dataObject, type.value as dataType, " +
                    "desc.value as description, link.value as sourceLink, " +
                    "site.value as siteName, views.value as viewCount, " +
                    "downloads.value as downloadCount " +
                    "LIMIT $limit";

            Result dataResult = session.run(cypher, Map.of("keyword", query, "limit", limit));

            // 获取数据对象的完整信息
            int nodeId = 0;
            Map<String, Integer> objectNodes = new HashMap<>();

            // 首先处理数据对象节点
            while (dataResult.hasNext()) {
                Record record = dataResult.next();

                String dataObject = record.get("dataObject").asString();
                String dataType = record.get("dataType").asString();
                String description = record.get("description").asString();
                String sourceLink = record.get("sourceLink").asString();
                String siteName = record.get("siteName").asString();
                String viewCount = record.get("viewCount").asString();
                String downloadCount = record.get("downloadCount").asString();

                if (!objectNodes.containsKey(dataObject)) {
                    // 创建数据对象节点
                    Map<String, Object> node = new HashMap<>();
                    node.put("id", nodeId);
                    node.put("name", dataObject);
                    node.put("category", 0);
                    node.put("symbolSize", 50);
                    node.put("nodeType", "数据对象");
                    nodes.add(node);

                    // 保存节点ID映射
                    objectNodes.put(dataObject, nodeId);

                    // 保存完整数据信息
                    Map<String, Object> fullData = new HashMap<>();
                    fullData.put("dataObject", dataObject);
                    fullData.put("dataType", dataType);
                    fullData.put("description", description);
                    fullData.put("sourceLink", sourceLink);
                    fullData.put("siteName", siteName);
                    fullData.put("viewCount", viewCount);
                    fullData.put("downloadCount", downloadCount);
                    dataMap.put(nodeId, fullData);

                    nodeId++;
                }
            }

            // 然后获取关系并创建属性节点
            String relationCypher = "MATCH (d:数据对象) " +
                    "MATCH (d)-[:数据类型]->(type) " +
                    "WHERE d.数据对象 CONTAINS $keyword " +
                    "OR type.value CONTAINS $keyword " +
                    "MATCH (d)-[r]->(p) " +
                    "RETURN d.数据对象 as dataObject, type(r) as relationType, p.value as propValue " +
                    "LIMIT $limit";

            Result relationResult = session.run(relationCypher, Map.of("keyword", query, "limit", limit));

            Map<String, Integer> propNodes = new HashMap<>();

            while (relationResult.hasNext()) {
                Record record = relationResult.next();

                String dataObject = record.get("dataObject").asString();
                String relationType = record.get("relationType").asString();
                String propValue = record.get("propValue").asString();

                // 跳过空值
                if (propValue == null || propValue.trim().isEmpty()) {
                    continue;
                }

                // 确保数据对象存在
                if (!objectNodes.containsKey(dataObject)) {
                    continue;
                }

                // 跳过"数据质量描述文本"节点 - 因为可以通过点击数据对象节点查看
                if ("数据质量描述文本".equals(relationType)) {
                    continue;
                }

                // 属性节点的唯一键
                String propKey = relationType + ":" + propValue;

                // 创建或获取属性节点
                if (!propNodes.containsKey(propKey)) {
                    Map<String, Object> propNode = new HashMap<>();
                    propNode.put("id", nodeId);
                    propNode.put("name", propValue);
                    propNode.put("category", 1);
                    propNode.put("symbolSize", 30);
                    propNode.put("nodeType", relationType); // 添加节点类型
                    nodes.add(propNode);
                    propNodes.put(propKey, nodeId);
                    nodeId++;
                }

                // 创建关系
                Map<String, Object> link = new HashMap<>();
                link.put("source", objectNodes.get(dataObject));
                link.put("target", propNodes.get(propKey));
                link.put("name", relationType);
                links.add(link);
            }
        }

        result.put("nodes", nodes);
        result.put("links", links);
        result.put("dataMap", dataMap); // 添加完整数据映射
        return result;
    }

    /**
     * 根据查询结果生成回复消息
     * 
     * @param query    用户查询
     * @param dataList 查询结果列表
     * @return 格式化的回复消息
     */
    private String generateReplyMessage(String query, List<Map<String, Object>> dataList) {
        if (dataList.isEmpty()) {
            return "抱歉，我没有找到与\"" + query + "\"相关的数据。请尝试其他关键词。";
        }

        StringBuilder message = new StringBuilder();
        message.append("根据您的查询\"").append(query).append("\"，我找到了以下相关数据：\n\n");

        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> data = dataList.get(i);
            message.append(i + 1).append(". ").append(data.get("dataObject")).append("\n");
            message.append("   类型：").append(data.get("dataType")).append("\n");
            message.append("   来源：").append(data.get("siteName")).append("\n");
            message.append("   浏览次数：").append(data.get("viewCount")).append("\n");
            message.append("   下载次数：").append(data.get("downloadCount")).append("\n\n");
        }

        message.append("您可以查看关系图获取更详细的数据关联信息。");
        return message.toString();
    }
}