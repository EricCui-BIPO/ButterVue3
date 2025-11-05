import request from '../../core/request';
import type { ApiResponse } from '@I0/shared/types';
import type {
  LocationOutput,
  CreateLocationInput,
  UpdateLocationInput,
  LocationPageInput,
  LocationType,
  LocationPageResponse
} from '@I0/shared/types';

// 地理位置 API 类
class LocationAPI {
  private readonly basePath = '/v1/locations';

  // 分页查询地理位置
  async getLocationPage(params: LocationPageInput = {}): Promise<LocationPageResponse> {
    return request.get<LocationPageResponse['data']>(this.basePath, params);
  }

  // 获取所有地理位置
  async getAllLocations(): Promise<ApiResponse<LocationOutput[]>> {
    return request.get<LocationOutput[]>(`${this.basePath}`);
  }

  // 获取树形结构的地理位置
  async getLocationsTree(): Promise<ApiResponse<LocationOutput[]>> {
    return request.get<LocationOutput[]>(`${this.basePath}/tree`);
  }

  // 根据ID获取地理位置
  async getLocationById(id: string): Promise<ApiResponse<LocationOutput>> {
    return request.get<LocationOutput>(`${this.basePath}/${id}`);
  }

  // 创建地理位置
  async createLocation(data: CreateLocationInput): Promise<ApiResponse<LocationOutput>> {
    return request.post<LocationOutput>(`${this.basePath}`, data);
  }

  // 更新地理位置
  async updateLocation(
    id: string,
    data: UpdateLocationInput
  ): Promise<ApiResponse<LocationOutput>> {
    return request.put<LocationOutput>(`${this.basePath}/${id}`, data);
  }

  // 删除地理位置
  async deleteLocation(id: string): Promise<ApiResponse<void>> {
    return request.delete<void>(`${this.basePath}/${id}`);
  }

  // 根据上级ID获取地理位置列表
  async getLocationsByParentId(parentId: string): Promise<ApiResponse<LocationOutput[]>> {
    return request.get<LocationOutput[]>(`${this.basePath}/by-parent/${parentId}`);
  }

  // 根据类型获取地理位置列表
  async getLocationsByType(locationType: string): Promise<ApiResponse<LocationOutput[]>> {
    return request.get<LocationOutput[]>(`${this.basePath}/by-type/${locationType}`);
  }

  // 激活地理位置
  async activateLocation(id: string): Promise<ApiResponse<LocationOutput>> {
    return request.patch<LocationOutput>(`${this.basePath}/${id}/activate`);
  }

  // 停用地理位置
  async deactivateLocation(id: string): Promise<ApiResponse<LocationOutput>> {
    return request.patch<LocationOutput>(`${this.basePath}/${id}/deactivate`);
  }

  // 获取激活的地理位置
  async getActiveLocations(locationType?: LocationType): Promise<ApiResponse<LocationOutput[]>> {
    const url = locationType
      ? `${this.basePath}/active?locationType=${locationType}`
      : `${this.basePath}/active`;
    return request.get<LocationOutput[]>(url);
  }

  // 获取所有地理位置类型
  async getLocationTypes(): Promise<ApiResponse<LocationType[]>> {
    return request.get<LocationType[]>(`${this.basePath}/types`);
  }

  // 获取所有国家（用于国籍选择）
  async getCountries(): Promise<ApiResponse<LocationOutput[]>> {
    return this.getLocationsByType('COUNTRY');
  }

  // 获取工作地点（非国家类型的地点）
  async getWorkLocations(): Promise<ApiResponse<LocationOutput[]>> {
    const response = await this.getLocationPage({ activeOnly: true });
    if (response.success && response.data) {
      // 过滤掉国家类型，保留其他类型作为工作地点
      const workLocations =
        response.data.content?.filter(location => location.type !== 'COUNTRY') || [];
      return {
        ...response,
        data: {
          ...response.data,
          content: workLocations
        }
      };
    }
    return response;
  }

  // 搜索地点
  async searchLocations(keyword: string): Promise<ApiResponse<LocationOutput[]>> {
    return this.getLocationPage({
      name: keyword,
      activeOnly: true,
      size: 50 // 限制搜索结果数量
    });
  }
}

// 创建 API 实例
export const locationAPI = new LocationAPI();
