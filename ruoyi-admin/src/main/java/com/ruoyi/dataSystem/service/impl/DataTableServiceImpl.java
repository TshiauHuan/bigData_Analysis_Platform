package com.ruoyi.dataSystem.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.dataSystem.mapper.DataTableMapper;
import com.ruoyi.dataSystem.domain.DataTable;
import com.ruoyi.dataSystem.service.IDataTableService;

/**
 * 数据Service业务层处理
 * 
 * @author Chaver
 * @date 2025-02-06
 */
@Service
public class DataTableServiceImpl implements IDataTableService 
{
    @Autowired
    private DataTableMapper dataTableMapper;

    /**
     * 查询数据
     * 
     * @param id 数据主键
     * @return 数据
     */
    @Override
    public DataTable selectDataTableById(Long id)
    {
        return dataTableMapper.selectDataTableById(id);
    }

    /**
     * 查询数据列表
     * 
     * @param dataTable 数据
     * @return 数据
     */
    @Override
    public List<DataTable> selectDataTableList(DataTable dataTable)
    {
        return dataTableMapper.selectDataTableList(dataTable);
    }

    /**
     * 新增数据
     * 
     * @param dataTable 数据
     * @return 结果
     */
    @Override
    public int insertDataTable(DataTable dataTable)
    {
        return dataTableMapper.insertDataTable(dataTable);
    }

    /**
     * 修改数据
     * 
     * @param dataTable 数据
     * @return 结果
     */
    @Override
    public int updateDataTable(DataTable dataTable)
    {
        return dataTableMapper.updateDataTable(dataTable);
    }

    /**
     * 批量删除数据
     * 
     * @param ids 需要删除的数据主键
     * @return 结果
     */
    @Override
    public int deleteDataTableByIds(Long[] ids)
    {
        return dataTableMapper.deleteDataTableByIds(ids);
    }

    /**
     * 删除数据信息
     * 
     * @param id 数据主键
     * @return 结果
     */
    @Override
    public int deleteDataTableById(Long id)
    {
        return dataTableMapper.deleteDataTableById(id);
    }
}
