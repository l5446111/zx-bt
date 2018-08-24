package com.zx.bt.hashspider.repository;

import com.zx.bt.hashspider.entity.Node;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * author:ZhengXing
 * datetime:2018-02-15 19:45
 */
@Component
public class NodeRepository {
    /**
     * 查询 记录最多的node 前x个
     */
    //@Query(nativeQuery = true, value = "SELECT * FROM node ORDER BY id DESC LIMIT ?1")
    public List<Node> findTopXNode(int size) {
        return null;
    }

}
