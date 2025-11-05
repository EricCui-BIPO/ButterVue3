const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

/**
 * Allure 报告生成器
 * 用于生成和管理 Allure 测试报告
 */
class AllureReporter {
  constructor() {
    this.allureResultsDir = path.join(__dirname, '../reports/allure-results');
    this.allureReportDir = path.join(__dirname, '../reports/allure-report');
  }

  /**
   * 确保 Allure 结果目录存在
   */
  ensureDirectories() {
    if (!fs.existsSync(this.allureResultsDir)) {
      fs.mkdirSync(this.allureResultsDir, { recursive: true });
      console.log(`✅ 创建 Allure 结果目录: ${this.allureResultsDir}`);
    }

    if (!fs.existsSync(this.allureReportDir)) {
      fs.mkdirSync(this.allureReportDir, { recursive: true });
      console.log(`✅ 创建 Allure 报告目录: ${this.allureReportDir}`);
    }
  }

  /**
   * 清理旧的 Allure 结果
   */
  cleanResults() {
    try {
      if (fs.existsSync(this.allureResultsDir)) {
        const files = fs.readdirSync(this.allureResultsDir);
        files.forEach(file => {
          const filePath = path.join(this.allureResultsDir, file);
          fs.unlinkSync(filePath);
        });
        console.log('🧹 清理旧的 Allure 结果文件');
      }
    } catch (error) {
      console.warn('⚠️ 清理 Allure 结果时出现警告:', error.message);
    }
  }

  /**
   * 生成 Allure 报告
   */
  generateReport() {
    try {
      console.log('📊 开始生成 Allure 报告...');
      
      // 检查是否有测试结果
      if (!fs.existsSync(this.allureResultsDir) || fs.readdirSync(this.allureResultsDir).length === 0) {
        console.log('⚠️ 没有找到 Allure 测试结果，跳过报告生成');
        return false;
      }

      // 生成报告
      const command = `npx allure generate ${this.allureResultsDir} -o ${this.allureReportDir} --clean`;
      execSync(command, { stdio: 'inherit' });
      
      console.log(`✅ Allure 报告生成成功: ${this.allureReportDir}`);
      console.log(`🌐 要查看报告，请运行: npx allure open ${this.allureReportDir}`);
      
      return true;
    } catch (error) {
      console.error('❌ 生成 Allure 报告失败:', error.message);
      return false;
    }
  }

  /**
   * 打开 Allure 报告
   */
  openReport() {
    try {
      if (!fs.existsSync(this.allureReportDir)) {
        console.log('⚠️ Allure 报告不存在，请先生成报告');
        return false;
      }

      console.log('🌐 打开 Allure 报告...');
      const command = `npx allure open ${this.allureReportDir}`;
      execSync(command, { stdio: 'inherit' });
      
      return true;
    } catch (error) {
      console.error('❌ 打开 Allure 报告失败:', error.message);
      return false;
    }
  }

  /**
   * 获取报告统计信息
   */
  getReportStats() {
    try {
      if (!fs.existsSync(this.allureResultsDir)) {
        return { hasResults: false, fileCount: 0 };
      }

      const files = fs.readdirSync(this.allureResultsDir);
      const resultFiles = files.filter(file => file.endsWith('-result.json'));
      
      return {
        hasResults: resultFiles.length > 0,
        fileCount: resultFiles.length,
        totalFiles: files.length
      };
    } catch (error) {
      console.error('❌ 获取报告统计信息失败:', error.message);
      return { hasResults: false, fileCount: 0, error: error.message };
    }
  }

  /**
   * 完整的报告生成流程
   */
  async generateFullReport() {
    console.log('🚀 开始 Allure 报告生成流程...');
    
    // 确保目录存在
    this.ensureDirectories();
    
    // 获取统计信息
    const stats = this.getReportStats();
    console.log(`📈 测试结果统计: ${stats.fileCount} 个结果文件`);
    
    if (!stats.hasResults) {
      console.log('⚠️ 没有测试结果，无法生成报告');
      return false;
    }
    
    // 生成报告
    const success = this.generateReport();
    
    if (success) {
      console.log('✅ Allure 报告生成完成！');
      console.log(`📁 报告位置: ${this.allureReportDir}`);
      console.log(`🌐 查看报告: npx allure open ${this.allureReportDir}`);
    }
    
    return success;
  }
}

// 如果直接运行此脚本，则生成报告
if (require.main === module) {
  const reporter = new AllureReporter();
  reporter.generateFullReport();
}

module.exports = AllureReporter;