package com.hla.gysc.service.impl;

import com.hla.gysc.dao.AdminMapper;
import com.hla.gysc.entity.Admin;
import com.hla.gysc.service.AdminService;
import com.hla.gysc.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminMapper adminMapper;

    @Override
    public boolean add(Admin admin) {
        return adminMapper.insertOne(admin) > 0;
    }

    @Override
    public boolean update(Admin admin) {
        return adminMapper.updateOne(admin) > 0;
    }

    @Override
    public List<Admin> getList(String admin_name, PageUtil pageUtil) {
        return adminMapper.selectAdminList(admin_name, pageUtil);
    }

    @Override
    public Admin get(String admin_name, Integer id) {
        return adminMapper.selectOne(admin_name, id);
    }

    @Override
    public Integer login(String admin_name, String admin_password) {
        return adminMapper.selectByLogin(admin_name, admin_password);
    }

    @Override
    public Integer getTotal(String admin_name) {
        return adminMapper.selectTotal(admin_name);
    }

    @Override
    public Admin getAdmin(String userName, String password) {
        return adminMapper.getAdmin(userName, password);
    }
}
