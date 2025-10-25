package pox.com.piteagents.service.functiontool.builtin;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pox.com.piteagents.service.functiontool.GLMUnifiedTool;
import pox.com.piteagents.service.functiontool.ParameterSchemaBuilderFofGLM;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取当前时间工具（内置工具示例）
 * <p>
 * 根据指定的时区返回当前时间。
 * </p>
 *
 * @author piteAgents
 * @since 2.0.0
 */
@Slf4j
@Component
public class CurrentTimeTool implements GLMUnifiedTool {

    private static final String TOOL_NAME = "get_current_time";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String getName() {
        return TOOL_NAME;
    }

    @Override
    public String getDescription() {
        return "获取当前时间，可以指定时区。支持的时区格式如: Asia/Shanghai, America/New_York, UTC 等。";
    }

    @Override
    public Map<String, Object> getParametersSchema() {
        return ParameterSchemaBuilderFofGLM.create()
                .addStringProperty("timezone", "时区标识（可选，默认为系统时区）", false)
                .build();
    }

    @Override
    public String execute(Map<String, Object> parameters) {
        try {
            // 获取时区参数
            String timezone = (String) parameters.get("timezone");
            ZoneId zoneId;
            
            if (timezone != null && !timezone.trim().isEmpty()) {
                try {
                    zoneId = ZoneId.of(timezone);
                } catch (Exception e) {
                    log.warn("无效的时区: {}，使用系统默认时区", timezone);
                    zoneId = ZoneId.systemDefault();
                }
            } else {
                zoneId = ZoneId.systemDefault();
            }

            // 获取当前时间
            ZonedDateTime now = ZonedDateTime.now(zoneId);
            String formattedTime = now.format(FORMATTER);

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("timezone", zoneId.getId());
            result.put("time", formattedTime);
            result.put("timestamp", now.toEpochSecond());

            // 返回 JSON 格式的结果
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(result);

        } catch (Exception e) {
            log.error("获取当前时间失败: {}", e.getMessage(), e);
            return "{\"error\": \"获取当前时间失败: " + e.getMessage() + "\"}";
        }
    }

    @Override
    public ToolType getToolType() {
        return ToolType.BUILTIN;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}

