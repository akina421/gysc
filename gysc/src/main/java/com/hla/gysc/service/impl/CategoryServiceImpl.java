package com.hla.gysc.service.impl;

import com.hla.gysc.dao.CategoryMapper;
import com.hla.gysc.entity.Category;
import com.hla.gysc.service.CategoryService;
import com.hla.gysc.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;

    @Override
    public boolean add(Category category) {
        return categoryMapper.insertOne(category) > 0;
    }

    @Override
    public boolean update(Category category) {
        return categoryMapper.updateOne(category) > 0;
    }

    @Override
    public List<Category> getList(String category_name, PageUtil pageUtil) {
        return categoryMapper.selectCategoryList(category_name, pageUtil);
    }

    @Override
    public Category get(Integer category_id) {
        return categoryMapper.selectOne(category_id);
    }

    @Override
    public Integer getTotal(String category_name) {
        return categoryMapper.selectTotal(category_name);
    }
}
