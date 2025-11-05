package com.i0.agents.domain.services;

import com.i0.agents.domain.valueobjects.BusinessFunction;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 业务函数注册器
 * 负责管理和注册所有可被AI调用的业务函数
 */
@Slf4j
public class BusinessFunctionRegistry {

    private final Map<String, BusinessFunction> functions = new ConcurrentHashMap<>();

    /**
     * 注册业务函数
     */
    public void register(BusinessFunction function) {
        if (function == null) {
            throw new IllegalArgumentException("Function cannot be null");
        }

        String functionName = function.getName();
        if (functions.containsKey(functionName)) {
            log.warn("Function '{}' is already registered, it will be overwritten", functionName);
        }

        functions.put(functionName, function);
        log.info("Registered business function: {}", functionName);
    }

    /**
     * 批量注册业务函数
     */
    public void registerAll(Collection<BusinessFunction> functions) {
        if (functions != null) {
            functions.forEach(this::register);
        }
    }

    /**
     * 获取指定名称的业务函数
     */
    public Optional<BusinessFunction> getFunction(String name) {
        return Optional.ofNullable(functions.get(name));
    }

    /**
     * 获取所有已注册的函数
     */
    public Collection<BusinessFunction> getAllFunctions() {
        return new ArrayList<>(functions.values());
    }

    /**
     * 获取所有函数的BigModel API格式定义
     */
    public List<Map<String, Object>> getAllFunctionDefinitions() {
        return functions.values().stream()
            .map(BusinessFunction::toBigModelFunctionDefinition)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 检查函数是否已注册
     */
    public boolean isRegistered(String functionName) {
        return functions.containsKey(functionName);
    }

    /**
     * 注销函数
     */
    public boolean unregister(String functionName) {
        BusinessFunction removed = functions.remove(functionName);
        if (removed != null) {
            log.info("Unregistered business function: {}", functionName);
            return true;
        }
        return false;
    }

    /**
     * 清空所有注册的函数
     */
    public void clear() {
        int count = functions.size();
        functions.clear();
        log.info("Cleared {} registered functions", count);
    }

    /**
     * 获取已注册函数的数量
     */
    public int count() {
        return functions.size();
    }

    /**
     * 执行指定的业务函数
     */
    public BusinessFunction.FunctionCallResult executeFunction(String functionName, Map<String, Object> arguments) {
        Optional<BusinessFunction> functionOpt = getFunction(functionName);
        if (functionOpt.isPresent()) {
            BusinessFunction function = functionOpt.get();
            try {
                log.debug("Executing business function: {} with arguments: {}", functionName, arguments);
                BusinessFunction.FunctionCallResult result = function.getHandler().execute(arguments);
                log.debug("Function execution result: {}", result.isSuccess() ? "SUCCESS" : "FAILURE");
                return result;
            } catch (Exception e) {
                log.error("Error executing function: {}", functionName, e);
                return BusinessFunction.FunctionCallResult.failure(
                    String.format("函数执行失败: %s - %s", functionName, e.getMessage())
                );
            }
        } else {
            log.warn("Function not found: {}", functionName);
            return BusinessFunction.FunctionCallResult.failure(
                String.format("函数不存在: %s", functionName)
            );
        }
    }

    /**
     * 获取所有函数名称
     */
    public Set<String> getFunctionNames() {
        return new HashSet<>(functions.keySet());
    }
}