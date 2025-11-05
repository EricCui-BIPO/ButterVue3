<template>
    <el-card shadow="never" class="table-card">
        <el-table :data="tableData" style="width: 100%">
            <el-table-column label="Direct Employee" min-width="240">
                <template #default="{ row }">
                    <div class="contractor-cell">
                        <el-avatar :src="row.avatar" />
                        <div class="contractor-info">
                            <div class="name">{{ row.contractorName }}</div>
                            <div class="id">ID: {{ row.contractId }}</div>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="vendor" label="Vendor" min-width="160" />
            <el-table-column prop="role" label="Job Title" min-width="160" />
            <el-table-column label="Country" min-width="160">
                <template #default="{ row }">
                    <div class="country-cell">
                        <img v-if="row.countryCode" :src="getFlagUrl(row.countryCode)" @error="handleFlagError" class="flag-icon" />
                        <span>{{ row.country }}</span>
                        <el-tag v-if="row.remote" type="info" size="small" class="remote-tag">Remote</el-tag>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="startDate" label="Start" min-width="120" />
            <el-table-column prop="endDate" label="End" min-width="120" />
            <el-table-column prop="percentage" label="Status" min-width="140" fixed="right">
                <template #default="{ row }">
                    <el-progress :percentage="row.percentage" :status="progressStatus(row.percentage)" :text-inside="true" :stroke-width="14" />
                </template>
            </el-table-column>
        </el-table>
    </el-card>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { getFlagUrl, handleFlagError } from '@I0/shared/utils/country-flag'

// Contractor data types
type ContractType = 'T&M' | 'Fixed-Price' | 'Retainer'
type RateType = 'Hourly' | 'Daily' | 'Monthly'
type ContractorStatus = 'ACTIVE' | 'ENDING_SOON' | 'ENDED' | 'ONBOARDING' | 'COMPLIANCE_PENDING'
type TimesheetStatus = 'APPROVED' | 'SUBMITTED' | 'PENDING' | 'MISSING'
type InvoiceStatus = 'PAID' | 'APPROVED' | 'PENDING' | 'FAILED'
type PaymentMethod = 'BANK_BATCH' | 'WIRE' | 'PAYROLL_CARD' | 'MIXED'

interface InvoiceInfo {
  amount: number
  currency: string
  status: InvoiceStatus
  invoiceDate: string
}

interface ContractorRow {
  avatar: string
  contractorName: string
  vendor: string
  role: string
  country: string
  countryCode: string
  remote: boolean
  contractId: string
  contractType: ContractType
  rateType: RateType
  rate: number
  currency: string
  startDate: string
  endDate?: string | null
  status: ContractorStatus
  timesheetStatus: TimesheetStatus
  latestInvoice: InvoiceInfo
  paymentMethod: PaymentMethod
  percentage: number
}

function formatCurrency(amount: number, currency: string): string {
  try {
    return new Intl.NumberFormat('en', { style: 'currency', currency }).format(amount)
  } catch (_) {
    return `${currency} ${amount.toFixed(2)}`
  }
}

function statusTagType(status: ContractorStatus): 'success' | 'warning' | 'info' | 'danger' {
  switch (status) {
    case 'ACTIVE': return 'success'
    case 'ENDING_SOON': return 'warning'
    case 'ENDED': return 'info'
    case 'ONBOARDING': return 'info'
    case 'COMPLIANCE_PENDING': return 'warning'
  }
}
function timesheetTagType(status: TimesheetStatus): 'success' | 'warning' | 'info' | 'danger' {
  switch (status) {
    case 'APPROVED': return 'success'
    case 'SUBMITTED': return 'info'
    case 'PENDING': return 'warning'
    case 'MISSING': return 'danger'
  }
}
function invoiceTagType(status: InvoiceStatus): 'success' | 'warning' | 'info' | 'danger' {
  switch (status) {
    case 'PAID': return 'success'
    case 'APPROVED': return 'success'
    case 'PENDING': return 'warning'
    case 'FAILED': return 'danger'
  }
}

