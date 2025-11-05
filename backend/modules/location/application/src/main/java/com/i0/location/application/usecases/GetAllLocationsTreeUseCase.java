package com.i0.location.application.usecases;

import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.domain.entities.Location;
import com.i0.location.domain.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * 获取全量地理位置树形结构用例
 * 负责获取所有地理位置并构建树形结构的业务逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetAllLocationsTreeUseCase {

    private final LocationRepository locationRepository;

    /**
     * 执行获取全量地理位置树形结构用例
     * @return 树形结构的地理位置列表
     */
    public List<LocationOutput> execute() {
        log.info("Getting all locations in tree structure");

        // 获取所有地理位置
        List<Location> allLocations = locationRepository.findAll();
        
        log.info("Found {} total locations", allLocations.size());

        // 转换为LocationOutput
        List<LocationOutput> locationOutputs = allLocations.stream()
                .map(LocationOutput::from)
                .collect(Collectors.toList());

        // 构建树形结构
        List<LocationOutput> treeStructure = buildTreeStructure(locationOutputs);
        
        log.info("Built tree structure with {} root nodes", treeStructure.size());
        
        return treeStructure;
    }

    /**
     * 构建树形结构
     * @param locations 所有地理位置列表
     * @return 树形结构的根节点列表
     */
    private List<LocationOutput> buildTreeStructure(List<LocationOutput> locations) {
        // 创建ID到LocationOutput的映射
        Map<String, LocationOutput> locationMap = locations.stream()
                .collect(Collectors.toMap(LocationOutput::getId, location -> location));

        List<LocationOutput> rootNodes = new ArrayList<>();

        // 遍历所有位置，构建父子关系
        for (LocationOutput location : locations) {
            String parentId = location.getParentId();
            
            if (parentId == null || parentId.trim().isEmpty()) {
                // 没有父级的是根节点
                rootNodes.add(location);
            } else {
                // 有父级的添加到父级的children中
                LocationOutput parent = locationMap.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(location);
                }
            }
        }

        // 对根节点按sortOrder排序
        rootNodes.sort(Comparator.comparing(LocationOutput::getSortOrder, 
                Comparator.nullsLast(Comparator.naturalOrder())));

        // 递归对所有子节点排序
        sortChildrenRecursively(rootNodes);

        return rootNodes;
    }

    /**
     * 递归对子节点进行排序
     * @param nodes 节点列表
     */
    private void sortChildrenRecursively(List<LocationOutput> nodes) {
        for (LocationOutput node : nodes) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                // 对子节点按sortOrder排序
                node.getChildren().sort(Comparator.comparing(LocationOutput::getSortOrder, 
                        Comparator.nullsLast(Comparator.naturalOrder())));
                
                // 递归排序子节点的子节点
                sortChildrenRecursively(node.getChildren());
            }
        }
    }
}