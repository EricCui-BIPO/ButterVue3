// Shared types for StepForm and parent pages
export interface FormField {
  key: string
  label: string
  type: 'input' | 'number' | 'select' | 'textarea' | 'date' | 'switch' | 'radio' | 'checkbox-group' | 'checkbox'
  placeholder: string
  focusKey: string

  // common
  min?: number
  options?: { label: string; value: string | number }[]
  clearable?: boolean

  // select
  multiple?: boolean

  // textarea
  rows?: number

  // date
  dateType?: 'year' | 'month' | 'date' | 'dates' | 'week' | 'datetime' | 'datetimerange' | 'daterange' | 'monthrange'
  valueFormat?: string
  startPlaceholder?: string
  endPlaceholder?: string

  // switch
  activeValue?: any
  inactiveValue?: any
  activeText?: string
  inactiveText?: string

  // explanation
  explainTitle: string
  explainText: string
}

export interface StepConfig {
  key: string
  title: string
  formData: any
  fields: FormField[]
}