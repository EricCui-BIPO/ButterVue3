-- 修复clients表中的locationId引用，确保引用locations表中实际存在的位置ID

-- 更新clients表中的location_id，使用locations表中实际存在的位置ID
UPDATE clients SET location_id = 'country-us' WHERE location_id = 'location_us';
UPDATE clients SET location_id = 'country-cn' WHERE location_id = 'location_cn';
UPDATE clients SET location_id = 'country-kr' WHERE location_id = 'location_kr';
UPDATE clients SET location_id = 'country-jp' WHERE location_id = 'location_jp';
UPDATE clients SET location_id = 'country-nl' WHERE location_id = 'location_nl';