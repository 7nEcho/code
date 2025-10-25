# piteAgents 代码规范文档

## 1. 项目包结构规范

```
pox.com.piteagents/
├── config/              # 配置类
│   ├── CorsConfig.java           # CORS 跨域配置
│   └── ZhipuConfig.java          # 智谱 AI 配置
├── constant/            # 常量类
│   ├── FinishReason.java         # 完成原因常量
│   └── MessageRole.java          # 消息角色常量
├── controller/          # 控制器层（REST API）
│   ├── AgentController.java      # Agent 管理接口
│   ├── ChatController.java       # 对话接口
│   └── ConversationController.java # 对话历史接口
├── dto/                 # 数据传输对象
│   ├── common/          # 通用 DTO
│   │   └── ApiResponse.java      # 统一响应格式
│   ├── request/         # 请求对象
│   │   ├── AgentCreateRequest.java
│   │   ├── AgentUpdateRequest.java
│   │   ├── AgentChatRequest.java
│   │   └── ChatRequest.java
│   ├── response/        # 响应对象
│   │   ├── AgentDTO.java
│   │   ├── AgentConfigDTO.java
│   │   ├── AgentChatResponse.java
│   │   ├── ChatResponse.java
│   │   ├── ConversationSessionDTO.java
│   │   ├── ConversationMessageDTO.java
│   │   ├── StreamChatResponse.java
│   │   └── ModelInfo.java
│   └── Message.java     # 通用消息对象
├── entity/              # 实体类（数据库映射）
│   ├── base/            # 基础实体类
│   │   ├── BaseEntity.java       # 基础实体（ID、时间戳）
│   │   └── SoftDeleteEntity.java # 支持软删除的实体
│   ├── Agent.java               # Agent 实体
│   ├── AgentConfig.java         # Agent 配置实体
│   ├── AgentTool.java           # Agent-工具关联
│   ├── AgentKnowledge.java      # Agent-知识库关联
│   ├── ConversationSession.java # 会话实体
│   ├── ConversationMessage.java # 消息实体
│   ├── ToolDefinition.java      # 工具定义实体
│   └── KnowledgeBase.java       # 知识库实体
├── enums/               # 枚举类
│   ├── AgentStatusEnum.java     # Agent 状态枚举
│   ├── ChatModeEnum.java        # 对话模式枚举
│   ├── ContentTypeEnum.java     # 内容类型枚举
│   ├── HttpMethodEnum.java      # HTTP 方法枚举
│   └── ZhipuModelEnum.java      # 智谱模型枚举
├── exception/           # 异常类
│   ├── GlobalExceptionHandler.java # 全局异常处理器
│   └── ZhipuApiException.java      # 智谱 API 异常
├── repository/          # 数据访问层
│   ├── AgentRepository.java
│   ├── AgentConfigRepository.java
│   ├── AgentToolRepository.java
│   ├── AgentKnowledgeRepository.java
│   ├── ConversationSessionRepository.java
│   ├── ConversationMessageRepository.java
│   ├── ToolDefinitionRepository.java
│   └── KnowledgeBaseRepository.java
├── service/             # 业务逻辑层
│   ├── AgentService.java         # Agent 业务服务
│   ├── ConversationService.java  # 对话历史服务
│   └── ZhipuService.java         # 智谱 AI 服务
└── PiteAgentsApplication.java    # 应用入口
```

## 2. 分层架构规范

### 2.1 Controller 层

**职责**：
- 接收 HTTP 请求
- 参数验证（使用 @Valid）
- 调用 Service 层
- 返回统一格式的响应（ApiResponse）
- 不包含业务逻辑

**规范**：
```java
@Slf4j
@RestController
@RequestMapping("/api/xxx")
@RequiredArgsConstructor
public class XxxController {
    
    private final XxxService xxxService;
    
    @PostMapping
    public ApiResponse<XxxDTO> create(@Valid @RequestBody XxxRequest request) {
        log.info("收到创建请求...");
        XxxDTO result = xxxService.create(request);
        return ApiResponse.success(result);
    }
}
```

### 2.2 Service 层

**职责**：
- 实现核心业务逻辑
- 事务管理（@Transactional）
- 调用 Repository 层
- 实体与 DTO 之间的转换
- 业务规则验证

**规范**：
```java
@Slf4j
@Service
@RequiredArgsConstructor
public class XxxService {
    
    private final XxxRepository xxxRepository;
    
    @Transactional(rollbackFor = Exception.class)
    public XxxDTO create(XxxRequest request) {
        // 1. 参数验证
        // 2. 业务逻辑处理
        // 3. 调用 Repository
        // 4. 转换为 DTO 返回
    }
    
    @Transactional(readOnly = true)
    public XxxDTO get(Long id) {
        // 只读事务
    }
}
```

### 2.3 Repository 层

