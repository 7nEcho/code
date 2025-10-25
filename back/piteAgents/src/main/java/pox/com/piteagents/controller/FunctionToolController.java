package pox.com.piteagents.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;
import pox.com.piteagents.entity.dto.common.ApiResponse;
import pox.com.piteagents.entity.dto.request.FunctionToolCreateRequest;
import pox.com.piteagents.entity.dto.request.FunctionToolUpdateRequest;
import pox.com.piteagents.entity.dto.response.FunctionToolDefinitionDTO;
import pox.com.piteagents.service.IFunctionToolService;
import pox.com.piteagents.common.utils.PaginationUtils;

/**
 * 工具管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/tools")
@RequiredArgsConstructor
public class FunctionToolController {

    private final IFunctionToolService toolService;
    private final PaginationUtils paginationUtils;

    /**
     * 创建工具
     *
     * @param request 请求参数
     * @return 工具定义
     */
    @PostMapping
    public ApiResponse<FunctionToolDefinitionDTO> createTool(@Valid @RequestBody FunctionToolCreateRequest request) {
        log.info("收到创建工具请求: {}", request.getName());
        FunctionToolDefinitionDTO tool = toolService.createTool(request);
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
    public ApiResponse<FunctionToolDefinitionDTO> updateTool(@PathVariable Long id,
                                                             @Valid @RequestBody FunctionToolUpdateRequest request) {
        log.info("收到更新工具请求，ID: {}", id);
        FunctionToolDefinitionDTO tool = toolService.updateTool(id, request);
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
     * @param page 页码（默认1）
     * @param size 每页大小（使用配置的默认值）
     * @param isActive 是否启用
     * @param sortField 排序字段（可选）
     * @param sortDirection 排序方向（可选）
     * @return 工具分页列表
     */
    @GetMapping
    public ApiResponse<IPage<FunctionToolDefinitionDTO>> listTools(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDirection) {

        log.info("分页查询工具列表，page: {}, size: {}, isActive: {}, sortField: {}, sortDirection: {}", 
                page, size, isActive, sortField, sortDirection);

        // 使用分页工具类创建分页对象
        IPage<FunctionToolDefinitionDTO> pageParam = paginationUtils.createPage(page, size, sortField, sortDirection);
        IPage<FunctionToolDefinitionDTO> tools = toolService.listTools(isActive, pageParam);
        
        paginationUtils.logPaginationInfo(tools, "查询工具列表");
        
        return ApiResponse.success(tools);
    }

    /**
     * 查询单个工具
     *
     * @param id 工具 ID
     * @return 工具定义
     */
    @GetMapping("/{id}")
    public ApiResponse<FunctionToolDefinitionDTO> getTool(@PathVariable Long id) {
        log.info("查询工具详情，ID: {}", id);
        FunctionToolDefinitionDTO tool = toolService.getTool(id);
        return ApiResponse.success(tool);
    }
}
