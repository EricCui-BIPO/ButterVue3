package com.i0.talent.application.service;

import com.i0.talent.domain.enums.DataLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 法律法规提示服务
 *
 * 负责根据不同地区的数据存储位置提供相应的法律法规提示
 */
@Service
@Slf4j
public class LegalComplianceService {

    /**
     * 获取数据存储位置对应的法律法规提示
     *
     * @param dataLocation 数据存储位置
     * @return 法律法规提示信息
     */
    public String getLegalNotice(DataLocation dataLocation) {
        if (dataLocation == null) {
            return getDefaultLegalNotice();
        }

        switch (dataLocation) {
            case GERMANY:
                return getGermanyLegalNotice();
            case SINGAPORE:
                return getSingaporeLegalNotice();
            case NINGXIA:
                return getNingxiaLegalNotice();
            default:
                return getDefaultLegalNotice();
        }
    }

    /**
     * 获取德国法律法规提示（GDPR）
     *
     * @return 德国法律提示
     */
    private String getGermanyLegalNotice() {
        return "⚠️ GDPR 法规提醒:\n" +
               "1. 数据主体权利：员工有权访问、更正、删除其个人数据\n" +
               "2. 数据处理限制：仅允许为特定、明确的目的处理数据\n" +
               "3. 数据保护影响评估：处理敏感数据前需进行DPIA\n" +
               "4. 数据泄露通知：发生数据泄露需在72小时内通知监管机构\n" +
               "5. 跨境数据传输：向欧盟以外传输数据需确保适当保护措施\n" +
               "6. 保留期限：数据保留不超过必要的时间\n\n" +
               "联系方式：德国联邦数据保护与信息自由委员会 (BfDI)";
    }

    /**
     * 获取新加坡法律法规提示（PDPA）
     *
     * @return 新加坡法律提示
     */
    private String getSingaporeLegalNotice() {
        return "⚠️ PDPA 法规提醒:\n" +
               "1. 同意原则：收集个人数据需获得数据主体同意\n" +
               "2. 目的限制：仅能按告知目的使用数据\n" +
               "3. 通知义务：必须告知数据收集目的和使用方式\n" +
               "4. 访问权限：数据主体有权访问其个人数据\n" +
               "5. 更正权限：数据主体有权更正不准确的数据\n" +
               "6. 数据安全：必须采取合理安全措施保护数据\n\n" +
               "联系方式：新加坡个人数据保护委员会 (PDPC)";
    }

    /**
     * 获取中国宁夏法律法规提示
     *
     * @return 中国法律提示
     */
    private String getNingxiaLegalNotice() {
        return "⚠️ 中国法律法规提醒:\n" +
               "1. 《个人信息保护法》：明确个人信息处理规则\n" +
               "2. 《数据安全法》：建立数据分类分级保护制度\n" +
               "3. 知情同意：处理个人信息需取得个人同意\n" +
               "4. 最小必要：仅收集与处理目的最小相关的信息\n" +
               "5. 安全保障：采取必要措施保障数据安全\n" +
               "6. 境内存储：重要数据应当在境内存储\n\n" +
               "联系方式：国家网信办、宁夏回族自治区网信办";
    }

    /**
     * 获取默认法律法规提示
     *
     * @return 默认法律提示
     */
    private String getDefaultLegalNotice() {
        return "⚠️ 通用数据保护提醒:\n" +
               "1. 仅在必要时访问和处理员工数据\n" +
               "2. 确保数据的机密性和完整性\n" +
               "3. 遵守适用的数据保护法规\n" +
               "4. 记录所有数据访问和处理活动\n" +
               "5. 定期审查数据访问权限\n\n" +
               "如有疑问，请联系法务部门或数据保护官";
    }

