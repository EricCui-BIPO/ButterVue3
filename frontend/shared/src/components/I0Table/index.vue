<template>
  <div class="i0-table">
    <!-- Default Slot -->
    <div class="i0-table__default">
      <slot></slot>
    </div>

    <el-table
      :data="tableData"
      :stripe="stripe"
      :border="border"
      :size="size"
      :highlight-current-row="highlightCurrentRow"
      v-loading="loading"
      :empty-text="emptyText"
      @sort-change="handleSortChange"
      @row-click="handleRowClick"
      @cell-click="handleCellClick"
    >
      <!-- Dynamic Columns -->
      <el-table-column
        v-for="column in tableColumn"
        :key="column.prop"
        :prop="column.prop"
        :label="column.name"
        :width="column.width"
        :min-width="column.minWidth"
        :fixed="column.fixed"
        :sortable="column.sortable"
        :align="column.align || 'left'"
        :show-overflow-tooltip="column.showOverflowTooltip"
        :class-name="column.className"
      >
        <template #default="scope">
          <!-- Actions Column Slot -->
          <slot
            v-if="column.prop === 'actions'"
            name="actions"
            :row="scope.row"
            :column="column"
            :index="scope.$index"
          />

          <!-- Dynamic Column Slot -->
          <slot
            v-else-if="getColumnSlotName(column)"
            :name="getColumnSlotName(column)"
            :row="scope.row"
            :column="column"
            :value="getRawCellValue(scope.row, column)"
            :formatted-value="getFormattedCellValue(scope.row, column)"
            :index="scope.$index"
          />

          <!-- Default Cell Rendering -->
          <template v-else>
            {{ getCellValue(scope.row, column) }}
          </template>
        </template>
      </el-table-column>

      <template #empty>
        <slot name="empty">
          <div class="i0-table__empty-message">No data available</div>
        </slot>
      </template>
    </el-table>

    <div class="i0-table__pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="paginationConfig?.pageSizes"
        :total="paginationConfig?.total"
        :layout="paginationConfig?.layout"
        :small="paginationConfig?.small"
        :background="true"
        @size-change="handlePageSizeChange"
        @current-change="handlePageChange"
      />
    </div>
    <!-- <pre>pagination: {{ pagination }}</pre>
    <pre>paginationConfig: {{ paginationConfig }}</pre>
    <pre>currentPage: {{ currentPage }}</pre>
    <pre>pageSize: {{ pageSize }}</pre> -->
  </div>
</template>

<script setup lang="ts">
  import { computed, ref, watch } from 'vue'
  import {
    getCellValue,
    getColumnSlotName,
    getFormattedCellValue,
    getRawCellValue
  } from './utils/formatValue'
  import type { I0TableProps, I0TableEmits } from './types'

  // Props definition
  const props = withDefaults(defineProps<I0TableProps>(), {
    loading: false,
    stripe: false,
    border: false,
    size: 'default',
    highlightCurrentRow: true,
    emptyText: 'No data available'
  })

  // Emits definition
  const emit = defineEmits<I0TableEmits>()

  // Reactive state
  const currentSort = ref<{
    prop: string
    order: 'ascending' | 'descending' | null
  }>({
    prop: '',
    order: null
  })

  const currentRow = ref<number | null>(null)

  // 分页状态管理
  const currentPage = ref(props.pagination?.currentPage || 1)
  const pageSize = ref(props.pagination?.pageSize || 20)

  // 分页配置
  const paginationConfig = computed(() => {
    return {
      total: props.pagination!.total,
      currentPage: props.pagination!.currentPage || 1,
      pageSize: props.pagination!.pageSize || 20,
      pageSizes: props.pagination!.pageSizes || [10, 20, 50, 100],
      layout: props.pagination!.layout || 'total, sizes, prev, pager, next, jumper',
      small: props.pagination!.small !== false
    }
  })

  // Event handlers
  const handleSortChange = (params: {
    column: any
    prop: string
    order: 'ascending' | 'descending' | null
  }) => {
    const { prop, order } = params

    // Update current sort state
    currentSort.value = {
      prop,
      order
    }

    // Find the column configuration
    const columnConfig = props.tableColumn.find(col => col.prop === prop)

    emit('sort-change', {
      column: columnConfig || { prop, name: prop, type: 'string' },
      order,
      prop
    })
  }

  const handleRowClick = (row: Record<string, any>) => {
    const index = props.tableData.indexOf(row)

    if (props.highlightCurrentRow) {
      currentRow.value = index
    }

    emit('row-click', {
      row,
      index,
      event: new Event('row-click')
    })
  }

  const handleCellClick = (row: Record<string, any>, column: any, cellValue: any) => {
    const columnConfig = props.tableColumn.find(col => col.prop === column.property)

    emit('cell-click', {
      row,
      column: columnConfig || column,
      value: cellValue
    })
  }

  // 分页事件处理
  const handlePageSizeChange = (newSize: number) => {
    pageSize.value = newSize
    currentPage.value = 1

    emit('pagination-change', {
      page: currentPage.value,
      pageSize: newSize
    })
  }

  const handlePageChange = (newPage: number) => {
    currentPage.value = newPage

    emit('pagination-change', {
      page: newPage,
      pageSize: pageSize.value
    })
  }

  // Public methods (expose)
  const refresh = () => {
    emit('refresh')
  }

  const getSortState = () => {
    return { ...currentSort.value }
  }

  const getPaginationState = () => {
    return {
      page: currentPage.value,
      pageSize: pageSize.value,
      total: props.pagination!.total
    }
  }

  // Watch for pagination changes
  watch(
    () => props.pagination,
    newPagination => {
      if (newPagination) {
        if (newPagination.currentPage !== undefined) {
          currentPage.value = newPagination.currentPage
        }
        if (newPagination.pageSize !== undefined) {
          pageSize.value = newPagination.pageSize
        }
      }
    },
    { immediate: true }
  )

  // Expose methods
  defineExpose({
    refresh,
    getSortState,
    getPaginationState
  })
</script>

<style scoped lang="scss">
  .i0-table {
    position: relative;
    width: 100%;
    border-collapse: collapse;

    .i0-table__pagination {
      padding: 10px 0;
      text-align: center;
      display: flex;
      justify-content: center;
      align-items: center;
      width: 100%;
    }
  }
</style>
