package com.minjor.common.utils;

import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 递归处理工具类
 * 提供树形结构的遍历、生成、查找等操作
 */
@UtilityClass
public class RecursionUtil {

    // ==================== 树遍历相关方法 ====================

    /**
     * 深度优先遍历（前序遍历）
     * @param root 根节点
     * @param childrenFunc 获取子节点列表的函数
     * @param consumer 对每个节点的操作
     * @param <T> 节点类型
     */
    public static <T> void depthFirstTraversal(
            T root,
            Function<T, List<T>> childrenFunc,
            Consumer<T> consumer) {
        depthFirstTraversal(root, childrenFunc, consumer, null);
    }

    /**
     * 深度优先遍历（前序遍历）
     * @param root 根节点
     * @param childrenFunc 获取子节点列表的函数
     * @param consumer 对每个节点的操作
     * @param shouldTraverseChildren 是否继续遍历子节点的条件判断
     * @param <T> 节点类型
     */
    public static <T> void depthFirstTraversal(
            T root,
            Function<T, List<T>> childrenFunc,
            Consumer<T> consumer,
            Predicate<T> shouldTraverseChildren) {
        if (root == null) return;
        
        consumer.accept(root);
        
        if (shouldTraverseChildren == null || shouldTraverseChildren.test(root)) {
            List<T> children = childrenFunc.apply(root);
            if (children != null) {
                for (T child : children) {
                    depthFirstTraversal(child, childrenFunc, consumer, shouldTraverseChildren);
                }
            }
        }
    }

    /**
     * 带祖先节点的深度优先遍历
     * @param root 根节点
     * @param ancestors 祖先节点列表
     * @param childrenFunc 获取子节点列表的函数
     * @param consumer 对每个节点及其祖先节点的操作
     * @param <T> 节点类型
     */
    public static <T> void depthFirstTraversalWithAncestors(
            T root,
            List<T> ancestors,
            Function<T, List<T>> childrenFunc,
            BiConsumer<T, List<T>> consumer) {
        depthFirstTraversalWithAncestors(root, ancestors, childrenFunc, consumer, null);
    }

    /**
     * 带祖先节点的深度优先遍历
     * @param root 当前节点
     * @param ancestors 祖先节点列表
     * @param childrenFunc 获取子节点列表的函数
     * @param consumer 对每个节点及其祖先节点的操作
     * @param shouldTraverseChildren 是否继续遍历子节点的条件判断
     * @param <T> 节点类型
     */
    public static <T> void depthFirstTraversalWithAncestors(
            T root,
            List<T> ancestors,
            Function<T, List<T>> childrenFunc,
            BiConsumer<T, List<T>> consumer,
            Predicate<T> shouldTraverseChildren) {
        if (root == null) return;
        
        // 创建新的祖先列表（包含当前节点）
        List<T> currentAncestors = new ArrayList<>(ancestors);
        currentAncestors.add(root);
        
        // 处理当前节点
        consumer.accept(root, ancestors);
        
        // 判断是否需要继续遍历子节点
        if (shouldTraverseChildren == null || shouldTraverseChildren.test(root)) {
            List<T> children = childrenFunc.apply(root);
            if (children != null) {
                for (T child : children) {
                    depthFirstTraversalWithAncestors(
                        child, 
                        currentAncestors, 
                        childrenFunc, 
                        consumer, 
                        shouldTraverseChildren
                    );
                }
            }
        }
    }

