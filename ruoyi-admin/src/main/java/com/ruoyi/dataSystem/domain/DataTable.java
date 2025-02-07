package com.ruoyi.dataSystem.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 数据对象 data_table
 * 
 * @author Chaver
 * @date 2025-02-06
 */
public class DataTable extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 数据ID */
    private Long id;

    /** 数据类型 */
    @Excel(name = "数据类型")
    private String dataType;

    /** 数据对象 */
    @Excel(name = "数据对象")
    private String dataObject;

    /** 数据质量描述文本 */
    @Excel(name = "数据质量描述文本")
    private String dataQualityDescription;

    /** 来源链接 */
    @Excel(name = "来源链接")
    private String sourceLink;

    /** 网站名称 */
    @Excel(name = "网站名称")
    private String websiteName;

    /** 浏览次数 */
    @Excel(name = "浏览次数")
    private Long viewCount;

    /** 下载次数 */
    @Excel(name = "下载次数")
    private Long downloadCount;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setDataType(String dataType) 
    {
        this.dataType = dataType;
    }

    public String getDataType() 
    {
        return dataType;
    }
    public void setDataObject(String dataObject) 
    {
        this.dataObject = dataObject;
    }

    public String getDataObject() 
    {
        return dataObject;
    }
    public void setDataQualityDescription(String dataQualityDescription) 
    {
        this.dataQualityDescription = dataQualityDescription;
    }

    public String getDataQualityDescription() 
    {
        return dataQualityDescription;
    }
    public void setSourceLink(String sourceLink) 
    {
        this.sourceLink = sourceLink;
    }

    public String getSourceLink() 
    {
        return sourceLink;
    }
    public void setWebsiteName(String websiteName) 
    {
        this.websiteName = websiteName;
    }

    public String getWebsiteName() 
    {
        return websiteName;
    }
    public void setViewCount(Long viewCount) 
    {
        this.viewCount = viewCount;
    }

    public Long getViewCount() 
    {
        return viewCount;
    }
    public void setDownloadCount(Long downloadCount) 
    {
        this.downloadCount = downloadCount;
    }

    public Long getDownloadCount() 
    {
        return downloadCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("dataType", getDataType())
            .append("dataObject", getDataObject())
            .append("dataQualityDescription", getDataQualityDescription())
            .append("sourceLink", getSourceLink())
            .append("websiteName", getWebsiteName())
            .append("viewCount", getViewCount())
            .append("downloadCount", getDownloadCount())
            .toString();
    }
}
