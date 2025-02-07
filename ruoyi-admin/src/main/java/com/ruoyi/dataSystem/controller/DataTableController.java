package com.ruoyi.dataSystem.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.dataSystem.domain.DataTable;
import com.ruoyi.dataSystem.service.IDataTableService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 数据Controller
 * 
 * @author Chaver
 * @date 2025-02-06
 */
@RestController
@RequestMapping("/dataSystem/data")
public class DataTableController extends BaseController
{
    @Autowired
    private IDataTableService dataTableService;

    /**
     * 查询数据列表
     */
    @PreAuthorize("@ss.hasPermi('dataSystem:data:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataTable dataTable)
    {
        startPage();
        List<DataTable> list = dataTableService.selectDataTableList(dataTable);
        return getDataTable(list);
    }

    /**
     * 导出数据列表
     */
    @PreAuthorize("@ss.hasPermi('dataSystem:data:export')")
    @Log(title = "数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataTable dataTable)
    {
        List<DataTable> list = dataTableService.selectDataTableList(dataTable);
        ExcelUtil<DataTable> util = new ExcelUtil<DataTable>(DataTable.class);
        util.exportExcel(response, list, "数据数据");
    }

    /**
     * 获取数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('dataSystem:data:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataTableService.selectDataTableById(id));
    }

    /**
     * 新增数据
     */
    @PreAuthorize("@ss.hasPermi('dataSystem:data:add')")
    @Log(title = "数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataTable dataTable)
    {
        return toAjax(dataTableService.insertDataTable(dataTable));
    }

    /**
     * 修改数据
     */
    @PreAuthorize("@ss.hasPermi('dataSystem:data:edit')")
    @Log(title = "数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataTable dataTable)
    {
        return toAjax(dataTableService.updateDataTable(dataTable));
    }

    /**
     * 删除数据
     */
    @PreAuthorize("@ss.hasPermi('dataSystem:data:remove')")
    @Log(title = "数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dataTableService.deleteDataTableByIds(ids));
    }
}
