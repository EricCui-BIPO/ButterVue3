<template>
    <div class="el-container">
        <div class="content-container">
            <el-page-header class="page-header" @back="goBack">
                <template #content>
                    <span> Create EOR Employee </span>
                </template>
            </el-page-header>

            <!-- 使用抽象组件渲染顶部步骤条 + 左侧表单 + 右侧说明 + 底部操作按钮 -->
            <div class="step-form-wrapper">
                <StepForm :step-config="stepConfig" v-model:step-active="stepActive" @submit="submitForm" />
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import StepForm from '../components/stepForm/stepForm.vue'
import type {  StepConfig } from '../components/stepForm/types'

// 步骤条状态管理
const stepActive = ref<number>(0)

// 表单类型与数据模型
interface BasicForm {
    name: string
    gender: 'Male' | 'Female' | 'Other'
    birthday: string
    email: string
    phone: string
}

interface EmployerForm {
    company: string
    department: string
    jobTitle: string
    lineManager: string
    employmentStartDate: string
    employmentEndDate?: string | null
}

interface SalaryForm {
    salary: number
    is13thMonth: boolean
    payGroup: string
    currency: string
    payCycle: 'monthly' | 'bi-weekly' | 'semi-monthly'
}

// 使用共享类型定义，移除本地接口

// 表单数据
const basicForm = reactive<BasicForm>({
    name: '',
    gender: 'Male',
    birthday: '',
    email: '',
    phone: '',
})

const employerForm = reactive<EmployerForm>({
    company: '',
    department: '',
    jobTitle: '',
    lineManager: '',
    employmentStartDate: '',
    employmentEndDate: null,
})

const salaryForm = reactive<SalaryForm>({
    salary: 0,
    is13thMonth: false,
    payGroup: 'Standard',
    currency: 'USD',
    payCycle: 'monthly',
})

