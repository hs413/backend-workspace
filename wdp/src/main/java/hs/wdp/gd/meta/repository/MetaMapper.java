package hs.wdp.gd.meta.repository;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MetaMapper {
    int selectCount();
}
