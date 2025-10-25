package pox.com.piteagents.common.utils;

import org.springframework.stereotype.Component;

/**
 * 字符串转换工具类
 * <p>
 * 提供字符串格式转换功能，如命名风格转换、大小写转换等。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Component
public class StringConversionUtils {

    /**
     * 将 Java 驼峰命名转换为数据库下划线命名
     * <p>
     * 例如：createdAt -> created_at, userName -> user_name
     * 适用于将 Java 实体字段映射到数据库列名。
     * </p>
     *
     * @param camelCase 驼峰命名字符串
     * @return 下划线命名字符串
     */
    public String camelToSnakeCase(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
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
     * 将数据库下划线命名转换为 Java 驼峰命名
     * <p>
     * 例如：created_at -> createdAt, user_name -> userName
     * 适用于将数据库列名映射到 Java 实体字段。
     * </p>
     *
     * @param snakeCase 下划线命名字符串
     * @return 驼峰命名字符串
     */
    public String snakeToCamelCase(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return snakeCase;
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;

        for (int i = 0; i < snakeCase.length(); i++) {
            char c = snakeCase.charAt(i);
            if (c == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        return result.toString();
    }

    /**
     * 将字符串首字母大写
     * <p>
     * 例如：name -> Name, userName -> UserName
     * </p>
     *
     * @param str 输入字符串
     * @return 首字母大写的字符串
     */
    public String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 将字符串首字母小写
     * <p>
     * 例如：Name -> name, UserName -> userName
     * </p>
     *
     * @param str 输入字符串
     * @return 首字母小写的字符串
     */
    public String uncapitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 截断字符串到指定长度，并添加省略号
     * <p>
     * 如果字符串长度超过指定长度，则截断并添加 "..."。
     * 适用于日志输出、摘要显示等场景。
     * </p>
     *
     * @param str       输入字符串
     * @param maxLength 最大长度（不包括省略号）
     * @return 截断后的字符串
     */
    public String truncate(String str, int maxLength) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }

    /**
     * 截断字符串到指定长度，并添加自定义后缀
     * <p>
     * 如果字符串长度超过指定长度，则截断并添加指定后缀。
     * </p>
     *
     * @param str       输入字符串
     * @param maxLength 最大长度（不包括后缀）
     * @param suffix    后缀字符串
     * @return 截断后的字符串
     */
    public String truncate(String str, int maxLength, String suffix) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + (suffix != null ? suffix : "");
    }

    /**
     * 检查字符串是否为空白（null、空字符串或只包含空白字符）
     * <p>
     * 与 Spring 的 StringUtils.hasText() 功能相反。
     * </p>
     *
     * @param str 输入字符串
     * @return true 表示空白，false 表示非空白
     */
    public boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 检查字符串是否不为空白
     * <p>
     * 与 isBlank() 功能相反。
     * </p>
     *
     * @param str 输入字符串
     * @return true 表示非空白，false 表示空白
     */
    public boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}

