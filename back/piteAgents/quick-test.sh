#!/bin/bash
# 快速测试工具调用

echo "🧪 测试工具调用功能..."
echo ""

curl -s -X POST http://localhost:8080/api/chat/with-tools \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [{"role": "user", "content": "请调用 hello_pox 工具"}],
    "model": "glm-4.5"
  }' | python3 -c "import sys, json; data=json.load(sys.stdin); print('✅ 成功!' if data['code']==200 else '❌ 失败'); print('📝 回答:', data['data']['content'][:100] + '...')"

echo ""
echo "✨ 测试完成！"
