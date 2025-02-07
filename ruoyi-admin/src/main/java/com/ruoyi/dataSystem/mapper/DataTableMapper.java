package com.ruoyi.dataSystem.mapper;

import java.util.List;
import com.ruoyi.dataSystem.domain.DataTable;

/**
 * 数据Mapper接口
 * 
 * @author Chaver
 * @date 2025-02-06
 */
public interface DataTableMapper 
{
    /**
     * 查询数据
     * 
     * @param id 数据主键
     * @return 数据
     */
    public DataTable selectDataTableById(Long id);

    /**
     * 查询数据列表
     * 
     * @param dataTable 数据
     * @return 数据集合
     */
    public List<DataTable> selectDataTableList(DataTable dataTable);

    /**
     * 新增数据
     * 
     * @param dataTable 数据
     * @return 结果
     */
    public int insertDataTable(DataTable dataTable);

    /**
     * 修改数据
     * 
     * @param dataTable 数据
     * @return 结果
     */
    public int updateDataTable(DataTable dataTable);

    /**
     * 删除数据
     * 
     * @param id 数据主键
     * @return 结果
     */
    public int deleteDataTableById(Long id);

    /**
     * 批量删除数据
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataTableByIds(Long[] ids);
}
