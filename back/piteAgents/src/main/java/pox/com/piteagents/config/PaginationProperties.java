package pox.com.piteagents.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 分页配置属性类
 * <p>
 * 从 application.yml 读取分页相关配置，提供全局统一的分页参数。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "pite-agents.pagination")
public class PaginationProperties {

    /**
     * 默认页面大小
     * <p>
     * 当用户未指定页面大小时使用此值
     * </p>
     */
    private int defaultPageSize = 20;

    /**
     * 最大页面大小
     * <p>
     * 防止恶意查询设置过大的页面大小，保护系统性能
     * </p>
     */
    private int maxPageSize = 1000;

    /**
     * 默认排序字段
     * <p>
     * 当用户未指定排序字段时使用此值
     * </p>
     */
    private String defaultSortField = "createdAt";

    /**
     * 默认排序方向
     * <p>
     * 可选值：ASC（升序）、DESC（降序）
     * </p>
     */
    private String defaultSortDirection = "DESC";

    /**
     * 获取安全的页面大小
     * <p>
     * 如果指定的页面大小超过最大值，则返回最大值；
     * 如果小于等于0，则返回默认值。
     * </p>
     *
     * @param pageSize 用户指定的页面大小
     * @return 安全的页面大小
     */
    public int getSafePageSize(Integer pageSize) {
        if (pageSize == null || pageSize <= 0) {
            return this.defaultPageSize;
        }
        return Math.min(pageSize, this.maxPageSize);
    }

    /**
     * 获取安全的页码
     * <p>
     * 确保页码不小于1（MyBatis-Plus 分页从1开始）
     * </p>
     *
     * @param pageNum 用户指定的页码
     * @return 安全的页码
     */
    public long getSafePageNum(Integer pageNum) {
        if (pageNum == null || pageNum <= 0) {
            return 1L;
        }
        return pageNum.longValue();
    }

    /**
     * 获取安全的排序字段
     * <p>
     * 如果用户未指定排序字段或字段为空，则返回默认排序字段
     * </p>
     *
     * @param sortField 用户指定的排序字段
     * @return 安全的排序字段
     */
    public String getSafeSortField(String sortField) {
        if (sortField == null || sortField.trim().isEmpty()) {
            return this.defaultSortField;
        }
        return sortField.trim();
    }

    /**
     * 获取安全的排序方向
     * <p>
     * 如果用户未指定排序方向或方向不合法，则返回默认排序方向
     * </p>
     *
     * @param sortDirection 用户指定的排序方向
     * @return 安全的排序方向
     */
    public String getSafeSortDirection(String sortDirection) {
        if (sortDirection == null || sortDirection.trim().isEmpty()) {
            return this.defaultSortDirection;
        }
        
        String direction = sortDirection.trim().toUpperCase();
        if (!"ASC".equals(direction) && !"DESC".equals(direction)) {
            return this.defaultSortDirection;
        }
        
        return direction;
    }
}