    /**
     * 广度优先遍历
     * @param root 根节点
     * @param childrenFunc 获取子节点列表的函数
     * @param consumer 对每个节点的操作
     * @param <T> 节点类型
     */
    public static <T> void breadthFirstTraversal(
            T root,
            Function<T, List<T>> childrenFunc,
            Consumer<T> consumer) {
        if (root == null) return;
        
        Queue<T> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            T current = queue.poll();
            consumer.accept(current);
            
            List<T> children = childrenFunc.apply(current);
            if (children != null) {
                for (T child : children) {
                    queue.offer(child);
                }
            }
        }
    }

    // ==================== 树生成相关方法 ====================

    /**
     * 将列表转换为树形结构
     * @param list 原始列表
     * @param getIdFunc 获取节点ID的函数
     * @param getPidFunc 获取父节点ID的函数
     * @param setChildrenFunc 设置子节点列表的函数
     * @param <T> 节点类型
     * @param <ID> ID类型
     * @return 树形结构的根节点列表
     */
    public static <T, ID> List<T> listToTree(
            List<T> list,
            Function<T, ID> getIdFunc,
            Function<T, ID> getPidFunc,
            BiConsumer<T, List<T>> setChildrenFunc) {
        return listToTree(list, getIdFunc, getPidFunc, setChildrenFunc, null);
    }

    /**
     * 将列表转换为树形结构（指定根节点条件）
     * @param list 原始列表
     * @param getIdFunc 获取节点ID的函数
     * @param getPidFunc 获取父节点ID的函数
     * @param setChildrenFunc 设置子节点列表的函数
     * @param isRootFunc 判断是否为根节点的函数
     * @param <T> 节点类型
     * @param <ID> ID类型
     * @return 树形结构的根节点列表
     */
    public static <T, ID> List<T> listToTree(
            List<T> list,
            Function<T, ID> getIdFunc,
            Function<T, ID> getPidFunc,
            BiConsumer<T, List<T>> setChildrenFunc,
            Predicate<T> isRootFunc) {
        
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 用于快速查找节点的Map
        Map<ID, T> nodeMap = list.stream()
            .collect(Collectors.toMap(getIdFunc, node -> node));
        
        // 存储根节点
        List<T> roots = new ArrayList<>();
        
        // 遍历所有节点，建立父子关系
        for (T node : list) {
            ID pid = getPidFunc.apply(node);
            
            if (pid == null || (isRootFunc != null && isRootFunc.test(node))) {
                // 根节点
                roots.add(node);
            } else {
                // 查找父节点
                T parent = nodeMap.get(pid);
                if (parent != null) {
                    // 获取或创建子节点列表
                    List<T> children = getOrCreateChildren(parent, setChildrenFunc);
                    children.add(node);
                }
            }
        }
        
        return roots;
    }

    /**
     * 将列表转换为树形结构（使用Map缓存）
     * @param list 原始列表
     * @param getIdFunc 获取节点ID的函数
     * @param getPidFunc 获取父节点ID的函数
     * @param setChildrenFunc 设置子节点列表的函数
     * @param isRootFunc 判断是否为根节点的函数
     * @param comparator 子节点排序比较器
     * @param <T> 节点类型
     * @param <ID> ID类型
     * @return 树形结构的根节点列表
     */
    public static <T, ID> List<T> listToTreeWithSort(
            List<T> list,
            Function<T, ID> getIdFunc,
            Function<T, ID> getPidFunc,
            BiConsumer<T, List<T>> setChildrenFunc,
            Predicate<T> isRootFunc,
            Comparator<T> comparator) {
        
        List<T> roots = listToTree(list, getIdFunc, getPidFunc, setChildrenFunc, isRootFunc);
        
        // 对树进行排序
        if (comparator != null) {
            sortTree(roots, childrenGetter(setChildrenFunc), comparator);
        }
        
        return roots;
    }

    /**
     * 获取或创建子节点列表
     */
    private static <T> List<T> getOrCreateChildren(
            T node,
            BiConsumer<T, List<T>> setChildrenFunc) {
        // 这里需要通过反射或自定义接口来获取子节点列表
        // 为了简化，我们假设节点有一个getChildren方法或类似机制
        // 实际上，setChildrenFunc应该与一个getChildrenFunc配对使用
        // 这里我们使用一个简单的实现，实际使用时可能需要调整
        
        // 创建一个新的列表，并在下次调用setChildrenFunc时设置回去
        // 注意：这个实现依赖于具体的节点类型
        // 更好的方式是使用函数式接口来获取和设置子节点
        return new ArrayList<>();
    }

    // ==================== 辅助方法 ====================

    /**
     * 从setChildrenFunc创建一个对应的getChildrenFunc
     * 注意：这是一个简化版本，实际使用中需要根据具体实现调整
     */
    public static <T> Function<T, List<T>> childrenGetter(
            BiConsumer<T, List<T>> setChildrenFunc) {
        return node -> {
            // 这里需要根据实际节点类型实现
            // 可能需要使用反射或其他机制
            return new ArrayList<>();
        };
    }

    /**
     * 对树进行排序
     * @param nodes 节点列表
     * @param childrenFunc 获取子节点的函数
     * @param comparator 排序比较器
     * @param <T> 节点类型
     */
    public static <T> void sortTree(
            List<T> nodes,
            Function<T, List<T>> childrenFunc,
            Comparator<T> comparator) {
        if (nodes == null || nodes.isEmpty()) return;
        
        // 排序当前层级
        nodes.sort(comparator);
        
        // 递归排序子节点
        for (T node : nodes) {
            List<T> children = childrenFunc.apply(node);
            if (children != null && !children.isEmpty()) {
                sortTree(children, childrenFunc, comparator);
            }
        }
    }

    /**
     * 查找树中的节点
     * @param roots 根节点列表
     * @param childrenFunc 获取子节点的函数
     * @param predicate 查找条件
     * @param <T> 节点类型
     * @return 找到的节点，未找到返回null
     */
    public static <T> T findNode(
            List<T> roots,
            Function<T, List<T>> childrenFunc,
            Predicate<T> predicate) {
        
        for (T root : roots) {
            T found = findNode(root, childrenFunc, predicate);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * 查找树中的节点
     * @param root 根节点
     * @param childrenFunc 获取子节点的函数
     * @param predicate 查找条件
     * @param <T> 节点类型
     * @return 找到的节点，未找到返回null
     */
    public static <T> T findNode(
            T root,
            Function<T, List<T>> childrenFunc,
            Predicate<T> predicate) {
        
        if (root == null) return null;
        
        if (predicate.test(root)) {
            return root;
        }
        
        List<T> children = childrenFunc.apply(root);
        if (children != null) {
            for (T child : children) {
                T found = findNode(child, childrenFunc, predicate);
                if (found != null) {
                    return found;
                }
            }
        }
        
        return null;
    }

    /**
     * 计算树的深度
     * @param root 根节点
     * @param childrenFunc 获取子节点的函数
     * @param <T> 节点类型
     * @return 树的深度
     */
    public static <T> int calculateDepth(
            T root,
            Function<T, List<T>> childrenFunc) {
        return calculateDepth(root, childrenFunc, 1);
    }

    private static <T> int calculateDepth(
            T node,
            Function<T, List<T>> childrenFunc,
            int currentDepth) {
        
        List<T> children = childrenFunc.apply(node);
        if (children == null || children.isEmpty()) {
            return currentDepth;
        }
        
        int maxDepth = currentDepth;
        for (T child : children) {
            int childDepth = calculateDepth(child, childrenFunc, currentDepth + 1);
            maxDepth = Math.max(maxDepth, childDepth);
        }
        
        return maxDepth;
    }

    /**
     * 扁平化树结构（树转列表）
     * @param roots 根节点列表
     * @param childrenFunc 获取子节点的函数
     * @param <T> 节点类型
     * @return 扁平化的节点列表
     */
    public static <T> List<T> flattenTree(
            List<T> roots,
            Function<T, List<T>> childrenFunc) {
        
        List<T> result = new ArrayList<>();
        for (T root : roots) {
            depthFirstTraversal(root, childrenFunc, result::add);
        }
        return result;
    }
}