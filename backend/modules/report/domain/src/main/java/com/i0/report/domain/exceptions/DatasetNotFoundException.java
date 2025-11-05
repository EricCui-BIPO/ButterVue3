package com.i0.report.domain.exceptions;

/**
 * 数据集不存在异常
 */
public class DatasetNotFoundException extends ReportDomainException {

    public DatasetNotFoundException(String datasetId) {
        super("DATASET_NOT_FOUND", "数据集不存在: " + datasetId);
    }
}