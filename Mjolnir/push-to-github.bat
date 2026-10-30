@echo off
REM Usage: push-to-github.bat https://github.com/YOUR_NAME/YOUR_REPO.git
if "%~1"=="" (
  echo Usage: push-to-github.bat ^<github-repo-url^>
  exit /b 1
)
git init
git add .
git commit -m "Add Mjolnir plugin"
git branch -M main
git remote add origin %1
git push -u origin main
echo.
echo Pushed! The build starts automatically.
echo Open the repo's Actions tab, click the latest run, and download "Mjolnir-plugin" under Artifacts.
