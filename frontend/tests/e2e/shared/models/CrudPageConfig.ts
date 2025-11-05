import { Portal } from '../utils/constants';
import { COMMON_SELECTORS } from '../utils/selectors';

/**
 * CRUD 页面配置接口
 * 定义每个 CRUD 页面的特定配置
 */
export interface CrudPageConfig {
  /** Portal 类型 */
  portal: Portal;
  
  /** 实体名称（用于日志和错误消息） */
  entityName: string;
  
  /** 页面路由 */
  route: string;
  
  /** 字段映射 */
  fields: {
    /** 名称字段 */
    name: string;
    /** 类型字段 */
    type: string;
    /** 描述字段（可选） */
    description?: string;
    /** 状态字段（可选） */
    status?: string;
  };
  
  /** 选择器配置（可选，用于覆盖默认选择器） */
  selectors?: Partial<typeof COMMON_SELECTORS>;
  
  /** 页面特定配置 */
  pageConfig?: {
    /** 创建按钮文本 */
    createButtonText?: string;
    /** 编辑按钮文本 */
    editButtonText?: string;
    /** 删除按钮文本 */
    deleteButtonText?: string;
    /** 搜索框占位符 */
    searchPlaceholder?: string;
    /** 类型选择器占位符 */
    typeSelectPlaceholder?: string;
    /** 状态选择器占位符 */
    statusSelectPlaceholder?: string;
  };
  
  /** 验证配置 */
  validation?: {
    /** 必填字段验证消息 */
    requiredMessages?: Record<string, string>;
    /** 字段长度限制 */
    maxLengths?: Record<string, number>;
    /** 字段最小长度 */
    minLengths?: Record<string, number>;
  };
  
  /** 业务规则配置 */
  businessRules?: {
    /** 是否支持重复类型 */
    allowDuplicateType?: boolean;
    /** 是否支持批量操作 */
    allowBatchOperation?: boolean;
    /** 是否支持状态切换 */
    allowStatusToggle?: boolean;
  };
  
  /** 表格列索引配置 */
  columnIndexes?: {
    /** 类型列索引（用于验证筛选结果） */
    typeColumn?: number;
    /** 状态列索引（用于验证筛选结果） */
    statusColumn?: number;
  };
}

/**
 * 测试数据模型接口
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
  
  /** 创建时间 */
  createdAt?: Date;
  
  /** 更新时间 */
  updatedAt?: Date;
  
  /** 是否已验证 */
  verified?: boolean;
}

/**
 * 实体数据模型
 */
export interface EntityData extends TestDataModel {
  /** 实体类型 */
  entityType: string;
}

/**
 * 服务类型数据模型
 */
export interface ServiceTypeData extends TestDataModel {
  /** 服务类型 */
  serviceType: string;
}

/**
 * 客户端数据模型（未来）
 */
export interface ClientData extends TestDataModel {
  /** 客户端类型 */
  clientType: string;
}

/**
 * 位置数据模型（未来）
 */
export interface LocationData extends TestDataModel {
  /** 位置类型 */
  locationType: string;
  
  /** 国家 */
  country?: string;
  
  /** 城市 */
  city?: string;
}

/**
 * 服务数据模型（未来）
 */
export interface ServiceData extends TestDataModel {
  /** 服务类型 */
  serviceType: string;
  
  /** 价格 */
  price?: number;
  
  /** 货币 */
  currency?: string;
}

/**
 * 人才数据模型（未来）
 */
export interface TalentData extends TestDataModel {
  /** 人才类型 */
  talentType: string;
  
  /** 技能 */
  skills?: string[];
  
  /** 经验年限 */
  experienceYears?: number;
}

/**
 * 租户数据模型（未来）
 */
export interface TenantData extends TestDataModel {
  /** 租户类型 */
  tenantType: string;
  
  /** 订阅计划 */
  subscriptionPlan?: string;
}

/**
 * 预定义的 CRUD 页面配置
 */
export const CRUD_PAGE_CONFIGS: Record<string, CrudPageConfig> = {
  entity: {
    portal: 'admin',
    entityName: 'entity',
    route: '/entity',
    fields: {
      name: 'entityName',
      type: 'entityType',
      description: 'description',
      status: 'status'
    },
    pageConfig: {
      createButtonText: 'Create Entity',
      editButtonText: 'Edit Entity',
      deleteButtonText: 'Delete',
      searchPlaceholder: 'Enter entity name',
      typeSelectPlaceholder: 'Select entity type',
      statusSelectPlaceholder: 'Select status'
    },
    validation: {
      requiredMessages: {
        entityName: 'Please enter entity name',
        entityType: 'Please select entity type'
      }
    },
    businessRules: {
      allowDuplicateType: true,
      allowBatchOperation: true,
      allowStatusToggle: true
    },
    columnIndexes: {
      typeColumn: 1,
      statusColumn: 2
    }
  },
  
  serviceType: {
    portal: 'admin',
    entityName: 'service-type',
    route: '/service-type',
    fields: {
      name: 'serviceName',
      type: 'serviceType',
      description: 'description',
      status: 'status'
    },
    pageConfig: {
      createButtonText: 'Create Service Type',
      editButtonText: 'Edit Service Type',
      deleteButtonText: 'Delete',
      searchPlaceholder: 'Enter service type name',
      typeSelectPlaceholder: 'Select service type',
      statusSelectPlaceholder: 'Select status'
    },
    validation: {
      requiredMessages: {
        serviceName: 'Please enter service type name',
        serviceType: 'Please select service type'
      }
    },
    businessRules: {
      allowDuplicateType: false, // Service Type 不允许重复类型
      allowBatchOperation: false,
      allowStatusToggle: true
    },
    columnIndexes: {
      typeColumn: 1,
      statusColumn: 3
    }
  },
  
  client: {
    portal: 'admin',
    entityName: 'client',
    route: '/clients',
    fields: {
      name: 'clientName',
      type: 'location',
      description: 'description',
      status: 'status'
    },
    pageConfig: {
      createButtonText: 'Create Client',
      editButtonText: 'Edit Client',
      deleteButtonText: 'Delete',
      searchPlaceholder: 'Enter client name, code or alias',
      typeSelectPlaceholder: 'Select location',
      statusSelectPlaceholder: 'Select status'
    },
    validation: {
      requiredMessages: {
        clientName: '请输入客户名称',
        clientCode: '请输入客户代码',
        locationId: '请选择位置'
      }
    },
    businessRules: {
      allowDuplicateType: true,
      allowBatchOperation: true,
      allowStatusToggle: true
    },
    columnIndexes: {
      typeColumn: 2,
      statusColumn: 3
    }
  }
};

/**
 * 获取 CRUD 页面配置
 */
export function getCrudPageConfig(entityType: string): CrudPageConfig {
  const config = CRUD_PAGE_CONFIGS[entityType];
  if (!config) {
    throw new Error(`CRUD page config not found for entity type: ${entityType}`);
  }
  return config;
}

/**
 * 创建自定义 CRUD 页面配置
 */
export function createCrudPageConfig(config: Partial<CrudPageConfig>): CrudPageConfig {
  return {
    portal: 'admin',
    entityName: 'unknown',
    route: '/unknown',
    fields: {
      name: 'name',
      type: 'type'
    },
    ...config
  } as CrudPageConfig;
}
