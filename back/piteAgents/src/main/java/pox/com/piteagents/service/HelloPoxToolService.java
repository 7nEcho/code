package pox.com.piteagents.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * HelloPox 工具服务
 * <p>
 * 用于 POC 测试的简单工具，返回固定的问候语。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Slf4j
@Service
public class HelloPoxToolService {

    /**
     * 执行工具
     * <p>
     * 返回简单的问候语 "hello pox"
     * </p>
     *
     * @return 问候语字符串
     */
    public String execute() {
        log.info("执行 HelloPox 工具");
        return "hello pox";
    }
}

