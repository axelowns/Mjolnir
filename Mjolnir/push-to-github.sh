#!/usr/bin/env bash
# Usage: ./push-to-github.sh https://github.com/YOUR_NAME/YOUR_REPO.git
set -e
if [ -z "$1" ]; then
  echo "Usage: $0 <github-repo-url>"
  exit 1
fi
git init
git add .
git commit -m "Add Mjolnir plugin"
git branch -M main
git remote add origin "$1"
git push -u origin main
echo
echo "Pushed! The build starts automatically."
echo "Open the repo's Actions tab, click the latest run, and download 'Mjolnir-plugin' under Artifacts."
