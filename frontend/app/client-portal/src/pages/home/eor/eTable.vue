<template>
    <el-card shadow="never" class="table-card">
        <el-table :data="tableData" style="width: 100%">
            <el-table-column label="Employee" min-width="240">
                <template #default="{ row }">
                    <div class="employee-cell">
                        <el-avatar :src="row.avatar" />
                        <div class="employee-info">
                            <div class="name">{{ row.name }}</div>
                            <div class="id">Code: {{ row.code }}</div>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="serviceCountry" label="Service Country" min-width="160">
                <template #default="{ row }">
                    <div class="country-cell">
                        <img v-if="row.serviceCountryCode" :src="getFlagUrl(row.serviceCountryCode)"
                            @error="handleFlagError" alt="" class="flag-icon" />
                        <span>{{ row.serviceCountry }}</span>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="nationality" label="Nationality" min-width="140">
                <template #default="{ row }">
                    <div class="nationality-cell">
                        <img v-if="row.nationalityCode" :src="getFlagUrl(row.nationalityCode)" @error="handleFlagError"
                            class="flag-icon" />
                        <span>{{ row.nationality }}</span>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="onboardingDate" label="Onboarding Date" min-width="140" />
            <el-table-column prop="offboardingDate" label="Offboarding Date" min-width="140" />
            <el-table-column prop="service" label="Service" min-width="160" />
            <el-table-column prop="percentage" label="Service Status" min-width="140" fixed="right">
                <template #default="{ row }">
                    <el-progress :percentage="row.percentage" :status="progressStatus(row.percentage)"
                        :text-inside="true" :stroke-width="14" />
                </template>
            </el-table-column>
        </el-table>
    </el-card>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { getFlagUrl, handleFlagError } from '@I0/shared/utils/country-flag'

// Helper to map percentage to el-progress status
type ProgressStatus = 'success' | 'exception' | 'warning'
function progressStatus(p: number): ProgressStatus {
    if (p === 100) return 'success'
    if (p === 0) return 'exception'
    return 'warning'
}

// 表格数据与类型
interface EmployeeRow {
    avatar: string
    name: string
    code: string
    serviceCountry: string
    serviceCountryCode: string
    nationality: string
    nationalityCode: string
    service: string
    onboardingDate: string
    offboardingDate?: string | null
    percentage: number
}

const tableData = ref<EmployeeRow[]>([
    { avatar: 'https://i.pravatar.cc/100?img=1', name: 'Alice Tan', code: 'EOR-2025-001', serviceCountry: 'India', serviceCountryCode: 'IN', nationality: 'Singapore', nationalityCode: 'SG', service: 'Onboarding', onboardingDate: '2024-01-15', offboardingDate: null, percentage: 95 },
    { avatar: 'https://i.pravatar.cc/100?img=2', name: 'Eddie Wong', code: 'EOR-2025-002', serviceCountry: 'Singapore', serviceCountryCode: 'SG', nationality: 'China', nationalityCode: 'CN', service: 'Information Change', onboardingDate: '2024-03-10', offboardingDate: null, percentage: 40 },
    { avatar: 'https://i.pravatar.cc/100?img=3', name: 'Priya Sharma', code: 'EOR-2025-003', serviceCountry: 'India', serviceCountryCode: 'IN', nationality: 'India', nationalityCode: 'IN', service: 'Visa Renew', onboardingDate: '2023-11-05', offboardingDate: null, percentage: 85 },
    { avatar: 'https://i.pravatar.cc/100?img=4', name: 'John Smith', code: 'EOR-2025-004', serviceCountry: 'United States', serviceCountryCode: 'US', nationality: 'United States', nationalityCode: 'US', service: 'Contract Renew', onboardingDate: '2023-08-20', offboardingDate: null, percentage: 90 },
    { avatar: 'https://i.pravatar.cc/100?img=5', name: 'Emma Johnson', code: 'EOR-2025-005', serviceCountry: 'Australia', serviceCountryCode: 'AU', nationality: 'United Kingdom', nationalityCode: 'GB', service: 'Offobarding', onboardingDate: '2023-09-12', offboardingDate: '2024-09-01', percentage: 70 },
    { avatar: 'https://i.pravatar.cc/100?img=6', name: "Liam O'Connor", code: 'EOR-2025-006', serviceCountry: 'Australia', serviceCountryCode: 'AU', nationality: 'Australia', nationalityCode: 'AU', service: 'Visa Renew', onboardingDate: '2023-10-01', offboardingDate: null, percentage: 90 },
    { avatar: 'https://i.pravatar.cc/100?img=7', name: 'Sophie Dubois', code: 'EOR-2025-007', serviceCountry: 'Japan', serviceCountryCode: 'JP', nationality: 'France', nationalityCode: 'FR', service: 'Onboarding', onboardingDate: '2024-02-18', offboardingDate: null, percentage: 30 },
    { avatar: 'https://i.pravatar.cc/100?img=8', name: 'Akira Sato', code: 'EOR-2025-008', serviceCountry: 'Japan', serviceCountryCode: 'JP', nationality: 'Japan', nationalityCode: 'JP', service: 'Offobarding', onboardingDate: '2023-07-25', offboardingDate: null, percentage: 0 },
    { avatar: 'https://i.pravatar.cc/100?img=9', name: 'Carlos Ruiz', code: 'EOR-2025-009', serviceCountry: 'Canada', serviceCountryCode: 'CA', nationality: 'Canada', nationalityCode: 'CA', service: 'Information Change', onboardingDate: '2024-04-05', offboardingDate: null, percentage: 80 },
    { avatar: 'https://i.pravatar.cc/100?img=10', name: 'Hans Müller', code: 'EOR-2025-010', serviceCountry: 'Germany', serviceCountryCode: 'DE', nationality: 'Germany', nationalityCode: 'DE', service: 'Contract Renew', onboardingDate: '2023-12-11', offboardingDate: null, percentage: 95 }
])

</script>


<style scoped lang="scss">
.table-card {
    margin: 10px 0;
}

/* Flag icon styles for country/nationality cells */
.country-cell,
.nationality-cell {
    display: flex;
    align-items: center;
}

.flag-icon {
    width: 18px;
    height: 14px;
    margin-right: 8px;
    border-radius: 2px;
    object-fit: cover;
}
.employee-cell {
    display: flex;
    align-items: center;
    gap: 10px;
}
.employee-info {
    display: flex;
    flex-direction: column;
}
.employee-info .name {
    font-weight: 600;
}
.employee-info .id {
    font-size: 12px;
    color: var(--el-text-color-secondary);
}
</style>