import { TestDataResourceType } from '../../support/test-data-manager';
import { logger } from '../utils/logger';

/**
 * 测试数据模型接口
 * 定义测试数据的基本结构
 */
export interface TestDataModel {
  /** 数据名称 */
  name: string;
  
  /** 数据类型 */
  type: string;
  
  /** 描述 */
  description?: string;
  
  /** 状态 */
  status?: string;
  
  /** Portal 类型 */
  portal: string;
  
  /** 创建时间 */
  createdAt: Date;
  
  /** 是否已验证 */
  verified: boolean;
  
  /** 额外属性 */
  extra?: Record<string, any>;
}

/**
 * 测试数据创建选项
 */
export interface TestDataCreateOptions {
  /** 是否自动添加唯一后缀 */
  addUniqueSuffix?: boolean;
  
  /** 是否自动验证创建 */
  autoVerify?: boolean;
  
  /** 是否自动注册到测试数据管理器 */
  autoRegister?: boolean;
  
  /** 自定义后缀 */
  customSuffix?: string;
  
  /** 额外属性 */
  extra?: Record<string, any>;
}

/**
 * 测试数据模板
 */
export interface TestDataTemplate {
  /** 模板名称 */
  name: string;
  
  /** 模板描述 */
  description: string;
  
  /** 默认数据 */
  defaultData: Partial<TestDataModel>;
  
  /** 必填字段 */
  requiredFields: string[];
  
  /** 可选字段 */
  optionalFields: string[];
}

/**
 * 实体测试数据模板
 */
export const ENTITY_TEMPLATE: TestDataTemplate = {
  name: 'entity',
  description: 'Entity test data template',
  defaultData: {
    type: 'Client',
    status: 'Active',
    portal: 'admin'
  },
  requiredFields: ['name', 'type'],
  optionalFields: ['description', 'status']
};

/**
 * 服务类型测试数据模板
 */
export const SERVICE_TYPE_TEMPLATE: TestDataTemplate = {
  name: 'serviceType',
  description: 'Service Type test data template',
  defaultData: {
    type: 'EOR',
    status: 'Active',
    portal: 'admin'
  },
  requiredFields: ['name', 'type'],
  optionalFields: ['description', 'status']
};

/**
 * 客户端测试数据模板（未来）
 */
export const CLIENT_TEMPLATE: TestDataTemplate = {
  name: 'client',
  description: 'Client test data template',
  defaultData: {
    type: 'Enterprise',
    status: 'Active',
    portal: 'client'
  },
  requiredFields: ['name', 'type'],
  optionalFields: ['description', 'status']
};

/**
 * 位置测试数据模板（未来）
 */
export const LOCATION_TEMPLATE: TestDataTemplate = {
  name: 'location',
  description: 'Location test data template',
  defaultData: {
    type: 'Office',
    status: 'Active',
    portal: 'admin'
  },
  requiredFields: ['name', 'type'],
  optionalFields: ['description', 'status', 'country', 'city']
};

/**
 * 服务测试数据模板（未来）
 */
export const SERVICE_TEMPLATE: TestDataTemplate = {
  name: 'service',
  description: 'Service test data template',
  defaultData: {
    type: 'EOR',
    status: 'Active',
    portal: 'service'
  },
  requiredFields: ['name', 'type'],
  optionalFields: ['description', 'status', 'price', 'currency']
};

/**
 * 人才测试数据模板（未来）
 */
export const TALENT_TEMPLATE: TestDataTemplate = {
  name: 'talent',
  description: 'Talent test data template',
  defaultData: {
    type: 'Developer',
    status: 'Active',
    portal: 'talent'
  },
  requiredFields: ['name', 'type'],
  optionalFields: ['description', 'status', 'skills', 'experienceYears']
};

/**
 * 租户测试数据模板（未来）
 */
export const TENANT_TEMPLATE: TestDataTemplate = {
  name: 'tenant',
  description: 'Tenant test data template',
  defaultData: {
    type: 'Enterprise',
    status: 'Active',
    portal: 'admin'
  },
  requiredFields: ['name', 'type'],
  optionalFields: ['description', 'status', 'subscriptionPlan']
};

/**
 * 所有测试数据模板
 */
export const TEST_DATA_TEMPLATES: Record<TestDataResourceType, TestDataTemplate> = {
  entity: ENTITY_TEMPLATE,
  serviceType: SERVICE_TYPE_TEMPLATE,
  client: CLIENT_TEMPLATE,
  location: LOCATION_TEMPLATE,
  service: SERVICE_TEMPLATE,
  talent: TALENT_TEMPLATE,
  tenant: TENANT_TEMPLATE
};

/**
 * 获取测试数据模板
 */
export function getTestDataTemplate(resourceType: TestDataResourceType): TestDataTemplate {
  const template = TEST_DATA_TEMPLATES[resourceType];
  if (!template) {
    throw new Error(`Test data template not found for resource type: ${resourceType}`);
  }
  return template;
}

/**
 * 创建测试数据模型
 */
export function createTestDataModel(
  resourceType: TestDataResourceType,
  data: Partial<TestDataModel>,
  options: TestDataCreateOptions = {}
): TestDataModel {
  const template = getTestDataTemplate(resourceType);
  const {
    addUniqueSuffix = true,
    autoVerify = false,
    autoRegister = true,
    customSuffix,
    extra = {}
  } = options;

  // 生成唯一名称
  let name = data.name || `Test ${template.name}`;
  if (addUniqueSuffix) {
    const suffix = customSuffix || Date.now().toString().slice(-10);
    name = `${name}_${suffix}`;
  }

  // 合并默认数据
  const mergedData = {
    ...template.defaultData,
    ...data,
    name,
    createdAt: new Date(),
    verified: autoVerify,
    extra
  };

  // 验证必填字段
  for (const field of template.requiredFields) {
    if (!mergedData[field as keyof TestDataModel]) {
      throw new Error(`Required field '${field}' is missing for ${resourceType}`);
    }
  }

  return mergedData as TestDataModel;
}

/**
 * 验证测试数据模型
 */
export function validateTestDataModel(
  resourceType: TestDataResourceType,
  data: TestDataModel
): boolean {
  const template = getTestDataTemplate(resourceType);
  
  // 检查必填字段
  for (const field of template.requiredFields) {
    if (!data[field as keyof TestDataModel]) {
      logger.error(`Missing required field '${field}' for ${resourceType}`);
      return false;
    }
  }
  
  return true;
}
