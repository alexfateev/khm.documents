package com.himmash.database.dao;

import com.himmash.database.Const;
import com.himmash.model.Category;

import java.sql.*;
import java.util.ArrayList;

public class CategoryDbDAO implements CategoryDAO {
    private static final String INSERT = "INSERT INTO " + Const.TABLE_CATEGORY + "(" + Const.CAT_NAME + ","
            + Const.CAT_PID + ") VALUES(?,?)";
    private static final String UPDATE = "UPDATE " + Const.TABLE_CATEGORY + " SET " + Const.CAT_NAME + "=?,"
            + Const.CAT_PID + "=? WHERE " + Const.CAT_ID + "=?";
    private static final String DELETE = "DELETE FROM " + Const.TABLE_CATEGORY + " WHERE " + Const.CAT_ID + "=?";
    private static final String SELECT = "SELECT * FROM " + Const.TABLE_CATEGORY;
    private static final String SELECT_BY_ID = "SELECT * FROM " + Const.TABLE_CATEGORY + " WHERE " + Const.CAT_ID + "=?";

    private Connection connection = null;

    public CategoryDbDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int categoryInsert(Category category) {
        int id = -1;
        try (PreparedStatement st = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, category.getName());
            st.setInt(2, category.getPid());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            return id;
        }
    }

    @Override
    public boolean categoryUpdate(Category category) {
        boolean result = false;
        try (PreparedStatement st = connection.prepareStatement(UPDATE)) {
            st.setString(1, category.getName());
            st.setInt(2, category.getPid());
            st.setInt(3, category.getId());
            result = st.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            return result;
        }
    }

    @Override
    public boolean categoryDelete(int categoryId) {
        boolean result = false;
        try (PreparedStatement st = connection.prepareStatement(DELETE)) {
            st.setInt(1, categoryId);
            result = st.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            return result;
        }
    }

    @Override
    public ArrayList<Category> categorySelect() {
        return null;
    }

    @Override
    public Category categorySelectById(int categoryId) {
        return null;
    }
}
