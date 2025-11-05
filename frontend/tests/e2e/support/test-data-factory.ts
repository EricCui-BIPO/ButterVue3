import { TestDataResourceType, TestDataManager } from './test-data-manager';
import { 
  TestDataModel, 
  TestDataCreateOptions,
  createTestDataModel,
  validateTestDataModel
} from '../shared/models/TestDataModel';
import { Portal } from '../shared/utils/constants';
import { logger } from '../shared/utils/logger';

// 定义具体的测试数据类型
interface EntityData extends TestDataModel {
  entityType: string;
}

interface ServiceTypeData extends TestDataModel {
  serviceType: string;
}

interface ClientData extends TestDataModel {
  clientType: string;
}

interface LocationData extends TestDataModel {
  locationType: string;
}

interface ServiceData extends TestDataModel {
  serviceType: string;
}

interface TalentData extends TestDataModel {
  talentType: string;
}

interface TenantData extends TestDataModel {
  tenantType: string;
}

/**
 * 测试数据工厂
 * 提供统一的测试数据创建和管理接口
 */
export class TestDataFactory {
  private static dataManager: TestDataManager;

  /**
   * 初始化数据管理器
   */
  static initialize(dataManager: TestDataManager): void {
    this.dataManager = dataManager;
  }

  /**
   * 获取数据管理器实例
   */
  private static getDataManager(): TestDataManager {
    if (!this.dataManager) {
      this.dataManager = new TestDataManager();
    }
    return this.dataManager;
  }

  /**
   * 创建实体测试数据
   */
  static createEntity(
    portal: Portal, 
    data: Partial<EntityData>, 
    options: TestDataCreateOptions = {}
  ): EntityData {
    const entityData = createTestDataModel('entity', {
      ...data,
      portal,
      extra: { entityType: data.entityType || 'Client' }
    }, options);

    // 自动注册到数据管理器
    if (options.autoRegister !== false) {
      this.getDataManager().recordData('entity', entityData.name, options.autoVerify || false);
    }

    return entityData as EntityData;
  }

  /**
   * 创建服务类型测试数据
   */
  static createServiceType(
    portal: Portal, 
    data: Partial<ServiceTypeData>, 
    options: TestDataCreateOptions = {}
  ): ServiceTypeData {
    const serviceTypeData = createTestDataModel('serviceType', {
      ...data,
      portal,
      extra: { serviceType: data.serviceType || 'EOR' }
    }, options);

    // 自动注册到数据管理器
    if (options.autoRegister !== false) {
      this.getDataManager().recordData('serviceType', serviceTypeData.name, options.autoVerify || false);
    }

    return serviceTypeData as ServiceTypeData;
  }

  /**
   * 创建客户端测试数据（未来）
   */
  static createClient(
    portal: Portal, 
    data: Partial<ClientData>, 
    options: TestDataCreateOptions = {}
  ): ClientData {
    const clientData = createTestDataModel('client', {
      ...data,
      portal,
      extra: { clientType: data.clientType || 'Enterprise' }
    }, options);

    // 自动注册到数据管理器
    if (options.autoRegister !== false) {
      this.getDataManager().recordData('client', clientData.name, options.autoVerify || false);
    }

    return clientData as ClientData;
  }

  /**
   * 创建位置测试数据（未来）
   */
  static createLocation(
    portal: Portal, 
    data: Partial<LocationData>, 
    options: TestDataCreateOptions = {}
  ): LocationData {
    const locationData = createTestDataModel('location', {
      ...data,
      portal,
      extra: { locationType: data.locationType || 'Office' }
    }, options);

    // 自动注册到数据管理器
    if (options.autoRegister !== false) {
      this.getDataManager().recordData('location', locationData.name, options.autoVerify || false);
    }

    return locationData as LocationData;
  }

  /**
   * 创建服务测试数据（未来）
   */
  static createService(
    portal: Portal, 
    data: Partial<ServiceData>, 
    options: TestDataCreateOptions = {}
  ): ServiceData {
    const serviceData = createTestDataModel('service', {
      ...data,
      portal,
      extra: { serviceType: data.serviceType || 'EOR' }
    }, options);

    // 自动注册到数据管理器
    if (options.autoRegister !== false) {
      this.getDataManager().recordData('service', serviceData.name, options.autoVerify || false);
    }

    return serviceData as ServiceData;
  }

