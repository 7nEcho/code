package pox.com.piteagents.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pox.com.piteagents.config.PaginationProperties;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 分页工具类
 * <p>
 * 提供统一的分页功能，包括参数校验、安全检查和标准化的分页对象创建。
 * 基于 MyBatis-Plus 分页插件实现。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaginationUtils {

    private final PaginationProperties paginationProperties;

    /**
     * 允许的排序字段白名单
     * <p>
     * 防止 SQL 注入攻击，只允许这些字段用于排序
     * </p>
     */
    private static final Set<String> ALLOWED_SORT_FIELDS = new HashSet<>(Arrays.asList(
            "id", "createdAt", "updatedAt", "name", "title", "status", 
            "sortOrder", "priority", "messageCount", "totalTokens"
    ));

    /**
     * 创建分页对象
     * <p>
     * 根据页码和页面大小创建 MyBatis-Plus 分页对象，
     * 使用配置文件中的默认排序设置。
     * </p>
     *
     * @param pageNum  页码（从1开始，null时使用默认值1）
     * @param pageSize 页面大小（null时使用配置的默认值）
     * @param <T>      分页数据类型
     * @return MyBatis-Plus 分页对象
     */
    public <T> IPage<T> createPage(Integer pageNum, Integer pageSize) {
        return createPage(pageNum, pageSize, null, null);
    }

    /**
     * 创建分页对象（带排序）
     * <p>
     * 根据分页和排序参数创建 MyBatis-Plus 分页对象。
     * 包含参数校验和安全检查。
     * </p>
     *
     * @param pageNum       页码（从1开始，null时使用默认值1）
     * @param pageSize      页面大小（null时使用配置的默认值）
     * @param sortField     排序字段（null时使用配置的默认字段）
     * @param sortDirection 排序方向（ASC/DESC，null时使用配置的默认方向）
     * @param <T>           分页数据类型
     * @return MyBatis-Plus 分页对象
     * @throws IllegalArgumentException 当排序字段不在白名单中时抛出
     */
    public <T> IPage<T> createPage(Integer pageNum, Integer pageSize, String sortField, String sortDirection) {
        // 获取安全的分页参数
        long safePageNum = paginationProperties.getSafePageNum(pageNum);
        long safePageSize = paginationProperties.getSafePageSize(pageSize);
        String safeSortField = paginationProperties.getSafeSortField(sortField);
        String safeSortDirection = paginationProperties.getSafeSortDirection(sortDirection);

        log.debug("创建分页对象 - 页码: {}, 页面大小: {}, 排序字段: {}, 排序方向: {}", 
                safePageNum, safePageSize, safeSortField, safeSortDirection);

        // 验证排序字段安全性
        validateSortField(safeSortField);

        // 创建分页对象
        Page<T> page = new Page<>(safePageNum, safePageSize);

        // 设置排序
        if ("ASC".equalsIgnoreCase(safeSortDirection)) {
            page.addOrder(OrderItem.asc(convertToDbField(safeSortField)));
        } else {
            page.addOrder(OrderItem.desc(convertToDbField(safeSortField)));
        }

        return page;
    }

    /**
     * 创建简单分页对象
     * <p>
     * 用于不需要复杂排序的简单分页查询。
     * 使用默认的创建时间降序排列。
     * </p>
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param <T>      分页数据类型
     * @return MyBatis-Plus 分页对象
     */
    public <T> IPage<T> createSimplePage(Integer pageNum, Integer pageSize) {
        long safePageNum = paginationProperties.getSafePageNum(pageNum);
        long safePageSize = paginationProperties.getSafePageSize(pageSize);
        
        return new Page<>(safePageNum, safePageSize);
    }

    /**
     * 验证排序字段是否安全
     * <p>
     * 检查排序字段是否在白名单中，防止 SQL 注入攻击。
     * </p>
     *
     * @param sortField 排序字段
     * @throws IllegalArgumentException 当排序字段不在白名单中时抛出
     */
    private void validateSortField(String sortField) {
        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            log.warn("尝试使用不安全的排序字段: {}", sortField);
            throw new IllegalArgumentException("不支持的排序字段: " + sortField);
        }
    }

    /**
     * 将 Java 字段名转换为数据库字段名
     * <p>
     * 将驼峰命名转换为下划线命名，如 createdAt -> created_at
     * </p>
     *
     * @param javaField Java 字段名
     * @return 数据库字段名
     */
    private String convertToDbField(String javaField) {
        // 简单的驼峰转下划线实现
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < javaField.length(); i++) {
            char c = javaField.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * 记录分页查询信息
     * <p>
     * 用于调试和监控分页查询的执行情况。
     * </p>
     *
     * @param page   分页对象
     * @param action 操作描述
     */
    public void logPaginationInfo(IPage<?> page, String action) {
        log.info("{} - 当前页: {}, 页面大小: {}, 总记录数: {}, 总页数: {}", 
                action, page.getCurrent(), page.getSize(), page.getTotal(), page.getPages());
    }

    /**
     * 检查分页结果是否有效
     * <p>
     * 验证分页查询结果是否正常。
     * </p>
     *
     * @param page 分页结果
     * @return 是否有效
     */
    public boolean isValidPage(IPage<?> page) {
        return page != null && page.getCurrent() > 0 && page.getSize() > 0;
    }
}
