package pox.com.piteagents.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pox.com.piteagents.constant.MessageRole;
import pox.com.piteagents.entity.dto.common.Message;
import pox.com.piteagents.entity.dto.request.ChatRequest;
import pox.com.piteagents.entity.dto.response.ChatResponse;
import pox.com.piteagents.exception.ZhipuApiException;
import pox.com.piteagents.service.IZhipuService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ChatController 测试类
 * <p>
 * 测试对话控制器的所有REST API接口，包括正常情况和异常情况。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@WebMvcTest(ChatController.class)
@DisplayName("对话控制器测试")
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IZhipuService zhipuService;

    private ChatRequest validChatRequest;
    private ChatResponse mockChatResponse;

    /**
     * 测试前准备
     * 初始化测试数据
     */
    @BeforeEach
    void setUp() {
        // 准备有效的请求对象
        validChatRequest = ChatRequest.builder()
                .messages(List.of(
                        Message.builder()
                                .role(MessageRole.USER)
                                .content("你好")
                                .build()
                ))
                .model("glm-4.5")
                .temperature(0.7)
                .build();

        // 准备模拟的响应对象
        mockChatResponse = ChatResponse.builder()
                .id("test-response-id")
                .model("glm-4.5")
                .content("你好！我是智谱AI助手。")
                .finishReason("stop")
                .usage(ChatResponse.TokenUsage.builder()
                        .promptTokens(5)
                        .completionTokens(10)
                        .totalTokens(15)
                        .build())
                .build();
    }

    // ==================== 同步对话接口测试 ====================

    @Test
    @DisplayName("同步对话 - 成功")
    void testChat_Success() throws Exception {
        // 模拟Service返回
        when(zhipuService.chat(any(ChatRequest.class))).thenReturn(mockChatResponse);

        // 执行请求并验证
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validChatRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value("test-response-id"))
                .andExpect(jsonPath("$.data.model").value("glm-4.5"))
                .andExpect(jsonPath("$.data.content").value("你好！我是智谱AI助手。"))
                .andExpect(jsonPath("$.data.finishReason").value("stop"))
                .andExpect(jsonPath("$.data.usage.promptTokens").value(5))
                .andExpect(jsonPath("$.data.usage.completionTokens").value(10))
                .andExpect(jsonPath("$.data.usage.totalTokens").value(15));

        // 验证Service被调用
        verify(zhipuService, times(1)).chat(any(ChatRequest.class));
    }

    @Test
    @DisplayName("同步对话 - 带系统提示")
    void testChat_WithSystemMessage() throws Exception {
        // 创建带系统提示的请求
        ChatRequest requestWithSystem = ChatRequest.builder()
                .messages(List.of(
                        Message.builder()
                                .role(MessageRole.SYSTEM)
                                .content("你是一个专业的编程助手")
                                .build(),
                        Message.builder()
                                .role(MessageRole.USER)
                                .content("如何学习Java？")
                                .build()
                ))
                .model("glm-4.5")
                .build();

        when(zhipuService.chat(any(ChatRequest.class))).thenReturn(mockChatResponse);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithSystem)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(zhipuService, times(1)).chat(any(ChatRequest.class));
    }

    @Test
    @DisplayName("同步对话 - 自定义温度参数")
    void testChat_WithCustomTemperature() throws Exception {
        ChatRequest customRequest = ChatRequest.builder()
                .messages(List.of(Message.user("测试")))
                .temperature(0.9)
                .build();

        when(zhipuService.chat(any(ChatRequest.class))).thenReturn(mockChatResponse);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(zhipuService, times(1)).chat(any(ChatRequest.class));
    }

    @Test
    @DisplayName("同步对话 - 参数校验失败（空消息列表）")
    void testChat_ValidationFailed_EmptyMessages() throws Exception {
        // 创建空消息列表的请求
        ChatRequest invalidRequest = ChatRequest.builder()
                .messages(List.of())
                .build();

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("参数校验失败"));

        // 验证Service未被调用
        verify(zhipuService, never()).chat(any(ChatRequest.class));
    }

    @Test
    @DisplayName("同步对话 - 参数校验失败（消息角色为空）")
    void testChat_ValidationFailed_NullRole() throws Exception {
        String requestJson = """
                {
                    "messages": [
                        {
                            "role": "",
                            "content": "测试内容"
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verify(zhipuService, never()).chat(any(ChatRequest.class));
    }

    @Test
    @DisplayName("同步对话 - 参数校验失败（温度参数超出范围）")
    void testChat_ValidationFailed_InvalidTemperature() throws Exception {
        ChatRequest invalidRequest = ChatRequest.builder()
                .messages(List.of(Message.user("测试")))
                .temperature(1.5) // 超出范围
                .build();

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verify(zhipuService, never()).chat(any(ChatRequest.class));
    }

    @Test
    @DisplayName("同步对话 - Service抛出异常")
    void testChat_ServiceThrowsException() throws Exception {
        // 模拟Service抛出异常
        when(zhipuService.chat(any(ChatRequest.class)))
                .thenThrow(new ZhipuApiException(500, "智谱API调用失败"));

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validChatRequest)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("智谱API调用失败"));

        verify(zhipuService, times(1)).chat(any(ChatRequest.class));
    }

    // ==================== 流式对话接口测试 ====================

    @Test
    @DisplayName("流式对话 - 成功")
    void testStreamChat_Success() throws Exception {
        // 创建模拟的SSE Emitter
        SseEmitter mockEmitter = new SseEmitter();
        when(zhipuService.streamChat(any(ChatRequest.class))).thenReturn(mockEmitter);

        MvcResult result = mockMvc.perform(post("/api/chat/stream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validChatRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        // 验证Service被调用
        verify(zhipuService, times(1)).streamChat(any(ChatRequest.class));
    }

    @Test
    @DisplayName("流式对话 - 参数校验失败")
    void testStreamChat_ValidationFailed() throws Exception {
        ChatRequest invalidRequest = ChatRequest.builder()
                .messages(List.of())
                .build();

        mockMvc.perform(post("/api/chat/stream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verify(zhipuService, never()).streamChat(any(ChatRequest.class));
    }

    @Test
    @DisplayName("流式对话 - Service抛出异常")
    void testStreamChat_ServiceThrowsException() throws Exception {
        // 创建会立即失败的Emitter
        SseEmitter failedEmitter = new SseEmitter();
        failedEmitter.completeWithError(new ZhipuApiException("流式调用失败"));
        
        when(zhipuService.streamChat(any(ChatRequest.class))).thenReturn(failedEmitter);

        mockMvc.perform(post("/api/chat/stream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validChatRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted());

        verify(zhipuService, times(1)).streamChat(any(ChatRequest.class));
    }

    // ==================== 获取模型列表接口测试 ====================

    @Test
    @DisplayName("获取模型列表 - 成功")
    void testGetModels_Success() throws Exception {
        mockMvc.perform(get("/api/models"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data[0].code").exists())
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].description").exists());

        // 验证Service未被调用（因为直接从枚举获取）
        verifyNoInteractions(zhipuService);
    }

    @Test
    @DisplayName("获取模型列表 - 验证返回的模型信息")
    void testGetModels_VerifyModelInfo() throws Exception {
        mockMvc.perform(get("/api/models"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.code=='glm-4-5')]").exists())
                .andExpect(jsonPath("$.data[?(@.code=='glm-4.6')]").exists())
                .andExpect(jsonPath("$.data[?(@.code=='glm-4-5')].name").value("GLM-4.5"))
                .andExpect(jsonPath("$.data[?(@.code=='glm-4.6')].name").value("GLM-4.6"));
    }

    // ==================== 综合测试 ====================

    @Test
    @DisplayName("多轮对话 - 带历史消息")
    void testChat_MultiRound() throws Exception {
        ChatRequest multiRoundRequest = ChatRequest.builder()
                .messages(List.of(
                        Message.user("什么是Java？"),
                        Message.assistant("Java是一种编程语言..."),
                        Message.user("它有什么特点？")
                ))
                .build();

        when(zhipuService.chat(any(ChatRequest.class))).thenReturn(mockChatResponse);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(multiRoundRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(zhipuService, times(1)).chat(any(ChatRequest.class));
    }

    @Test
    @DisplayName("测试所有可选参数")
    void testChat_AllOptionalParameters() throws Exception {
        ChatRequest fullRequest = ChatRequest.builder()
                .messages(List.of(Message.user("测试")))
                .model("glm-4.5")
                .temperature(0.8)
                .maxTokens(1000)
                .topP(0.9)
                .build();

        when(zhipuService.chat(any(ChatRequest.class))).thenReturn(mockChatResponse);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fullRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(zhipuService, times(1)).chat(any(ChatRequest.class));
    }

    @Test
    @DisplayName("测试请求Content-Type不正确")
    void testChat_WrongContentType() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(validChatRequest)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());

        verify(zhipuService, never()).chat(any(ChatRequest.class));
    }

    @Test
    @DisplayName("测试请求Body格式错误")
    void testChat_InvalidJson() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(zhipuService, never()).chat(any(ChatRequest.class));
    }

    @Test
    @DisplayName("测试不支持的HTTP方法")
    void testChat_UnsupportedMethod() throws Exception {
        mockMvc.perform(get("/api/chat"))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());

        verify(zhipuService, never()).chat(any(ChatRequest.class));
    }

    @Test
    @DisplayName("测试路径不存在")
    void testChat_NotFoundPath() throws Exception {
        mockMvc.perform(post("/api/chat/invalid"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(zhipuService, never()).chat(any(ChatRequest.class));
    }
}

