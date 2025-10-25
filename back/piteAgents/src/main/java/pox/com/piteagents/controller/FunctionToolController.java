package pox.com.piteagents.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import pox.com.piteagents.entity.dto.common.ApiResponse;
import pox.com.piteagents.entity.dto.request.ToolCreateRequest;
import pox.com.piteagents.entity.dto.request.ToolUpdateRequest;
import pox.com.piteagents.entity.dto.response.ToolDefinitionDTO;
import pox.com.piteagents.service.IToolService;

/**
 * 工具管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/tools")
@RequiredArgsConstructor
public class FunctionToolController {

    private final IToolService toolService;

    /**
     * 创建工具
     *
     * @param request 请求参数
     * @return 工具定义
     */
    @PostMapping
    public ApiResponse<ToolDefinitionDTO> createTool(@Valid @RequestBody ToolCreateRequest request) {
        log.info("收到创建工具请求: {}", request.getName());
        ToolDefinitionDTO tool = toolService.createTool(request);
        return ApiResponse.success(tool);
    }

    /**
     * 更新工具
     *
     * @param id 工具 ID
     * @param request 请求参数
     * @return 工具定义
     */
    @PutMapping("/{id}")
    public ApiResponse<ToolDefinitionDTO> updateTool(@PathVariable Long id,
                                                     @Valid @RequestBody ToolUpdateRequest request) {
        log.info("收到更新工具请求，ID: {}", id);
        ToolDefinitionDTO tool = toolService.updateTool(id, request);
        return ApiResponse.success(tool);
    }

    /**
     * 删除工具
     *
     * @param id 工具 ID
     * @return 成功消息
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTool(@PathVariable Long id) {
        log.info("收到删除工具请求，ID: {}", id);
        toolService.deleteTool(id);
        return ApiResponse.success("工具删除成功");
    }

    /**
     * 分页查询工具列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param isActive 是否启用
     * @return 工具分页列表
     */
    @GetMapping
    public ApiResponse<Page<ToolDefinitionDTO>> listTools(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean isActive) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ToolDefinitionDTO> tools = toolService.listTools(isActive, pageable);
        return ApiResponse.success(tools);
    }

    /**
     * 查询单个工具
     *
     * @param id 工具 ID
     * @return 工具定义
     */
    @GetMapping("/{id}")
    public ApiResponse<ToolDefinitionDTO> getTool(@PathVariable Long id) {
        log.info("查询工具详情，ID: {}", id);
        ToolDefinitionDTO tool = toolService.getTool(id);
        return ApiResponse.success(tool);
    }
}
