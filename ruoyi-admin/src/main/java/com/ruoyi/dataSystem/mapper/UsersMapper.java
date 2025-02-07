package com.ruoyi.dataSystem.mapper;

import java.util.List;
import com.ruoyi.dataSystem.domain.Users;

/**
 * 用户Mapper接口
 * 
 * @author Chaver
 * @date 2025-02-06
 */
public interface UsersMapper 
{
    /**
     * 查询用户
     * 
     * @param userId 用户主键
     * @return 用户
     */
    public Users selectUsersByUserId(Long userId);

    /**
     * 查询用户列表
     * 
     * @param users 用户
     * @return 用户集合
     */
    public List<Users> selectUsersList(Users users);

    /**
     * 新增用户
     * 
     * @param users 用户
     * @return 结果
     */
    public int insertUsers(Users users);

    /**
     * 修改用户
     * 
     * @param users 用户
     * @return 结果
     */
    public int updateUsers(Users users);

    /**
     * 删除用户
     * 
     * @param userId 用户主键
     * @return 结果
     */
    public int deleteUsersByUserId(Long userId);

    /**
     * 批量删除用户
     * 
     * @param userIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUsersByUserIds(Long[] userIds);
}
