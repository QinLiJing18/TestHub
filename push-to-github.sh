#!/bin/bash

# TestHub修复内容推送脚本
# 使用方法：在终端中执行 ./push-to-github.sh

set -e

echo "================================================"
echo "TestHub Docker修复内容 - GitHub推送脚本"
echo "================================================"
echo ""

# 进入项目目录
cd /home/sutai/TestHub

# 显示当前状态
echo "📦 Git状态："
git status --short

echo ""
echo "📝 待推送的提交："
git log --oneline -1

echo ""
echo "🔗 远程仓库："
git remote -v

echo ""
echo "================================================"
echo "开始推送到GitHub..."
echo "================================================"
echo ""

# 确保SSH agent已启动
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_rsa 2>/dev/null || true

# 推送到GitHub
git push -u origin master

echo ""
echo "================================================"
echo "✅ 推送成功！"
echo "================================================"
echo ""
echo "访问以下地址查看更新："
echo "https://github.com/QinLiJing18/TestHub"
echo ""
