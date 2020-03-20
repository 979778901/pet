package com.wang.pet.service.impl;

import com.wang.pet.entity.Type;
import com.wang.pet.mapper.TypeMapper;
import com.wang.pet.service.ITypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author caoxinyu
 * @since 2020-03-19
 */
@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements ITypeService {

}
