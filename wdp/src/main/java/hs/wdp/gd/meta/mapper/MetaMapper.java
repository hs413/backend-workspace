package hs.wdp.gd.meta.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MetaMapper {
    int selectCount();
}
