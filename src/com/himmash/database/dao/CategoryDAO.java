package com.himmash.database.dao;

import com.himmash.model.Category;

import java.util.ArrayList;

public interface CategoryDAO {
    public int categoryInsert(Category category);
    public boolean categoryUpdate(Category category);
    public boolean categoryDelete(int categoryId);
    public ArrayList<Category> categorySelect();
    public Category categorySelectById(int categoryId);
}
