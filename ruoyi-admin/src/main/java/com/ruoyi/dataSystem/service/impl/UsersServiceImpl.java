package com.ruoyi.dataSystem.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.dataSystem.mapper.UsersMapper;
import com.ruoyi.dataSystem.domain.Users;
import com.ruoyi.dataSystem.service.IUsersService;

/**
 * 用户Service业务层处理
 * 
 * @author Chaver
 * @date 2025-02-06
 */
@Service
public class UsersServiceImpl implements IUsersService 
{
    @Autowired
    private UsersMapper usersMapper;

    /**
     * 查询用户
     * 
     * @param userId 用户主键
     * @return 用户
     */
    @Override
    public Users selectUsersByUserId(Long userId)
    {
        return usersMapper.selectUsersByUserId(userId);
    }

    /**
     * 查询用户列表
     * 
     * @param users 用户
     * @return 用户
     */
    @Override
    public List<Users> selectUsersList(Users users)
    {
        return usersMapper.selectUsersList(users);
    }

    /**
     * 新增用户
     * 
     * @param users 用户
     * @return 结果
     */
    @Override
    public int insertUsers(Users users)
    {
        return usersMapper.insertUsers(users);
    }

    /**
     * 修改用户
     * 
     * @param users 用户
     * @return 结果
     */
    @Override
    public int updateUsers(Users users)
    {
        return usersMapper.updateUsers(users);
    }

    /**
     * 批量删除用户
     * 
     * @param userIds 需要删除的用户主键
     * @return 结果
     */
    @Override
    public int deleteUsersByUserIds(Long[] userIds)
    {
        return usersMapper.deleteUsersByUserIds(userIds);
    }

    /**
     * 删除用户信息
     * 
     * @param userId 用户主键
     * @return 结果
     */
    @Override
    public int deleteUsersByUserId(Long userId)
    {
        return usersMapper.deleteUsersByUserId(userId);
    }
}