**职责**：
- 数据访问操作
- 继承 JpaRepository
- 定义查询方法（遵循 Spring Data JPA 命名规范）
- 自定义 JPQL 查询

**规范**：
```java
@Repository
public interface XxxRepository extends JpaRepository<Xxx, Long> {
    
    // 遵循命名规范的查询方法
    Optional<Xxx> findByName(String name);
    Page<Xxx> findByCategory(String category, Pageable pageable);
    
    // 自定义 JPQL 查询
    @Query("SELECT x FROM Xxx x WHERE x.name LIKE %:keyword%")
    List<Xxx> searchByKeyword(@Param("keyword") String keyword);
}
```

### 2.4 Entity 层

**职责**：
- 数据库表映射
- 字段定义和约束
- 关联关系定义
- 继承 BaseEntity 或 SoftDeleteEntity

**规范**：
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "xxx")
public class Xxx extends BaseEntity {
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    // 一对一关联
    @OneToOne(mappedBy = "xxx", cascade = CascadeType.ALL)
    private XxxConfig config;
    
    // 一对多关联
    @OneToMany(mappedBy = "xxx", cascade = CascadeType.ALL)
    private List<XxxItem> items = new ArrayList<>();
}
```

## 3. DTO 分类规范

### 3.1 Request 对象（dto/request/）

用于接收客户端请求：
- 以 `Request` 或 `Param` 结尾
- 包含参数验证注解（@NotNull, @NotBlank, @Size 等）
- 只包含业务必需的字段

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XxxCreateRequest {
    
    @NotBlank(message = "名称不能为空")
    @Size(max = 100, message = "名称长度不能超过100")
    private String name;
    
    @Valid
    private XxxConfigDTO config;
}
```

### 3.2 Response 对象（dto/response/）

用于返回数据给客户端：
- 以 `DTO`、`Response` 或 `VO` 结尾
- 包含展示所需的所有字段
- 可包含关联对象的详细信息

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XxxDTO {
    
    private Long id;
    private String name;
    private XxxConfigDTO config;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 3.3 Common 对象（dto/common/）

通用的 DTO：
- ApiResponse - 统一响应格式
- PageResult - 分页结果封装
- 其他跨模块共享的 DTO

## 4. 命名规范

### 4.1 类命名

| 类型 | 命名规范 | 示例 |
|------|----------|------|
| 实体类 | 名词，单数 | Agent, User, Order |
| Repository | 实体名 + Repository | AgentRepository |
| Service | 实体名/业务名 + Service | AgentService, PaymentService |
| Controller | 实体名/业务名 + Controller | AgentController |
| DTO | 实体名 + DTO/Request/Response | AgentDTO, AgentCreateRequest |
| 枚举类 | 名词 + Enum | AgentStatusEnum |
| 异常类 | 名词 + Exception | ZhipuApiException |
| 常量类 | 名词，全大写 | MessageRole, FinishReason |

### 4.2 方法命名

| 方法类型 | 命名规范 | 示例 |
|----------|----------|------|
| CRUD - 创建 | create/save/add | createAgent() |
| CRUD - 查询单个 | get/find/query | getAgent(id) |
| CRUD - 查询列表 | list/findAll/query | listAgents() |
| CRUD - 更新 | update/modify | updateAgent(id, request) |
| CRUD - 删除 | delete/remove | deleteAgent(id) |
| 布尔判断 | is/has/can | isActive(), hasPermission() |
| 转换方法 | convert/to/from | convertToDTO(), toEntity() |

### 4.3 变量命名

- **类变量**: 小驼峰，名词 - `agentService`, `userRepository`
- **常量**: 全大写下划线 - `MAX_TOKEN_COUNT`, `DEFAULT_TIMEOUT`
- **参数**: 小驼峰，有意义的名称 - `agentId`, `request`

## 5. 注解使用规范

### 5.1 实体类注解顺序

```java
@Data                    // Lombok 注解在前
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity                  // JPA 注解在后
@Table(name = "xxx")
@SQLDelete(...)          // Hibernate 特殊注解
@Where(...)
public class Xxx extends BaseEntity {
}
```

### 5.2 字段注解顺序

```java
@Column(name = "xxx", nullable = false, length = 100)
@Builder.Default
private String xxx = "default";
```

### 5.3 方法注解顺序

```java
@Override               // Java 原生注解
@Transactional          // Spring 框架注解
@PostMapping("/xxx")    // Spring Web 注解
public ApiResponse<Xxx> method() {
}
```

## 6. 事务管理规范

### 6.1 事务注解使用

```java
// 写操作：显式声明回滚异常
@Transactional(rollbackFor = Exception.class)
public void create(...) {
}

// 读操作：标记为只读事务
@Transactional(readOnly = true)
public XxxDTO get(Long id) {
}
```

### 6.2 事务边界

- 事务应该在 Service 层控制
- Controller 层不添加 @Transactional
- Repository 层不添加 @Transactional

## 7. 异常处理规范

### 7.1 异常分类

```java
// 业务异常：继承 RuntimeException
public class BusinessException extends RuntimeException {
}

// 参数验证异常：使用 IllegalArgumentException
if (invalid) {
    throw new IllegalArgumentException("参数无效");
}

// 资源不存在：使用自定义异常并设置 404 状态码
throw new ZhipuApiException(404, "资源不存在");
```

### 7.2 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>> handleValidation(...) {
        // 处理参数验证异常
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGeneral(Exception ex) {
        // 处理通用异常
    }
}
```

## 8. 日志规范

### 8.1 日志级别使用

| 级别 | 使用场景 | 示例 |
|------|----------|------|
| DEBUG | 详细的调试信息 | `log.debug("查询参数: {}", params)` |
| INFO | 关键业务流程 | `log.info("Agent 创建成功，ID: {}", id)` |
| WARN | 警告信息 | `log.warn("参数校验失败: {}", errors)` |
| ERROR | 错误信息 | `log.error("系统异常", ex)` |

### 8.2 日志内容规范

```java
// ✅ 好的日志
log.info("创建 Agent: {}", request.getName());
log.error("调用智谱 API 失败: {}", e.getMessage(), e);

