package com.i0.talent.domain.valueobjects;

import com.i0.talent.domain.exception.DomainException;

import java.util.Objects;

/**
 * 国籍值对象
 *
 * 封装员工国籍信息，仅支持COUNTRY类型的Location
 */
public class Nationality {

    /**
     * 国家地理位置ID
     */
    private final String countryId;

    /**
     * 国家名称（冗余存储，便于显示）
     */
    private final String countryName;

    /**
     * ISO代码（如CN、US等）
     */
    private final String isoCode;

    /**
     * 私有构造函数
     */
    private Nationality(String countryId, String countryName, String isoCode) {
        validate(countryId, countryName, isoCode);
        this.countryId = Objects.requireNonNull(countryId, "国籍国家ID不能为空");
        this.countryName = countryName;
        this.isoCode = isoCode;
    }

    /**
     * 创建国籍值对象
     *
     * @param countryId 国家地理位置ID
     * @param countryName 国家名称
     * @param isoCode ISO代码
     * @return 国籍值对象
     */
    public static Nationality of(String countryId, String countryName, String isoCode) {
        return new Nationality(countryId, countryName, isoCode);
    }

    /**
     * 从国家信息创建国籍值对象
     *
     * @param countryId 国家地理位置ID
     * @param countryName 国家名称
     * @return 国籍值对象
     */
    public static Nationality ofCountry(String countryId, String countryName) {
        return new Nationality(countryId, countryName, null);
    }

    /**
     * 验证国籍信息
     */
    private void validate(String countryId, String countryName, String isoCode) {
        if (countryId == null || countryId.trim().isEmpty()) {
            throw new DomainException("国籍国家ID不能为空");
        }

        if (countryName != null && countryName.trim().isEmpty()) {
            throw new DomainException("国籍国家名称不能为空字符串");
        }

        // ISO代码可以为空，但如果提供则验证格式
        if (isoCode != null && !isoCode.trim().isEmpty()) {
            if (!isoCode.matches("^[A-Za-z]{2,3}$")) {
                throw new DomainException("ISO代码格式不正确，应为2-3位字母");
            }
        }
    }

    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        return countryName != null ? countryName : countryId;
    }

    /**
     * 检查是否有效国籍
     */
    public boolean isValid() {
        return countryId != null && !countryId.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nationality that = (Nationality) o;
        return Objects.equals(countryId, that.countryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryId);
    }

    @Override
    public String toString() {
        return "Nationality{" +
                "countryId='" + countryId + '\'' +
                ", countryName='" + countryName + '\'' +
                ", isoCode='" + isoCode + '\'' +
                '}';
    }

    // Getters
    public String getCountryId() {
        return countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getIsoCode() {
        return isoCode;
    }
}