    /**
     * 获取数据处理最佳实践建议
     *
     * @param dataLocation 数据存储位置
     * @return 最佳实践建议
     */
    public List<String> getBestPractices(DataLocation dataLocation) {
        List<String> practices = Arrays.asList(
            "仅收集业务所需的最低限度的员工信息",
            "定期审查和清理不再需要的员工数据",
            "确保数据访问权限基于最小权限原则",
            "对敏感数据进行加密存储和传输",
            "记录所有数据访问和处理操作",
            "定期进行数据保护培训和意识提升"
        );

        // 根据地区添加特定的最佳实践
        if (dataLocation == DataLocation.GERMANY) {
            practices.add("任命数据保护官 (DPO)");
            practices.add("实施数据保护影响评估 (DPIA)");
        } else if (dataLocation == DataLocation.SINGAPORE) {
            practices.add("制定数据泄露响应计划");
            practices.add("定期更新数据保护政策");
        } else if (dataLocation == DataLocation.NINGXIA) {
            practices.add("配合监管部门的数据安全检查");
            practices.add("建立数据安全事件应急响应机制");
        }

        return practices;
    }

    /**
     * 获取数据保留期限建议
     *
     * @param dataLocation 数据存储位置
     * @return 保留期限建议
     */
    public String getDataRetentionGuidance(DataLocation dataLocation) {
        switch (dataLocation) {
            case GERMANY:
                return "德国数据保留建议：\n" +
                       "• 基本人事信息：雇佣关系结束后6年\n" +
                       "• 薪资数据：10年（税务要求）\n" +
                       "• 健康数据：雇佣关系结束后立即删除（除非获得明确同意）";

            case SINGAPORE:
                return "新加坡数据保留建议：\n" +
                       "• 基本人事信息：雇佣关系结束后5年\n" +
                       "• 薪资记录：7年\n" +
                       "• 评估数据：2年\n" +
                       "• 所有数据：最长不超过10年";

            case NINGXIA:
                return "中国数据保留建议：\n" +
                       "• 基本人事信息：劳动关系存续期间及结束后2年\n" +
                       "• 薪资支付记录：至少保存15年\n" +
                       "• 社保公积金记录：永久保存\n" +
                       "• 培训记录：3年";

            default:
                return "通用数据保留建议：\n" +
                       "• 根据业务需求和法规要求确定保留期限\n" +
                       "• 定期清理过期数据\n" +
                       "• 建立数据归档和销毁流程";
        }
    }

    /**
     * 验证数据处理操作的合规性
     *
     * @param dataLocation 数据存储位置
     * @param operationType 操作类型（如：VIEW、EDIT、EXPORT、DELETE）
     * @param userRole 用户角色
     * @return 合规性检查结果
     */
    public ComplianceCheckResult checkCompliance(DataLocation dataLocation, String operationType, String userRole) {
        ComplianceCheckResult result = new ComplianceCheckResult();

        // 基础合规性检查
        result.setCompliant(true);

        // 根据地区和操作类型进行特定检查
        if (dataLocation == DataLocation.GERMANY) {
            // 德国GDPR特殊要求
            if ("EXPORT".equals(operationType) && !hasExportPermission(userRole)) {
                result.setCompliant(false);
                result.addIssue("德国数据导出需要特殊权限");
            }
        } else if (dataLocation == DataLocation.NINGXIA) {
            // 中国数据出境要求
            if ("EXPORT".equals(operationType) && !isCrossBorderTransferAllowed(dataLocation)) {
                result.setCompliant(false);
                result.addIssue("重要数据出境需要进行安全评估");
            }
        }

        return result;
    }

    /**
     * 检查用户是否有数据导出权限
     *
     * @param userRole 用户角色
     * @return 是否有权限
     */
    private boolean hasExportPermission(String userRole) {
        if (userRole == null) {
            return false;
        }
        return Arrays.asList("ADMIN", "DATA_PROTECTION_OFFICER", "COMPLIANCE_MANAGER")
                   .contains(userRole.toUpperCase());
    }

    /**
     * 检查是否允许跨境数据传输
     *
     * @param dataLocation 数据存储位置
     * @return 是否允许
     */
    private boolean isCrossBorderTransferAllowed(DataLocation dataLocation) {
        // 简化的跨境传输检查逻辑
        return dataLocation != DataLocation.NINGXIA; // 宁夏数据需要特殊评估
    }

    /**
     * 合规性检查结果类
     */
    public static class ComplianceCheckResult {
        private boolean compliant;
        private List<String> issues = new java.util.ArrayList<>();

        public boolean isCompliant() {
            return compliant;
        }

        public void setCompliant(boolean compliant) {
            this.compliant = compliant;
        }

        public List<String> getIssues() {
            return issues;
        }

        public void addIssue(String issue) {
            this.issues.add(issue);
        }
    }
}