package com.whitecrow.blog.model.dto;

import com.whitecrow.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author WhiteCrow
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BlogQuery extends PageRequest {
    private static final long serialVersionUID = 1089750431480855057L;

    /**
     * id
     */
    private Long id;

    /**
     * 搜索关键词（同时对标题作者姓名和描述搜索）
     */
    private String searchText;
}
