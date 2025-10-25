#!/bin/bash

# 工具调用功能测试脚本
# 用于快速测试 hello_pox 工具调用功能

echo "======================================"
echo "   工具调用功能测试脚本"
echo "======================================"
echo ""

BASE_URL="http://localhost:8080"

# 颜色定义
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 测试 1：明确调用工具
echo -e "${BLUE}[测试 1] 明确指定调用 hello_pox 工具${NC}"
echo "请求: 请调用 hello_pox 工具"
echo ""
curl -s -X POST $BASE_URL/api/chat/with-tools \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [{"role": "user", "content": "请调用 hello_pox 工具"}],
    "model": "glm-4.5"
  }' | python3 -m json.tool
echo ""
echo "---"
echo ""

# 测试 2：自然语言请求
echo -e "${BLUE}[测试 2] 自然语言请求问候语${NC}"
echo "请求: 给我一个问候语"
echo ""
curl -s -X POST $BASE_URL/api/chat/with-tools \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [{"role": "user", "content": "给我一个问候语"}],
    "model": "glm-4.5"
  }' | python3 -m json.tool
echo ""
echo "---"
echo ""

# 测试 3：不需要工具的问题
echo -e "${BLUE}[测试 3] 普通问题（不应调用工具）${NC}"
echo "请求: 1+1等于多少？"
echo ""
curl -s -X POST $BASE_URL/api/chat/with-tools \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [{"role": "user", "content": "1+1等于多少？"}],
    "model": "glm-4.5"
  }' | python3 -m json.tool
echo ""
echo "---"
echo ""

# 测试 4：执行函数
echo -e "${BLUE}[测试 4] 要求执行特定函数${NC}"
echo "请求: 帮我执行 hello_pox 函数并告诉我结果"
echo ""
curl -s -X POST $BASE_URL/api/chat/with-tools \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [{"role": "user", "content": "帮我执行 hello_pox 函数并告诉我结果"}],
    "model": "glm-4.5"
  }' | python3 -m json.tool
echo ""

echo ""
echo -e "${GREEN}======================================"
echo "   测试完成"
echo "======================================${NC}"

