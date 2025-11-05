<template>
    <el-card shadow="never" class="table-card">
        <el-table :data="tableData" style="width: 100%">
            <el-table-column label="Contractor" min-width="240">
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
            <el-table-column prop="role" label="Role" min-width="160" />
            <el-table-column label="Country" min-width="160">
                <template #default="{ row }">
                    <div class="country-cell">
                        <img v-if="row.countryCode" :src="getFlagUrl(row.countryCode)" @error="handleFlagError" class="flag-icon" />
                        <span>{{ row.country }}</span>
                        <el-tag v-if="row.remote" type="info" size="small" class="remote-tag">Remote</el-tag>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="Contract" min-width="220">
                <template #default="{ row }">
                    <div class="contract-cell">
                        <span class="type">{{ row.contractType }}</span>
                        <span class="dot">•</span>
                        <span class="rate">{{ formatCurrency(row.rate, row.currency) }}/{{ row.rateType.toLowerCase() }}</span>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="startDate" label="Start" min-width="120" />
            <el-table-column prop="endDate" label="End" min-width="120" />
            <el-table-column label="Timesheet" min-width="160">
                <template #default="{ row }">
                    <el-tag :type="timesheetTagType(row.timesheetStatus)" round>{{ row.timesheetStatus }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="Invoice" min-width="200">
                <template #default="{ row }">
                    <div class="invoice-cell">
                        <span class="amount">{{ formatCurrency(row.latestInvoice.amount, row.latestInvoice.currency) }}</span>
                        <el-tag :type="invoiceTagType(row.latestInvoice.status)" round>{{ row.latestInvoice.status }}</el-tag>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="paymentMethod" label="Payment" min-width="140" />
            <el-table-column prop="percentage" label="Service Status" min-width="140" fixed="right">
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
  {
    avatar: 'https://i.pravatar.cc/100?img=21',
    contractorName: 'Alex Chen',
    vendor: 'Upwork',
    role: 'Frontend Developer',
    country: 'Singapore',
    countryCode: 'SG',
    remote: true,
    contractId: 'CTR-2025-001',
    contractType: 'T&M',
    rateType: 'Hourly',
    rate: 85,
    currency: 'USD',
    startDate: '2024-06-01',
    endDate: null,
    status: 'ACTIVE',
    timesheetStatus: 'SUBMITTED',
    percentage: 75,
    latestInvoice: { amount: 5200, currency: 'USD', status: 'APPROVED', invoiceDate: '2025-10-05' },
    paymentMethod: 'WIRE'
  },
  {
    avatar: 'https://i.pravatar.cc/100?img=22',
    contractorName: 'Priya Sharma',
    vendor: 'Toptal',
    role: 'Data Engineer',
    country: 'India',
    countryCode: 'IN',
    remote: true,
    contractId: 'CTR-2025-002',
    contractType: 'Retainer',
    rateType: 'Monthly',
    rate: 6000,
    currency: 'USD',
    startDate: '2024-01-15',
    endDate: null,
    status: 'ACTIVE',
    timesheetStatus: 'APPROVED',
    percentage: 90,
    latestInvoice: { amount: 6000, currency: 'USD', status: 'PAID', invoiceDate: '2025-10-02' },
    paymentMethod: 'BANK_BATCH'
  },
  {
    avatar: 'https://i.pravatar.cc/100?img=23',
    contractorName: 'John Smith',
    vendor: 'Local Vendor',
    role: 'Backend Developer',
    country: 'United States',
    countryCode: 'US',
    remote: false,
    contractId: 'CTR-2025-003',
    contractType: 'Fixed-Price',
    rateType: 'Monthly',
    rate: 8000,
    currency: 'USD',
    startDate: '2023-09-01',
    endDate: '2025-10-31',
    status: 'ENDING_SOON',
    timesheetStatus: 'PENDING',
    percentage: 40,
    latestInvoice: { amount: 8000, currency: 'USD', status: 'PENDING', invoiceDate: '2025-10-03' },
    paymentMethod: 'WIRE'
  },
  {
    avatar: 'https://i.pravatar.cc/100?img=24',
    contractorName: 'Sophie Dubois',
    vendor: 'Freelance',
    role: 'UI/UX Designer',
    country: 'France',
    countryCode: 'FR',
    remote: true,
    contractId: 'CTR-2024-018',
    contractType: 'T&M',
    rateType: 'Daily',
    rate: 450,
    currency: 'EUR',
    startDate: '2024-05-20',
    endDate: null,
    status: 'COMPLIANCE_PENDING',
    timesheetStatus: 'MISSING',
    percentage: 10,
    latestInvoice: { amount: 1800, currency: 'EUR', status: 'FAILED', invoiceDate: '2025-09-30' },
    paymentMethod: 'MIXED'
  },
  {
    avatar: 'https://i.pravatar.cc/100?img=25',
    contractorName: 'Akira Sato',
    vendor: 'Local Vendor',
    role: 'QA Engineer',
    country: 'Japan',
    countryCode: 'JP',
    remote: false,
    contractId: 'CTR-2023-112',
    contractType: 'T&M',
    rateType: 'Hourly',
    rate: 5000,
    currency: 'JPY',
    startDate: '2023-07-25',
    endDate: '2025-09-30',
    status: 'ENDED',
    timesheetStatus: 'APPROVED',
    percentage: 0,
    latestInvoice: { amount: 250000, currency: 'JPY', status: 'PAID', invoiceDate: '2025-09-28' },
    paymentMethod: 'PAYROLL_CARD'
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