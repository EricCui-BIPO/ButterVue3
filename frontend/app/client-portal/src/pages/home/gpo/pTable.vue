<template>
  <el-card shadow="never" class="table-card">
    <el-table :data="tableData" style="width: 100%" show-summary :summary-method="summaryMethod">


      <!-- 项目名称与ID -->
      <el-table-column label="Project" min-width="220">
        <template #default="{ row }">
          <div class="project-cell">
            <div class="project-name">{{ row.projectName }}</div>
            <div class="project-id">ID: {{ row.projectId }}</div>
          </div>
        </template>
      </el-table-column>

      <!-- 主国家（可多国） -->
      <el-table-column label="Primary Country" min-width="180">
        <template #default="{ row }">
          <div class="country-cell">
            <img v-if="row.primaryCountryCode" :src="getFlagUrl(row.primaryCountryCode)" @error="handleFlagError" class="flag-icon" />
            <span>{{ row.primaryCountry }}</span>
            <span v-if="row.countries.length > 1" class="countries-count">+{{ row.countries.length - 1 }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="payPeriod" label="Pay Period" min-width="120" />
      <el-table-column prop="payDate" label="Pay Date" min-width="140" />

      <!-- 人数与金额汇总（项目维度） -->
      <el-table-column prop="headcount" label="Headcount" width="120" />
      <el-table-column label="Gross" min-width="180">
        <template #default="{ row }">
          {{ formatCurrency(calcGrossProject(row), row.settlementCurrency) }}
        </template>
      </el-table-column>
      <el-table-column label="Net" min-width="180">
        <template #default="{ row }">
          <strong>{{ formatCurrency(calcNetProject(row), row.settlementCurrency) }}</strong>
        </template>
      </el-table-column>

      <!-- 支付方式与状态 -->
      <el-table-column prop="paymentMethod" label="Method" min-width="160" />
      <el-table-column label="Status" width="180" fixed="right">
        <template #default="{ row }">
          <el-tag :type="tagType(row.status)" round>{{ row.status }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getFlagUrl, handleFlagError } from '@I0/shared/utils/country-flag'

// 项目状态、支付方式与处理阶段
type ProjectStatus = 'IN_PREPARATION' | 'APPROVAL_PENDING' | 'APPROVED' | 'PAID' | 'FAILED' | 'ON_HOLD'
type ProjectPaymentMethod = 'BANK_BATCH' | 'WIRE' | 'PAYROLL_CARD' | 'MIXED'
type ProcessingStage = 'DATA_COLLECTION' | 'VALIDATION' | 'PAYMENT_EXECUTION' | 'RECONCILIATION'

// Global Payroll 项目数据类型
interface PayrollProjectRow {
  projectId: string
  projectName: string
  provider: string
  primaryCountry: string
  primaryCountryCode: string
  countries: string[] // 国家代码集合（多国项目）
  settlementCurrency: string // 结算币种（项目层）
  payPeriod: string // e.g. 2025-10
  payDate: string   // e.g. 2025-10-05
  headcount: number
  baseTotal: number
  allowancesTotal: number
  overtimeTotal: number
  bonusTotal: number
  employeeTaxesTotal: number
  employeeSocialTotal: number
  otherDeductionsTotal: number
  employerTaxesTotal: number
  status: ProjectStatus
  paymentMethod: ProjectPaymentMethod
  stage: ProcessingStage
  dueDate: string
  sla: string
}

function formatCurrency(amount: number, currency: string): string {
  try {
    return new Intl.NumberFormat('en', { style: 'currency', currency }).format(amount)
  } catch (_) {
    return `${currency} ${amount.toFixed(2)}`
  }
}

function calcGrossProject(row: PayrollProjectRow): number {
  return row.baseTotal + row.allowancesTotal + row.overtimeTotal + row.bonusTotal
}

function calcNetProject(row: PayrollProjectRow): number {
  return calcGrossProject(row) - row.employeeTaxesTotal - row.employeeSocialTotal - row.otherDeductionsTotal
}

function tagType(status: ProjectStatus): 'success' | 'warning' | 'info' | 'danger' {
  switch (status) {
    case 'PAID': return 'success'
    case 'APPROVED': return 'success'
    case 'APPROVAL_PENDING': return 'warning'
    case 'IN_PREPARATION': return 'info'
    case 'ON_HOLD': return 'info'
    case 'FAILED': return 'danger'
  }
}

// 表格汇总行（Headcount/Gross/Net 合计）
function summaryMethod({ columns, data }: { columns: any[]; data: PayrollProjectRow[] }) {
  const sums: (string | number)[] = []
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = 'Total'
      return
    }
    if (column.property === 'headcount') {
      const total = data.reduce((acc, r) => acc + (r.headcount || 0), 0)
      sums[index] = total
      return
    }
    if (column.label === 'Gross') {
      const total = data.reduce((acc, r) => acc + calcGrossProject(r), 0)
      sums[index] = formatCurrency(total, data[0]?.settlementCurrency || 'USD')
      return
    }
    if (column.label === 'Net') {
      const total = data.reduce((acc, r) => acc + calcNetProject(r), 0)
      sums[index] = formatCurrency(total, data[0]?.settlementCurrency || 'USD')
      return
    }
    sums[index] = ''
  })
  return sums
}

