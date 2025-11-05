package com.i0.client.domain.specifications;

import com.i0.client.domain.entities.Client;

/**
 * 客户规约接口
 * 用于封装复杂的查询条件和业务规则
 */
public interface ClientSpecification {
    
    /**
     * 检查客户是否满足规约条件
     * @param client 要检查的客户
     * @return true如果满足条件
     */
    boolean isSatisfiedBy(Client client);
    
    /**
     * 与操作
     * @param other 另一个规约
     * @return 组合后的规约
     */
    default ClientSpecification and(ClientSpecification other) {
        return new AndSpecification(this, other);
    }
    
    /**
     * 或操作
     * @param other 另一个规约
     * @return 组合后的规约
     */
    default ClientSpecification or(ClientSpecification other) {
        return new OrSpecification(this, other);
    }
    
    /**
     * 非操作
     * @return 取反后的规约
     */
    default ClientSpecification not() {
        return new NotSpecification(this);
    }
    
    /**
     * 激活状态规约
     */
    class ActiveClientSpecification implements ClientSpecification {
        @Override
        public boolean isSatisfiedBy(Client client) {
            return client != null && client.isActive();
        }
    }
    
    /**
     * 位置规约
     */
    class LocationSpecification implements ClientSpecification {
        private final String locationId;

        public LocationSpecification(String locationId) {
            this.locationId = locationId;
        }

        @Override
        public boolean isSatisfiedBy(Client client) {
            return client != null &&
                   client.getLocationId() != null &&
                   client.getLocationId().equals(locationId);
        }
    }
    
    /**
     * 名称包含规约
     */
    class NameContainsSpecification implements ClientSpecification {
        private final String keyword;
        
        public NameContainsSpecification(String keyword) {
            this.keyword = keyword != null ? keyword.toLowerCase() : "";
        }
        
        @Override
        public boolean isSatisfiedBy(Client client) {
            return client != null && 
                   client.getName() != null && 
                   client.getName().toLowerCase().contains(keyword);
        }
    }
    
    /**
     * 代码包含规约
     */
    class CodeContainsSpecification implements ClientSpecification {
        private final String keyword;
        
        public CodeContainsSpecification(String keyword) {
            this.keyword = keyword != null ? keyword.toLowerCase() : "";
        }
        
        @Override
        public boolean isSatisfiedBy(Client client) {
            return client != null && 
                   client.getCode() != null && 
                   client.getCode().toLowerCase().contains(keyword);
        }
    }
    
    /**
     * 别名包含规约
     */
    class AliasNameContainsSpecification implements ClientSpecification {
        private final String keyword;
        
        public AliasNameContainsSpecification(String keyword) {
            this.keyword = keyword != null ? keyword.toLowerCase() : "";
        }
        
        @Override
        public boolean isSatisfiedBy(Client client) {
            return client != null && 
                   client.hasAliasName() && 
                   client.getAliasName().toLowerCase().contains(keyword);
        }
    }
    
    /**
     * 与规约实现
     */
    class AndSpecification implements ClientSpecification {
        private final ClientSpecification left;
        private final ClientSpecification right;
        
        public AndSpecification(ClientSpecification left, ClientSpecification right) {
            this.left = left;
            this.right = right;
        }
        
        @Override
        public boolean isSatisfiedBy(Client client) {
            return left.isSatisfiedBy(client) && right.isSatisfiedBy(client);
        }
    }
    
    /**
     * 或规约实现
     */
    class OrSpecification implements ClientSpecification {
        private final ClientSpecification left;
        private final ClientSpecification right;
        
        public OrSpecification(ClientSpecification left, ClientSpecification right) {
            this.left = left;
            this.right = right;
        }
        
        @Override
        public boolean isSatisfiedBy(Client client) {
            return left.isSatisfiedBy(client) || right.isSatisfiedBy(client);
        }
    }
    
    /**
     * 非规约实现
     */
    class NotSpecification implements ClientSpecification {
        private final ClientSpecification specification;
        
        public NotSpecification(ClientSpecification specification) {
            this.specification = specification;
        }
        
        @Override
        public boolean isSatisfiedBy(Client client) {
            return !specification.isSatisfiedBy(client);
        }
    }
}