// 动态表单配置
const stepConfig: StepConfig[] = [
    {
        key: 'basic',
        title: 'Basic Information',
        formData: basicForm,
        fields: [
            {
                key: 'name',
                label: 'Full Name',
                type: 'input',
                placeholder: 'Enter full name',
                focusKey: 'basic-name',
                explainTitle: 'Full Name',
                explainText: "Enter the employee's legal full name exactly as it appears on government-issued identification (e.g., passport or national ID). This name will be used for the employment contract, local payroll registration, tax filings, and benefits enrollment. Avoid nicknames or informal variations, and include middle names if they are part of the legal name in the relevant jurisdiction. If the employee uses diacritics or non-Latin characters, provide the official transliteration used by local authorities when required. In cross-border EOR arrangements, the legal name must match what is submitted to statutory agencies to prevent compliance discrepancies or delayed onboarding. For example, if an employee’s passport lists “María-José López García,” do not shorten it to “Maria Lopez,” as this may cause mismatches with payroll and banking systems. If the employee has recently changed their name due to marriage or legal proceedings, ensure supporting documents are available, since local authorities may request verification. Accurate entry here streamlines downstream tasks like issuing payslips, creating HRIS profiles, and setting up benefit accounts."
            },
            {
                key: 'gender',
                label: 'Gender',
                type: 'select',
                placeholder: 'Select gender',
                focusKey: 'basic-gender',
                options: [
                    { label: 'Male', value: 'Male' },
                    { label: 'Female', value: 'Female' },
                    { label: 'Other', value: 'Other' }
                ],
                explainTitle: 'Gender',
                explainText: "Gender information may be required for statutory reporting, benefits eligibility, or diversity metrics depending on local regulations and employer policies. Selecting 'Other' is appropriate when the employee prefers not to disclose or does not identify with binary categories. In certain jurisdictions, recording gender can affect social insurance classification, medical benefits, or mandated reporting to government bodies. The EOR will only use this field for lawful processing and to fulfill compliance obligations. If an employee transitions or requests updates to their gender record, follow the defined HR process to respect privacy and update official documents where needed. As an example, some countries require gender in payroll filings, while others discourage collecting it unless strictly necessary. When operating across multiple regions, keep consistency in HR records but align with local law, and do not infer gender from names. Always treat this field as sensitive personal data and restrict access to authorized personnel following applicable data protection laws."
            },
            {
                key: 'birthday',
                label: 'Birthday',
                type: 'date',
                placeholder: 'Select date of birth',
                focusKey: 'basic-birthday',
                dateType: 'date',
                valueFormat: 'YYYY-MM-DD',
                clearable: true,
                explainTitle: 'Birthday',
                explainText: "The date of birth is used to verify identity, determine eligibility for certain benefits, and comply with labor regulations that may set age-related conditions (e.g., minimum working age, overtime protections, or retirement-related programs). It also helps the EOR calculate statutory contributions in countries where age impacts rates or thresholds. Enter the date in the specified format and ensure it matches official documents. In some jurisdictions, employers must maintain accurate birth records for audits or social security filings. For example, benefits waiting periods may vary by age, and local pension schemes could require age-based reporting. If the employee has privacy concerns, reassure them that the EOR processes this data under legal bases and data protection standards. Incorrect dates can cause payroll errors, benefit enrollment issues, or government filing rejections, leading to onboarding delays. Double-check values, especially when converting formats across regions (e.g., day-month-year vs month-day-year)."
            },
            {
                key: 'email',
                label: 'Email',
                type: 'input',
                placeholder: 'Enter email',
                focusKey: 'basic-email',
                explainTitle: 'Email',
                explainText: "Provide a reliable email address for onboarding communications, contract signing, payroll notifications, and benefits information. This may be a corporate or personal email depending on company policy and onboarding stage. Ensure the mailbox is regularly monitored, has adequate storage, and can receive messages from HR and payroll systems (sometimes branded or automated). In EOR scenarios, email is used for identity verification, self-service portal access, and important updates such as policy changes or compliance requests. If using a corporate email, confirm that it will be provisioned before the employee’s start date; if using personal email, ensure strong security practices (e.g., multi-factor authentication) to protect sensitive information. Avoid shared addresses that could compromise confidentiality. An accurate, active email helps prevent delays in distributing key documents (e.g., offer letters, addenda, payslips) and reduces the risk of communication breakdowns during onboarding and beyond."
            },
            {
                key: 'phone',
                label: 'Phone Number',
                type: 'input',
                placeholder: 'Enter phone number',
                focusKey: 'basic-phone',
                explainTitle: 'Phone Number',
                explainText: "Enter a phone number with the appropriate country/region code (e.g., +65 for Singapore, +62 for Indonesia), and use a format recognized by local telecom standards. Phone numbers may be used for two-factor authentication, urgent HR communications, and verification steps required by banking or government portals. In EOR operations, a valid number can be important when coordinating time-sensitive tasks such as onboarding sessions, document submissions, or payroll cutoffs. If the employee prefers messaging apps (e.g., WhatsApp) that rely on the number, ensure it is correctly registered. Avoid landlines if mobile access is needed for authentication. Keep the number updated, especially for employees who travel frequently or relocate internationally. Where legally required, obtain consent for contacting the number and respect do-not-call preferences. Accurate contact details help reduce friction in critical moments like verifying identity for payroll or benefits enrollment."
            }
        ]
    },
    {
        key: 'employer',
        title: 'Employer Information',
        formData: employerForm,
        fields: [
            {
                key: 'jobTitle',
                label: 'Job Title',
                type: 'input',
                placeholder: 'Enter job title',
                focusKey: 'employer-jobTitle',
                explainTitle: 'Job Title',
                explainText: "The job title should reflect the position described in the employment agreement and align with actual responsibilities in daily work. Consistent titles support accurate role classification, compensation benchmarking, and regulatory compliance (e.g., exemptions related to overtime or professional categories). In EOR structures, the title also informs local payroll setup and benefits eligibility, as some programs or allowances are tied to job level or function. If a global job title is used internally, provide a local equivalent where necessary to meet jurisdictional norms or translation requirements. For example, 'Software Engineer II' may be recorded as 'Software Engineer' in countries that do not use numbered levels. Ensure the title is not misleading, as this can create confusion in audits or with authorities. When changing titles due to promotions or reorganizations, coordinate updates across HRIS, payroll, and contracts to preserve consistency and legal accuracy."
            },
            {
                key: 'department',
                label: 'Department',
                type: 'input',
                placeholder: 'Enter department',
                focusKey: 'employer-department',
                explainTitle: 'Department',
                explainText: "Specify the department or functional area the employee belongs to, such as Engineering, Finance, or Human Resources. Accurate departmental mapping supports approval workflows, organizational reporting, cost center allocation, and stakeholder visibility. In EOR environments, department alignment can determine who receives HR notifications, who approves payroll changes, and how the employee appears in organizational structures used across regions. If your organization uses codes or abbreviations, ensure they are standardized and recognized by HRIS and payroll teams. For cross-functional roles, pick the primary department responsible for performance management and budget oversight. Clear departmental data reduces delays when routing requests, assigning mandatory trainings, or producing compliance reports. When reorganizations occur, update the department promptly to avoid misrouted communications and ensure accurate headcount analytics."
            },
            {
                key: 'lineManager',
                label: 'Line Manager',
                type: 'input',
                placeholder: 'Enter line manager name',
                focusKey: 'employer-lineManager',
                explainTitle: 'Line Manager',
                explainText: "Identify the direct supervisor who oversees day-to-day work, approves time-off or payroll-related changes, and conducts performance reviews. In EOR contexts, the line manager is often the primary contact for operational decisions while the EOR handles employment administration and compliance. Provide the manager's full name to avoid ambiguity, and keep this information current when reporting lines change. Clear manager assignment accelerates onboarding, ensures requests are routed to the right approver, and supports accountability in HR processes. If the manager is located in a different country or time zone, set expectations for communication windows and escalation paths. For example, a developer in Jakarta may report to a manager in Singapore; define who handles urgent staffing decisions locally. Accurate manager data is also useful for internal audits, succession planning, and employee engagement programs."
            },
            {
                key: 'employmentStartDate',
                label: 'Employment Start Date',
                type: 'date',
                placeholder: 'Select start date',
                focusKey: 'employer-employmentStartDate',
                dateType: 'date',
                valueFormat: 'YYYY-MM-DD',
                clearable: true,
                explainTitle: 'Employment Start Date',
                explainText: "The official start date is the date on which the employment relationship begins under the EOR contract. This date influences probation periods, benefits eligibility windows, payroll cutoffs, and compliance filings. Enter the date carefully to avoid proration errors in the first payslip or misalignment with statutory reporting cycles. For example, starting mid-month may result in pro-rated salary and benefits enrollment effective from the start date, subject to local rules. The EOR relies on this date to register the employee with authorities, initiate mandatory contributions, and trigger onboarding workflows. If the start date changes, promptly communicate updates to prevent delays or legal inconsistencies. In multi-country operations, ensure that the start date aligns with visa or work authorization timing and any required notice periods."
            },
            {
                key: 'employmentEndDate',
                label: 'Employment End Date',
                type: 'date',
                placeholder: 'Select end date (if applicable)',
                focusKey: 'employer-employmentEndDate',
                dateType: 'date',
                valueFormat: 'YYYY-MM-DD',
                clearable: true,
                explainTitle: 'Employment End Date',
                explainText: "Provide an end date when the role is fixed-term or when an anticipated termination date is known. Leave this field empty for indefinite agreements. The end date can affect benefits eligibility, severance calculations, and statutory reporting. In some jurisdictions, fixed-term contracts must adhere to specific rules regarding length, renewals, and conversion to indefinite status. If the end date is extended or the contract is renewed, ensure updated documentation and payroll settings are applied before the original date lapses to avoid unintended termination processing. For example, a twelve-month contract ending on June 30 should be renewed in systems by mid-June to maintain continuous coverage and prevent payroll disruptions. Accurate end date management supports compliance audits, smooth offboarding, and transparent communication with the employee."
            }
        ]
    },
    {
        key: 'salary',
        title: 'Salary Information',
        formData: salaryForm,
        fields: [
            {
                key: 'salary',
                label: 'Basic Salary',
                type: 'number',
                placeholder: 'Enter base salary amount',
                focusKey: 'salary-basicSalary',
                min: 0,
                explainTitle: 'Basic Salary',
                explainText: "In an Employer of Record (EOR) arrangement, the basic salary is the core fixed compensation agreed between the client company and the employee under the local legal framework. This amount must comply with country-specific minimum wage laws, tax rules, and mandatory social contributions. For example, a Singapore-based software engineer might receive a base salary of 6,000 SGD per month, which determines CPF contributions and income tax withholding handled by the EOR. If the engineer later transfers to Indonesia under the same client, the base salary may be adjusted to align with local market rates and statutory requirements, e.g., 30,000,000 IDR. The basic salary also interacts with benefits such as paid leave, overtime eligibility (where applicable), and pro-rated payments for mid-cycle changes. In practice, the EOR performs payroll calculation, ensures compliance filings, and issues payslips every cycle, while the client company focuses on daily management and budget planning. When reviewing compensation, always consider cost-of-labor benchmarks, exchange rate stability, and potential allowances (housing, transport) that may be customary in certain jurisdictions."
            },
            {
                key: 'is13thMonth',
                label: 'Is 13th-month',
                type: 'switch',
                placeholder: 'Toggle 13th-month pay',
                focusKey: 'salary-is13thMonth',
                activeValue: true,
                inactiveValue: false,
                activeText: 'Included',
                inactiveText: 'Not Included',
                explainTitle: '13th-month Pay',
                explainText: "A 13th-month payment is a customary or statutory bonus in many markets, often paid at year-end or before major holidays. Whether it is mandatory or discretionary depends on local law and the employment agreement. In EOR arrangements, confirm if the 13th month is included, how it is calculated (e.g., based on base salary), and whether proration applies for partial service. For instance, in some countries the bonus is pro-rated for employees who start mid-year or resign before payout. Clarify eligibility conditions such as minimum service periods or performance requirements. Transparency helps avoid misunderstandings about total compensation. If not included, consider alternative bonus structures documented in the contract. Keep in mind that tax treatment may vary by jurisdiction, and the EOR will handle withholding and reporting according to local regulations."
            },
            {
                key: 'payGroup',
                label: 'Pay Group',
                type: 'select',
                placeholder: 'Select pay group',
                focusKey: 'salary-payGroup',
                options: [
                    { label: 'Standard', value: 'Standard' },
                    { label: 'Executive', value: 'Executive' },
                    { label: 'Contractor', value: 'Contractor' }
                ],
                explainTitle: 'Pay Group',
                explainText: "Pay groups segment employees into logical payroll batches with distinct calendars, approval workflows, or calculation rules. For example, executive groups may have different bonus handling or approval thresholds compared to standard groups. In EOR operations, grouping helps align processing across countries or business units while managing cutoffs and review processes efficiently. Choose the group that best matches the employee’s classification to ensure the correct rules are applied for each cycle. If an employee changes role or moves to another country, review group assignment to maintain consistency and compliance. Well-defined pay groups reduce errors, improve visibility for approvers, and streamline integration with HRIS or finance systems."
            },
            {
                key: 'payCycle',
                label: 'Pay Cycle',
                type: 'select',
                placeholder: 'Select pay cycle',
                focusKey: 'salary-payCycle',
                options: [
                    { label: 'Monthly', value: 'monthly' },
                    { label: 'Bi-weekly', value: 'bi-weekly' },
                    { label: 'Semi-monthly', value: 'semi-monthly' }
                ],
                explainTitle: 'Pay Cycle',
                explainText: "Select the payroll frequency used for this employee. Monthly cycles are common in many countries, while bi-weekly or semi-monthly cycles may be preferred in certain industries or regions. The pay cycle affects cutoff dates for timesheets and changes, the timing of statutory filings, and the schedule for releasing payslips. In EOR contexts, aligning pay cycles with local norms can reduce friction and improve employee satisfaction. Consider operational impacts: bi-weekly cycles generate more frequent processing, whereas monthly cycles simplify administrative tasks. If changing the pay cycle, notify all parties and adjust calendars to prevent missed deadlines."
            },
            {
                key: 'currency',
                label: 'Currency',
                type: 'select',
                placeholder: 'Select currency',
                focusKey: 'salary-currency',
                options: [
                    { label: 'USD', value: 'USD' },
                    { label: 'CNY', value: 'CNY' },
                    { label: 'SGD', value: 'SGD' },
                    { label: 'IDR', value: 'IDR' }
                ],
                explainTitle: 'Currency',
                explainText: "Choose the payroll currency that aligns with the employment agreement and local regulations. Many jurisdictions require payroll to be processed in the local currency for statutory compliance, taxation, and social insurance contributions. If offering a salary denominated in a foreign currency, confirm that it is permitted and clarify any foreign exchange considerations, including rate fluctuations and conversion timing. In EOR arrangements, the EOR will manage gross-to-net calculations in the selected currency, handle withholding, and ensure compliance filings are accurate. For example, a role based in Indonesia typically uses IDR for payroll, while cross-border arrangements may require additional agreements if USD is proposed. Clear currency selection avoids confusion in payslips and financial planning."
            }
        ]
    }
]

// 提交表单（后续可接入后端 API）
const submitForm = () => {
    // 简单校验示例（可改为使用 el-form 的 rules/validate）
    if (!basicForm.name || !basicForm.email) {
        ElMessage.warning('Please complete Basic Information (name and email)')
        stepActive.value = 0
        return
    }

    ElMessage.success('Submitted successfully')
}

const router = useRouter()
const goBack = () => {
    if (window.history.length > 1) {
        router.back()
    } else {
        router.push('/home/eor')
    }
}
</script>

<style scoped>
.el-container {
    display: flex;
    flex-direction: column;
    background: #fafafa;
    padding: 20px;
    height: calc(100vh);
    overflow-y: hidden;

    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}

.content-container {
    max-width: 1600px;
    width: 100%;
    margin: 0px auto 0 auto;
}

.page-header {
    margin-bottom: 20px;
}
</style>