// ❌ 不好的日志
log.info("开始");
log.error("出错了");
```

## 9. 注释规范

### 9.1 类注释

```java
/**
 * Agent 服务类
 * <p>
 * 提供 Agent 的 CRUD 和业务逻辑处理。
 * 包括创建、编辑、删除、查询等功能。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Service
public class AgentService {
}
```

### 9.2 方法注释

```java
/**
 * 创建 Agent
 * <p>
 * 验证名称唯一性后创建 Agent 及其配置。
 * </p>
 *
 * @param request 创建请求对象
 * @return Agent DTO
 * @throws IllegalArgumentException 当 Agent 名称已存在时
 */
public AgentDTO createAgent(AgentCreateRequest request) {
}
```

### 9.3 字段注释

```java
/**
 * Agent 名称
 * <p>
 * 必填，长度不超过 100 个字符，全局唯一。
 * </p>
 */
@Column(name = "name", nullable = false, length = 100)
private String name;
```

## 10. 代码质量规范

### 10.1 方法长度

- 单个方法不超过 50 行
- 超过则拆分为多个私有方法

### 10.2 类长度

- 单个类不超过 500 行
- Service 层如果过大，考虑拆分为多个 Service

### 10.3 参数个数

- 方法参数不超过 5 个
- 超过则封装为对象

### 10.4 魔法数字和字符串

```java
// ❌ 不好的写法
if (status.equals("ACTIVE")) {
    ...
}

// ✅ 好的写法
if (AgentStatusEnum.ACTIVE.getCode().equals(status)) {
    ...
}
```

## 11. 测试规范

### 11.1 测试类命名

```
XxxService -> XxxServiceTest
XxxController -> XxxControllerTest
```

### 11.2 测试方法命名

```java
@Test
@DisplayName("创建 Agent - 成功")
void testCreateAgent_Success() {
}

@Test
@DisplayName("创建 Agent - 名称重复")
void testCreateAgent_DuplicateName() {
}
```

### 11.3 测试覆盖率

- Service 层：至少 80%
- Controller 层：至少 70%
- 核心业务逻辑：100%

## 12. 数据库规范

### 12.1 表命名

- 小写下划线：`agent`, `conversation_session`
- 复数形式可选：`users` 或 `user` 都可以

### 12.2 字段命名

- 小写下划线：`created_at`, `agent_id`
- 外键：`{表名}_id`

### 12.3 索引命名

```sql
-- 主键
PRIMARY KEY (`id`)

-- 唯一索引
UNIQUE KEY `uk_{字段名}` (`field`)

-- 普通索引
INDEX `idx_{字段名}` (`field`)

-- 复合索引
INDEX `idx_{表名}_{字段1}_{字段2}` (`field1`, `field2`)

-- 外键
CONSTRAINT `fk_{表名}_{引用表}` FOREIGN KEY (...)
```

## 13. API 设计规范

### 13.1 RESTful 规范

| HTTP 方法 | 用途 | URL 示例 |
|-----------|------|----------|
| GET | 查询资源 | GET /api/agents |
| POST | 创建资源 | POST /api/agents |
| PUT | 更新资源（全量） | PUT /api/agents/{id} |
| PATCH | 更新资源（部分） | PATCH /api/agents/{id} |
| DELETE | 删除资源 | DELETE /api/agents/{id} |

### 13.2 URL 设计

```
# 资源集合
GET    /api/agents          # 查询列表
POST   /api/agents          # 创建