  /**
   * 创建人才测试数据（未来）
   */
  static createTalent(
    portal: Portal, 
    data: Partial<TalentData>, 
    options: TestDataCreateOptions = {}
  ): TalentData {
    const talentData = createTestDataModel('talent', {
      ...data,
      portal,
      extra: { talentType: data.talentType || 'Developer' }
    }, options);

    // 自动注册到数据管理器
    if (options.autoRegister !== false) {
      this.getDataManager().recordData('talent', talentData.name, options.autoVerify || false);
    }

    return talentData as TalentData;
  }

  /**
   * 创建租户测试数据（未来）
   */
  static createTenant(
    portal: Portal, 
    data: Partial<TenantData>, 
    options: TestDataCreateOptions = {}
  ): TenantData {
    const tenantData = createTestDataModel('tenant', {
      ...data,
      portal,
      extra: { tenantType: data.tenantType || 'Enterprise' }
    }, options);

    // 自动注册到数据管理器
    if (options.autoRegister !== false) {
      this.getDataManager().recordData('tenant', tenantData.name, options.autoVerify || false);
    }

    return tenantData as TenantData;
  }

  /**
   * 批量创建测试数据
   */
  static createBatch<T extends TestDataModel>(
    resourceType: TestDataResourceType,
    portal: Portal,
    dataList: Partial<T>[],
    options: TestDataCreateOptions = {}
  ): T[] {
    const results: T[] = [];

    for (const data of dataList) {
      let createdData: T;
      
      switch (resourceType) {
        case 'entity':
          createdData = this.createEntity(portal, data as Partial<EntityData>, options) as unknown as T;
          break;
        case 'serviceType':
          createdData = this.createServiceType(portal, data as Partial<ServiceTypeData>, options) as unknown as T;
          break;
        case 'client':
          createdData = this.createClient(portal, data as Partial<ClientData>, options) as unknown as T;
          break;
        case 'location':
          createdData = this.createLocation(portal, data as Partial<LocationData>, options) as unknown as T;
          break;
        case 'service':
          createdData = this.createService(portal, data as Partial<ServiceData>, options) as unknown as T;
          break;
        case 'talent':
          createdData = this.createTalent(portal, data as Partial<TalentData>, options) as unknown as T;
          break;
        case 'tenant':
          createdData = this.createTenant(portal, data as Partial<TenantData>, options) as unknown as T;
          break;
        default:
          throw new Error(`Unsupported resource type: ${resourceType}`);
      }
      
      results.push(createdData);
    }

    return results;
  }

  /**
   * 验证测试数据
   */
  static validateData(resourceType: TestDataResourceType, data: TestDataModel): boolean {
    return validateTestDataModel(resourceType, data);
  }

  /**
   * 生成唯一名称
   */
  static generateUniqueName(baseName: string, suffix?: string): string {
    const timestamp = Date.now().toString().slice(-10);
    const customSuffix = suffix || timestamp;
    return `${baseName}_${customSuffix}`;
  }

  /**
   * 生成测试数据模板
   */
  static generateTemplate(resourceType: TestDataResourceType, portal: Portal): Partial<TestDataModel> {
    const baseTemplate = {
      name: `Test ${resourceType}`,
      portal,
      status: 'Active',
      createdAt: new Date(),
      verified: false
    };

    switch (resourceType) {
      case 'entity':
        return { ...baseTemplate, extra: { entityType: 'Client' } };
      case 'serviceType':
        return { ...baseTemplate, extra: { serviceType: 'EOR' } };
      case 'client':
        return { ...baseTemplate, extra: { clientType: 'Enterprise' } };
      case 'location':
        return { ...baseTemplate, extra: { locationType: 'Office' } };
      case 'service':
        return { ...baseTemplate, extra: { serviceType: 'EOR' } };
      case 'talent':
        return { ...baseTemplate, extra: { talentType: 'Developer' } };
      case 'tenant':
        return { ...baseTemplate, extra: { tenantType: 'Enterprise' } };
      default:
        return baseTemplate;
    }
  }

  /**
   * 清理所有测试数据
   */
  static async cleanupAll(): Promise<void> {
    const dataManager = this.getDataManager();
    const allRecords = dataManager.getAllRecords();
    
    logger.info(`🧹 开始清理所有测试数据: ${allRecords.length} 条记录`);
    
    for (const record of allRecords) {
      logger.debug(`🧹 清理测试数据: ${record.type} - ${record.name}`);
    }
    
    dataManager.clearRecords();
    logger.info('✅ 测试数据清理完成');
  }

  /**
   * 获取测试数据统计
   */
  static getStats() {
    return this.getDataManager().getStats();
  }

  /**
   * 导出测试数据（调试用）
   */
  static exportData(): string {
    return this.getDataManager().exportToJson();
  }
}