type ProgressStatus = 'success' | 'exception' | 'warning'
function progressStatus(p: number): ProgressStatus {
  if (p === 100) return 'success'
  if (p === 0) return 'exception'
  return 'warning'
}

const tableData = ref<ContractorRow[]>([
  // 以下为 Direct 员工数据（保持当前格式）
  {
    avatar: 'https://i.pravatar.cc/100?img=26',
    contractorName: 'Maria Garcia',
    vendor: 'Direct',
    role: 'Project Manager',
    country: 'Spain',
    countryCode: 'ES',
    remote: false,
    contractId: 'DIR-2025-006',
    contractType: 'Retainer',
    rateType: 'Monthly',
    rate: 4200,
    currency: 'EUR',
    startDate: '2024-03-10',
    endDate: null,
    status: 'ACTIVE',
    timesheetStatus: 'APPROVED',
    percentage: 85,
    latestInvoice: { amount: 4200, currency: 'EUR', status: 'PAID', invoiceDate: '2025-10-04' },
    paymentMethod: 'BANK_BATCH'
  },
  {
    avatar: 'https://i.pravatar.cc/100?img=27',
    contractorName: 'Bruno Silva',
    vendor: 'Direct',
    role: 'Backend Developer',
    country: 'Brazil',
    countryCode: 'BR',
    remote: true,
    contractId: 'DIR-2025-007',
    contractType: 'T&M',
    rateType: 'Hourly',
    rate: 75,
    currency: 'BRL',
    startDate: '2024-08-01',
    endDate: null,
    status: 'ACTIVE',
    timesheetStatus: 'SUBMITTED',
    percentage: 60,
    latestInvoice: { amount: 3200, currency: 'BRL', status: 'APPROVED', invoiceDate: '2025-10-06' },
    paymentMethod: 'WIRE'
  },
  {
    avatar: 'https://i.pravatar.cc/100?img=28',
    contractorName: 'Hannah Müller',
    vendor: 'Direct',
    role: 'DevOps Engineer',
    country: 'Germany',
    countryCode: 'DE',
    remote: false,
    contractId: 'DIR-2025-008',
    contractType: 'T&M',
    rateType: 'Daily',
    rate: 600,
    currency: 'EUR',
    startDate: '2025-01-05',
    endDate: null,
    status: 'ONBOARDING',
    timesheetStatus: 'PENDING',
    percentage: 20,
    latestInvoice: { amount: 2400, currency: 'EUR', status: 'PENDING', invoiceDate: '2025-10-07' },
    paymentMethod: 'BANK_BATCH'
  },
  {
    avatar: 'https://i.pravatar.cc/100?img=29',
    contractorName: 'Wei Zhang',
    vendor: 'Direct',
    role: 'Fullstack Developer',
    country: 'China',
    countryCode: 'CN',
    remote: false,
    contractId: 'DIR-2025-009',
    contractType: 'Retainer',
    rateType: 'Monthly',
    rate: 28000,
    currency: 'CNY',
    startDate: '2023-11-20',
    endDate: null,
    status: 'ACTIVE',
    timesheetStatus: 'APPROVED',
    percentage: 95,
    latestInvoice: { amount: 28000, currency: 'CNY', status: 'PAID', invoiceDate: '2025-10-01' },
    paymentMethod: 'WIRE'
  },
  {
    avatar: 'https://i.pravatar.cc/100?img=30',
    contractorName: "Liam O'Connor",
    vendor: 'Direct',
    role: 'QA Lead',
    country: 'Ireland',
    countryCode: 'IE',
    remote: true,
    contractId: 'DIR-2025-010',
    contractType: 'Fixed-Price',
    rateType: 'Monthly',
    rate: 7000,
    currency: 'EUR',
    startDate: '2024-02-01',
    endDate: '2025-12-31',
    status: 'ENDING_SOON',
    timesheetStatus: 'SUBMITTED',
    percentage: 55,
    latestInvoice: { amount: 7000, currency: 'EUR', status: 'APPROVED', invoiceDate: '2025-10-08' },
    paymentMethod: 'MIXED'
  },
  {
    avatar: 'https://i.pravatar.cc/100?img=31',
    contractorName: 'Emily Johnson',
    vendor: 'Direct',
    role: 'Product Owner',
    country: 'Canada',
    countryCode: 'CA',
    remote: true,
    contractId: 'DIR-2025-011',
    contractType: 'Retainer',
    rateType: 'Monthly',
    rate: 8000,
    currency: 'CAD',
    startDate: '2024-06-15',
    endDate: null,
    status: 'ACTIVE',
    timesheetStatus: 'APPROVED',
    percentage: 88,
    latestInvoice: { amount: 8000, currency: 'CAD', status: 'PAID', invoiceDate: '2025-10-03' },
    paymentMethod: 'BANK_BATCH'
  },
  {
    avatar: 'https://i.pravatar.cc/100?img=32',
    contractorName: 'Ahmed Hassan',
    vendor: 'Direct',
    role: 'Security Engineer',
    country: 'United Arab Emirates',
    countryCode: 'AE',
    remote: false,
    contractId: 'DIR-2025-012',
    contractType: 'T&M',
    rateType: 'Daily',
    rate: 1200,
    currency: 'AED',
    startDate: '2025-03-01',
    endDate: null,
    status: 'COMPLIANCE_PENDING',
    timesheetStatus: 'MISSING',
    percentage: 15,
    latestInvoice: { amount: 3600, currency: 'AED', status: 'FAILED', invoiceDate: '2025-09-30' },
    paymentMethod: 'WIRE'
  },
  {
    avatar: 'https://i.pravatar.cc/100?img=33',
    contractorName: 'Nurul Aini',
    vendor: 'Direct',
    role: 'Data Analyst',
    country: 'Malaysia',
    countryCode: 'MY',
    remote: true,
    contractId: 'DIR-2025-013',
    contractType: 'T&M',
    rateType: 'Hourly',
    rate: 120,
    currency: 'MYR',
    startDate: '2024-10-01',
    endDate: null,
    status: 'ACTIVE',
    timesheetStatus: 'SUBMITTED',
    percentage: 70,
    latestInvoice: { amount: 4800, currency: 'MYR', status: 'APPROVED', invoiceDate: '2025-10-06' },
    paymentMethod: 'BANK_BATCH'
  },
  {
    avatar: 'https://i.pravatar.cc/100?img=34',
    contractorName: 'Diego Perez',
    vendor: 'Direct',
    role: 'Frontend Developer',
    country: 'Mexico',
    countryCode: 'MX',
    remote: false,
    contractId: 'DIR-2025-014',
    contractType: 'Retainer',
    rateType: 'Monthly',
    rate: 50000,
    currency: 'MXN',
    startDate: '2023-12-10',
    endDate: null,
    status: 'ACTIVE',
    timesheetStatus: 'PENDING',
    percentage: 50,
    latestInvoice: { amount: 50000, currency: 'MXN', status: 'PENDING', invoiceDate: '2025-10-05' },
    paymentMethod: 'WIRE'
  },
  {
    avatar: 'https://i.pravatar.cc/100?img=35',
    contractorName: 'Chang Liu',
    vendor: 'Direct',
    role: 'Engineering Manager',
    country: 'Singapore',
    countryCode: 'SG',
    remote: false,
    contractId: 'DIR-2025-015',
    contractType: 'Retainer',
    rateType: 'Monthly',
    rate: 12000,
    currency: 'SGD',
    startDate: '2022-11-01',
    endDate: null,
    status: 'ACTIVE',
    timesheetStatus: 'APPROVED',
    percentage: 92,
    latestInvoice: { amount: 12000, currency: 'SGD', status: 'PAID', invoiceDate: '2025-10-02' },
    paymentMethod: 'BANK_BATCH'
  }
])
</script>


<style scoped lang="scss">
.table-card {
  margin: 10px 0;
}

.contractor-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}
.contractor-info {
  display: flex;
  flex-direction: column;
}
.contractor-info .name {
  font-weight: 600;
}
.contractor-info .id {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.country-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
.flag-icon {
  width: 18px;
  height: 14px;
  border-radius: 2px;
  object-fit: cover;
}
.remote-tag {
  margin-left: 8px;
}

.contract-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}
.invoice-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>