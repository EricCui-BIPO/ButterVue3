import { logger } from '../shared/utils/logger';

/**
 * 统一测试数据管理器
 * 负责记录、验证和管理所有类型的测试数据，支持多模块清理
 */

export type TestDataResourceType = 
  | 'entity' 
  | 'serviceType' 
  | 'client' 
  | 'location' 
  | 'service' 
  | 'talent' 
  | 'tenant';

export interface TestDataRecord {
  type: TestDataResourceType;
  name: string;
  createdAt: number;
  verified: boolean; // 是否验证创建成功
}

export interface TestDataStats {
  totalRecords: number;
  verifiedRecords: number;
  unverifiedRecords: number;
  recordsByType: Record<TestDataResourceType, number>;
}

/**
 * 测试数据管理器
 * 提供统一的测试数据记录、验证和清理接口
 */
export class TestDataManager {
  private records: TestDataRecord[] = [];
  private readonly testSessionId: string;

  constructor() {
    // 使用时间戳作为测试会话ID，用于数据隔离
    this.testSessionId = Date.now().toString();
  }

  /**
   * 记录测试数据
   * @param type 资源类型
   * @param name 数据名称
   * @param verified 是否已验证创建成功，默认为 false
   */
  recordData(type: TestDataResourceType, name: string, verified: boolean = false): void {
    // 避免重复记录
    const existingRecord = this.records.find(record => 
      record.type === type && record.name === name
    );
    
    if (existingRecord) {
      // 更新验证状态
      existingRecord.verified = verified;
      // 数据记录更新，无需详细日志
    } else {
      // 新增记录
      const record: TestDataRecord = {
        type,
        name,
        createdAt: Date.now(),
        verified
      };
      this.records.push(record);
      // 新数据记录，无需详细日志
    }
  }

  /**
   * 验证数据创建成功
   * @param type 资源类型
   * @param name 数据名称
   */
  verifyDataCreated(type: TestDataResourceType, name: string): void {
    const record = this.records.find(r => r.type === type && r.name === name);
    if (record) {
      record.verified = true;
      // 验证成功，无需详细日志
    } else {
      logger.warn(`⚠️ 尝试验证未记录的测试数据: ${type} - ${name}`);
    }
  }

  /**
   * 获取指定类型的所有记录
   * @param type 资源类型
   * @returns 该类型的所有记录
   */
  getRecordsByType(type: TestDataResourceType): TestDataRecord[] {
    return this.records.filter(record => record.type === type);
  }

  /**
   * 获取所有已验证的记录
   * @returns 已验证的记录列表
   */
  getVerifiedRecords(): TestDataRecord[] {
    return this.records.filter(record => record.verified);
  }

  /**
   * 获取所有未验证的记录
   * @returns 未验证的记录列表
   */
  getUnverifiedRecords(): TestDataRecord[] {
    return this.records.filter(record => !record.verified);
  }

  /**
   * 获取所有记录
   * @returns 所有记录列表
   */
  getAllRecords(): TestDataRecord[] {
    return [...this.records];
  }

  /**
   * 获取测试数据统计信息
   * @returns 统计信息
   */
  getStats(): TestDataStats {
    const verifiedRecords = this.getVerifiedRecords();
    const unverifiedRecords = this.getUnverifiedRecords();
    
    const recordsByType: Record<TestDataResourceType, number> = {
      entity: 0,
      serviceType: 0,
      client: 0,
      location: 0,
      service: 0,
      talent: 0,
      tenant: 0
    };

    this.records.forEach(record => {
      recordsByType[record.type]++;
    });

    return {
      totalRecords: this.records.length,
      verifiedRecords: verifiedRecords.length,
      unverifiedRecords: unverifiedRecords.length,
      recordsByType
    };
  }

  /**
   * 检查是否有指定类型的记录
   * @param type 资源类型
   * @returns 是否有该类型的记录
   */
  hasRecordsOfType(type: TestDataResourceType): boolean {
    return this.records.some(record => record.type === type);
  }

  /**
   * 检查是否有任何记录
   * @returns 是否有记录
   */
  hasAnyRecords(): boolean {
    return this.records.length > 0;
  }

  /**
   * 清空所有记录
   */
  clearRecords(): void {
    const count = this.records.length;
    this.records = [];
    // 清空记录，仅在有数据时记录
    if (count > 0) {
      logger.debug(`🧹 清空测试数据记录: ${count} 条记录`);
    }
  }

  /**
   * 获取测试会话ID
   * @returns 测试会话ID
   */
  getTestSessionId(): string {
    return this.testSessionId;
  }

  /**
   * 打印当前记录状态（调试用）
   */
  printStatus(): void {
    const stats = this.getStats();
    // 仅在有数据时输出状态
    if (stats.totalRecords > 0) {
      logger.debug('📊 测试数据管理器状态:');
      logger.debug(`  总记录数: ${stats.totalRecords}`);
      logger.debug(`  已验证: ${stats.verifiedRecords}`);
      logger.debug(`  未验证: ${stats.unverifiedRecords}`);
    }
  }

  /**
   * 导出记录为JSON（调试用）
   * @returns JSON格式的记录数据
   */
  exportToJson(): string {
    return JSON.stringify({
      testSessionId: this.testSessionId,
      records: this.records,
      stats: this.getStats()
    }, null, 2);
  }
}