# 单个资源
GET    /api/agents/{id}     # 查询详情
PUT    /api/agents/{id}     # 更新
DELETE /api/agents/{id}     # 删除

# 子资源
GET    /api/agents/{id}/config        # 查询配置
PUT    /api/agents/{id}/config        # 更新配置
GET    /api/agents/{id}/tools         # 查询关联的工具
POST   /api/agents/{id}/tools         # 添加工具关联
```

### 13.3 请求参数

```java
// 路径参数
@PathVariable Long id

// 查询参数
@RequestParam(required = false) String keyword
@RequestParam(defaultValue = "0") int page

// 请求体
@Valid @RequestBody XxxRequest request
```

## 14. Git 提交规范

### 14.1 提交信息格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

**type 类型**：
- feat: 新功能
- fix: 修复 bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 代码重构
- test: 测试相关
- chore: 构建/工具变动

**示例**：
```
feat(agent): 添加 Agent 管理功能

- 实现 Agent CRUD 接口
- 添加 Agent 配置管理
- 支持提示词自定义

Closes #123
```

## 15. 代码检查清单

### 15.1 提交前检查

- [ ] 代码编译通过
- [ ] 所有测试通过
- [ ] 代码格式化
- [ ] 删除无用的 import
- [ ] 删除注释掉的代码
- [ ] 添加必要的注释
- [ ] 日志信息完善
- [ ] 异常处理完善

### 15.2 Code Review 要点

- [ ] 业务逻辑正确
- [ ] 异常处理完善
- [ ] 事务边界合理
- [ ] 性能考虑（N+1 查询、索引）
- [ ] 安全性检查
- [ ] 代码可读性
- [ ] 测试覆盖率

## 16. 性能优化规范

### 16.1 数据库查询

```java
// ❌ N+1 查询问题
List<Agent> agents = agentRepository.findAll();
for (Agent agent : agents) {
    AgentConfig config = configRepository.findByAgentId(agent.getId());
}

// ✅ 使用 JOIN 或 EntityGraph
@EntityGraph(attributePaths = {"config"})
List<Agent> findAll();
```

### 16.2 懒加载

```java
// 在 Service 层的事务中完成数据加载
@Transactional(readOnly = true)
public AgentDTO getAgent(Long id) {
    Agent agent = repository.findById(id).orElseThrow();
    // 在事务中触发懒加载
    agent.getConfig().getModel();
    return convertToDTO(agent);
}
```

### 16.3 分页查询

```java
// 总是使用分页，避免一次查询过多数据
Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
Page<Agent> result = repository.findAll(pageable);
```

## 17. 安全规范

### 17.1 SQL 注入防护

```java
// ✅ 使用参数化查询
@Query("SELECT a FROM Agent a WHERE a.name = :name")
Agent findByName(@Param("name") String name);

// ❌ 不要拼接 SQL
// String sql = "SELECT * FROM agent WHERE name = '" + name + "'";
```

### 17.2 敏感信息保护

```java
// 日志中隐藏敏感信息
String maskedApiKey = apiKey.substring(0, 6) + "****" + apiKey.substring(apiKey.length() - 4);
log.info("API Key: {}", maskedApiKey);
```

### 17.3 参数验证

```java
// 所有外部输入都要验证
@NotBlank(message = "名称不能为空")
@Size(max = 100, message = "名称长度不能超过100")
private String name;
```

## 18. 文档规范

### 18.1 必需文档

- README.md - 项目介绍和快速开始
- API 文档 - 接口说明
- 数据库设计文档 - 表结构和关系
- 架构设计文档 - 系统架构和设计思想

### 18.2 代码即文档

- 清晰的类名和方法名
- 完善的注释
- 合理的代码结构

## 19. 依赖管理规范

### 19.1 版本管理

- 使用 Spring Boot 的依赖管理
- 避免版本冲突
- 定期更新依赖版本

### 19.2 依赖原则

- 最小化依赖
- 优先使用 Spring 生态的库
- 避免引入过时或不维护的库

## 20. 配置管理规范

### 20.1 配置文件组织

```yaml
# application.yml
spring:
  application:
    name: xxx
  datasource:
    ...
  jpa:
    ...

# 业务配置独立管理
zhipu:
  api-key: xxx
  ...
```

### 20.2 环境配置

```
application.yml          # 通用配置
application-dev.yml      # 开发环境
application-test.yml     # 测试环境
application-prod.yml     # 生产环境
```

## 21. 参考资源

- [阿里巴巴 Java 开发手册](https://github.com/alibaba/p3c)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Spring Boot 最佳实践](https://spring.io/guides)
- [Effective Java（第3版）](https://www.oreilly.com/library/view/effective-java/9780134686097/)

---

**遵循这些规范，可以确保代码质量、可维护性和团队协作效率。**

