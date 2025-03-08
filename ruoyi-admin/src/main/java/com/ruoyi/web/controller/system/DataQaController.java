package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.system.service.IDataQaService;

/**
 * 数据问答系统控制器
 */
@RestController
@RequestMapping("/system/qa")
public class DataQaController extends BaseController {

    @Autowired
    private IDataQaService dataQaService;

    /**
     * 处理用户的数据查询请求
     * 
     * @param query 用户查询内容
     * @return 响应结果
     */
    @GetMapping("/query")
    public AjaxResult queryData(String query) {
        return AjaxResult.success(dataQaService.processQuery(query));
    }

    /**
     * 获取相关数据节点关系图
     * 
     * @param query 用户查询内容
     * @param limit 结果限制数量
     * @return 图数据结构
     */
    @GetMapping("/graph")
    public AjaxResult getRelatedDataGraph(String query, Integer limit) {
        return AjaxResult.success(dataQaService.getRelatedDataGraph(query, limit));
    }
}