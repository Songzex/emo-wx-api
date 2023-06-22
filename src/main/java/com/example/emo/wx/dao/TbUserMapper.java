package com.example.emo.wx.dao;

import com.example.emo.wx.pojo.TbUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.Set;

@Mapper
/**
* @author 24022
* @description 针对表【tb_user(用户表)】的数据库操作Mapper
* @createDate 2023-05-24 10:24:59
* @Entity com.example.emo.wx.pojo.TbUser
*/
public interface TbUserMapper {
    public boolean haveRootUser();

    public int insert(HashMap param);

    public Integer searchIdByOpenId(String openId);

    TbUser searchById(int userId);
    public Set<String> searchUserPermissions(int userId);
}