// 模拟 Global Payroll 项目数据（多国、多币种项目）
const tableData = ref<PayrollProjectRow[]>([
  {
    projectId: 'GPO-2025-10-SG-01',
    projectName: 'APAC Monthly Payroll Oct',
    provider: 'ADP',
    primaryCountry: 'Singapore',
    primaryCountryCode: 'SG',
    countries: ['SG', 'CN', 'IN'],
    settlementCurrency: 'SGD',
    payPeriod: '2025-10',
    payDate: '2025-10-05',
    headcount: 120,
    baseTotal: 680000,
    allowancesTotal: 55000,
    overtimeTotal: 12000,
    bonusTotal: 80000,
    employeeTaxesTotal: 90000,
    employeeSocialTotal: 42000,
    otherDeductionsTotal: 8000,
    employerTaxesTotal: 110000,
    status: 'APPROVAL_PENDING',
    paymentMethod: 'BANK_BATCH',
    stage: 'VALIDATION',
    dueDate: '2025-10-06',
    sla: 'T+2'
  },
  {
    projectId: 'GPO-2025-10-US-01',
    projectName: 'Americas Payroll Cycle Oct',
    provider: 'Workday',
    primaryCountry: 'United States',
    primaryCountryCode: 'US',
    countries: ['US', 'CA', 'MX'],
    settlementCurrency: 'USD',
    payPeriod: '2025-10',
    payDate: '2025-10-03',
    headcount: 95,
    baseTotal: 720000,
    allowancesTotal: 40000,
    overtimeTotal: 15000,
    bonusTotal: 120000,
    employeeTaxesTotal: 140000,
    employeeSocialTotal: 60000,
    otherDeductionsTotal: 15000,
    employerTaxesTotal: 160000,
    status: 'APPROVED',
    paymentMethod: 'WIRE',
    stage: 'PAYMENT_EXECUTION',
    dueDate: '2025-10-04',
    sla: 'T+1'
  },
  {
    projectId: 'GPO-2025-10-JP-01',
    projectName: 'Japan Monthly Payroll Oct',
    provider: 'Local Vendor',
    primaryCountry: 'Japan',
    primaryCountryCode: 'JP',
    countries: ['JP'],
    settlementCurrency: 'JPY',
    payPeriod: '2025-10',
    payDate: '2025-10-01',
    headcount: 60,
    baseTotal: 2800000,
    allowancesTotal: 160000,
    overtimeTotal: 60000,
    bonusTotal: 250000,
    employeeTaxesTotal: 450000,
    employeeSocialTotal: 320000,
    otherDeductionsTotal: 50000,
    employerTaxesTotal: 520000,
    status: 'PAID',
    paymentMethod: 'MIXED',
    stage: 'RECONCILIATION',
    dueDate: '2025-10-02',
    sla: 'T+1'
  },
  {
    projectId: 'GPO-2025-10-EU-01',
    projectName: 'EU Quarterly Bonus Q4',
    provider: 'SAP SuccessFactors',
    primaryCountry: 'Germany',
    primaryCountryCode: 'DE',
    countries: ['DE', 'FR', 'NL', 'ES'],
    settlementCurrency: 'EUR',
    payPeriod: '2025-Q4',
    payDate: '2025-10-15',
    headcount: 80,
    baseTotal: 300000,
    allowancesTotal: 25000,
    overtimeTotal: 0,
    bonusTotal: 200000,
    employeeTaxesTotal: 90000,
    employeeSocialTotal: 45000,
    otherDeductionsTotal: 10000,
    employerTaxesTotal: 100000,
    status: 'ON_HOLD',
    paymentMethod: 'BANK_BATCH',
    stage: 'DATA_COLLECTION',
    dueDate: '2025-10-16',
    sla: 'T+3'
  }
])
</script>

<style scoped lang="scss">
.table-card {
  margin: 10px 0;
}

.project-cell {
  display: flex;
  flex-direction: column;
}
.project-name {
  font-weight: 600;
}
.project-id {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.country-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}
.countries-count {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.flag-icon {
  width: 18px;
  height: 14px;
  border-radius: 2px;
  object-fit: cover;
}

.country-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.country-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: var(--el-fill-color-light);
  padding: 2px 6px;
  border-radius: 12px;
  font-size: 12px;
}

</style>